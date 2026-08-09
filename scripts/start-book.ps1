# ============================================================
# BookRecommendation 一键启动脚本
# 启动：MySQL(服务) -> Kafka 3.7 -> Spring Boot 后端 -> Spark Streaming -> Vue 前端
# 日志输出到本项目 logs/ 目录，停止请运行 stop-book.ps1
# ============================================================

$ErrorActionPreference = "Continue"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$LogDir = Join-Path $ProjectRoot "logs"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

# ---- 环境路径 ----
$Jdk17   = "D:\soft\program\Java\jdk-17.0.11"
$Jdk26   = "D:\soft\program\Java\jdk-26.0.1"
$Kafka   = "D:\soft\program\kafka_2.13-3.7.2"
$Spark   = "D:\soft\program\spark-3.5.2-bin-hadoop3-scala2.13"
$Hadoop  = "D:\soft\program\Hadoop"
$Hive    = "D:\soft\program\apache-hive-4.0.0-bin"

function Start-LoggedProcess {
    param(
        [string]$Name,
        [string]$File,
        [string[]]$ArgumentList,
        [string]$WorkingDir,
        [hashtable]$Env = @{}
    )
    $logOut = Join-Path $LogDir "$Name.out.log"
    $logErr = Join-Path $LogDir "$Name.err.log"
    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = $File
    $psi.Arguments = ($ArgumentList -join " ")
    $psi.WorkingDirectory = $WorkingDir
    $psi.UseShellExecute = $false
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.CreateNoWindow = $true
    foreach ($k in $Env.Keys) { $psi.EnvironmentVariables[$k] = [string]$Env[$k] }
    $p = [System.Diagnostics.Process]::Start($psi)
    # 后台异步写日志（避免管道阻塞）
    $outTask = $p.StandardOutput.ReadToEndAsync()
    $errTask = $p.StandardError.ReadToEndAsync()
    Write-Host "[start] $Name -> $logOut"
    return $p
}

# ---- 1. MySQL（Windows 服务，默认自启）----
$mysqlState = (sc.exe query MySQL | Select-String "RUNNING")
if ($mysqlState) {
    Write-Host "[ok] MySQL 服务运行中"
} else {
    Write-Host "[..] 启动 MySQL 服务..."
    sc.exe start MySQL | Out-Null
    Start-Sleep -Seconds 3
}

# ---- 2. Kafka（KRaft 单机，JDK 26，端口 9092）----
if (Get-NetTCPConnection -LocalPort 9092 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "[ok] Kafka 已在 9092 运行"
} else {
    Write-Host "[..] 启动 Kafka..."
    $env:JAVA_HOME = $Jdk26
    $kafkaArgs = @("-cp", "D:\soft\program\kafka_2.13-3.7.2\libs\*", "kafka.Kafka", "$Kafka\config\kraft\server.properties")
    Start-Process -FilePath (Join-Path $Jdk26 "bin\java.exe") -ArgumentList $kafkaArgs `
        -WorkingDirectory $Kafka `
        -RedirectStandardOutput (Join-Path $LogDir "kafka.out.log") `
        -RedirectStandardError (Join-Path $LogDir "kafka.err.log") `
        -WindowStyle Hidden
    Start-Sleep -Seconds 15
    if (-not (Get-NetTCPConnection -LocalPort 9092 -State Listen -ErrorAction SilentlyContinue)) {
        Write-Host "[err] Kafka 未就绪，查看 logs\kafka.log"
        exit 1
    }
    Write-Host "[ok] Kafka 9092 就绪"
}

# ---- 3. Hive metastore（真实 Hive 元数据服务，JDK 17，端口 9083）----
if (Get-NetTCPConnection -LocalPort 9083 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "[ok] Hive metastore 已在 9083 运行"
} else {
    Write-Host "[..] 启动 Hive metastore..."
    $hiveCp = "$Hive\conf;$Hive\lib\*;$Hadoop\share\hadoop\common\*;$Hadoop\share\hadoop\common\lib\*;$Hadoop\share\hadoop\hdfs\*;$Hadoop\share\hadoop\hdfs\lib\*;$Hadoop\share\hadoop\mapreduce\*;$Hadoop\share\hadoop\yarn\*;$Hadoop\share\hadoop\tools\lib\*"
    $msArgs = @("-cp", $hiveCp, "-Dhadoop.home.dir=D:/soft/program/Hadoop", "-Djava.library.path=D:/soft/program/Hadoop/bin", "org.apache.hadoop.hive.metastore.HiveMetaStore")
    Start-Process -FilePath (Join-Path $Jdk17 "bin\java.exe") -ArgumentList $msArgs `
        -WorkingDirectory $Hive `
        -RedirectStandardOutput (Join-Path $LogDir "hive-ms.out.log") `
        -RedirectStandardError (Join-Path $LogDir "hive-ms.err.log") `
        -WindowStyle Hidden
    Start-Sleep -Seconds 40
    if (-not (Get-NetTCPConnection -LocalPort 9083 -State Listen -ErrorAction SilentlyContinue)) {
        Write-Host "[err] Hive metastore 未就绪，查看 logs\hive-ms.err.log"
        exit 1
    }
    Write-Host "[ok] Hive metastore 9083 就绪"
}

