 # 빌드이미지로node:14지정
 FROM node:14 AS build
 WORKDIR /app
 # 빌드컨텍스트의소스코드를작업디렉토리로복사, 라이브러리설치및빌드
COPY . /app
 RUN npm ci
 RUN npm run build
 # 런타임이미지로nginx 1.21.4지정, /usr/share/nginx/html 폴더에권한추가
FROM nginx:1.21.4-alpine
 # 빌드이미지에서생성된dist 폴더를nginx이미지로복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 70
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]