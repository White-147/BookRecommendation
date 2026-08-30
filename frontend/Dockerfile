# BookRecommendation 前端 Dockerfile（Node 构建 + nginx 托管）
FROM node:16-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
# docker 模式（.env.docker）下 baseURL 为相对路径 /book_recommendation，经 nginx 代理到 compose 后端；
# GitHub Pages 部署仍用默认 production 模式（.env.production -> Render 后端）
RUN npm run build -- --mode docker

FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
# 前端默认请求 http://localhost:8081/book_recommendation，nginx 代理到后端
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
