# 빌드이미지로node:14지정
FROM node:14 AS build
WORKDIR /app

# 빌드 시 전달받을 환경 변수 정의
ARG REACT_APP_API_BASE_URL
ARG REACT_APP_OAUTH2_REDIRECT_URI

# React 빌드 환경 변수 설정
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL
ENV REACT_APP_OAUTH2_REDIRECT_URI=$REACT_APP_OAUTH2_REDIRECT_URI

# 빌드컨텍스트의소스코드를작업디렉토리로복사, 라이브러리설치및빌드
COPY package*.json /app/
RUN npm install

COPY . /app
RUN npm run build
# 런타임이미지로nginx 1.21.4지정, /usr/share/nginx/html 폴더에권한추가
FROM nginx:1.21.4-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
# 빌드이미지에서생성된dist 폴더를nginx이미지로복사
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
