# 建置階段
FROM node:18 AS build
WORKDIR /app
# 複製 package 設定
COPY package*.json ./
# 安裝依賴
RUN npm install
# 複製所有程式碼
COPY . .
# 執行建置 (產生 dist 資料夾)
RUN npm run build

# 執行階段 (使用 Nginx 伺服器)
FROM nginx:alpine
# 將建置好的 dist 複製到 Nginx 目錄
COPY --from=build /app/dist /usr/share/nginx/html
# 複製 Nginx 設定 (可選，若有特殊路由需求)
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]