# BookRecommendation 前端一键截图（Docker 容器内运行 puppeteer）
#
# 背景：Windows 受限环境（沙箱）禁止 Chromium 的 Mojo 命名管道通信，
#       无头浏览器无法在宿主机直接运行；容器内无此限制。
#
# 构建：docker build -t book-shots .
# 运行（须在 compose 网络内访问 frontend 服务）：
#   docker run --rm --network bookrecommendation_booknet -v "<仓库>\docs\assets\screenshots:/shots" book-shots
# 输出：/shots 下 login / home / recommend / newbook / booklist / collection 共 6 张 PNG
FROM node:20-slim

# Chromium 系统依赖 + 中文字体（否则中文渲染为方块）
# 使用阿里云 Debian 镜像源（deb.debian.org 在国内常 502/超时）+ 拉取重试
RUN sed -i 's@deb.debian.org@mirrors.aliyun.com@g' /etc/apt/sources.list.d/debian.sources \
    && apt-get update -o Acquire::Retries=5 \
    && apt-get install -y --no-install-recommends -o Acquire::Retries=5 \
      ca-certificates fonts-liberation fonts-noto-cjk \
      libnss3 libatk-bridge2.0-0 libatk1.0-0 libcups2 libdbus-1-3 libdrm2 libgbm1 \
      libxkbcommon0 libxcomposite1 libxdamage1 libxfixes3 libxrandr2 libxrender1 libasound2 \
      libcairo2 libpango-1.0-0 libpangocairo-1.0-0 libxss1 libxtst6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY package.json ./
# 本地安装（含 postinstall 下载 Chrome for Testing）
RUN npm install

COPY shot.js .
ENTRYPOINT ["node", "/app/shot.js"]
