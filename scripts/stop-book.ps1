# ============================================================
# BookRecommendation 一键停止脚本
# 停止：前端(8080) -> 后端(8081) -> Spark(4040) -> Kafka(9092)
# MySQL 服务保留（系统服务，不停止）
# ============================================================

$ErrorActionPreference = "SilentlyContinue"

function Stop-Port {
    param([int]$Port, [string]$Name)
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        $pids = $conn.OwningProcess | Sort-Object -Unique
        foreach ($pid in $pids) {
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Write-Host "[stop] $Name (端口 $Port, PID $pid)"
        }
    } else {
        Write-Host "[ok] $Name (端口 $Port) 未运行"
    }
}

Stop-Port 8080 "前端"
Stop-Port 8081 "后端"
Stop-Port 4040 "Spark Streaming"
Stop-Port 9092 "Kafka"
Stop-Port 9083 "Hive metastore"

# 兜底：结束后端 mvnw 子进程（8081 已处理，这里清理残留 java/mvn 包装进程）
Get-CimInstance Win32_Process -Filter "Name='java.exe'" -ErrorAction SilentlyContinue |
    Where-Object { $_.CommandLine -match "BookRecommendation" } |
    ForEach-Object {
        Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
        Write-Host "[stop] BookRecommendation java 进程 PID $($_.ProcessId)"
    }

Write-Host ""
Write-Host "BookRecommendation 已全部停止（MySQL 服务保留）"
