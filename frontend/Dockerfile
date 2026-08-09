# BookRecommendation 前端 Dockerfile（Node 构建 + nginx 托管）
FROM node:16-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
# 前端默认请求 http://localhost:8081/book_recommendation，nginx 代理到后端
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