# ---- 4. Spring Boot 后端（JDK 8，端口 8081）----
if (Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "[ok] 后端已在 8081 运行"
} else {
    Write-Host "[..] 启动后端..."
    $env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "mvnw.cmd -q spring-boot:run" `
        -WorkingDirectory (Join-Path $ProjectRoot "backend") `
        -RedirectStandardOutput (Join-Path $LogDir "backend.out.log") `
        -RedirectStandardError (Join-Path $LogDir "backend.err.log") `
        -WindowStyle Hidden
    Start-Sleep -Seconds 40
    if (-not (Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue)) {
        Write-Host "[err] 后端未就绪，查看 logs\backend.err.log"
    } else {
        Write-Host "[ok] 后端 8081 就绪"
    }
}

# ---- 5. Spark Streaming（JDK 17 + winutils + 真实 Hive metastore，消费 Kafka userLog）----
if (Get-NetTCPConnection -LocalPort 4040 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "[ok] Spark Streaming 已在 4040 运行"
} else {
    $jar = Join-Path $ProjectRoot "bigdata\target\recommend_bigdata-1.0.jar"
    if (-not (Test-Path $jar)) {
        Write-Host "[..] 编译 bigdata jar（首次）..."
        Push-Location (Join-Path $ProjectRoot "backend")
        cmd.exe /c "mvnw.cmd -q clean package -DskipTests -f ../bigdata/pom.xml" | Out-Null
        Pop-Location
    }
    Write-Host "[..] 启动 Spark Streaming..."
    $env:JAVA_HOME = $Jdk17
    $env:HADOOP_HOME = $Hadoop
    $sparkArgs = @(
        "--class", "com.hytc.bigdata.SparkStreamingRunner",
        "--conf", '"spark.driver.extraJavaOptions=-Dhadoop.home.dir=D:/soft/program/Hadoop -Djava.library.path=D:/soft/program/Hadoop/bin"',
        "--conf", '"spark.executor.extraJavaOptions=-Dhadoop.home.dir=D:/soft/program/Hadoop -Djava.library.path=D:/soft/program/Hadoop/bin"',
        $jar
    )
    Start-Process -FilePath (Join-Path $Spark "bin\spark-submit.cmd") -ArgumentList $sparkArgs `
        -WorkingDirectory (Split-Path $jar) `
        -RedirectStandardOutput (Join-Path $LogDir "spark.out.log") `
        -RedirectStandardError (Join-Path $LogDir "spark.err.log") `
        -WindowStyle Hidden
    Start-Sleep -Seconds 90
    if (-not (Get-NetTCPConnection -LocalPort 4040 -State Listen -ErrorAction SilentlyContinue)) {
        Write-Host "[err] Spark 未就绪，查看 logs\spark.log"
    } else {
        Write-Host "[ok] Spark Streaming 4040 就绪"
    }
}

# ---- 6. Vue 前端（端口 8080）----
if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "[ok] 前端已在 8080 运行"
} else {
    Write-Host "[..] 启动前端..."
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "npm run serve" `
        -WorkingDirectory (Join-Path $ProjectRoot "frontend") `
        -RedirectStandardOutput (Join-Path $LogDir "frontend.out.log") `
        -RedirectStandardError (Join-Path $LogDir "frontend.err.log") `
        -WindowStyle Hidden
    Start-Sleep -Seconds 30
    if (-not (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue)) {
        Write-Host "[err] 前端未就绪，查看 logs\frontend.err.log"
    } else {
        Write-Host "[ok] 前端 8080 就绪"
    }
}

Write-Host ""
Write-Host "========================"
Write-Host "BookRecommendation 已启动"
Write-Host "  前端   http://localhost:8080"
Write-Host "  后端   http://localhost:8081/book_recommendation"
Write-Host "  Spark  http://localhost:4040"
Write-Host "  日志   $LogDir"
Write-Host "停止请运行 scripts\stop-book.ps1"
Write-Host "========================"
