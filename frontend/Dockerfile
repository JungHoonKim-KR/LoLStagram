# 빌드 이미지로 node:14 지정
FROM node:14 AS build
WORKDIR /app

# 빌드 시 전달받을 환경 변수 정의
ARG REACT_APP_API_BASE_URL
ARG REACT_APP_OAUTH2_REDIRECT_URI

# React 빌드 환경 변수 설정
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL
ENV REACT_APP_OAUTH2_REDIRECT_URI=$REACT_APP_OAUTH2_REDIRECT_URI

# 소스 코드 복사 및 패키지 설치
COPY package*.json /app/
RUN npm install

COPY . /app
RUN npm run build

# 운영 환경용 이미지
FROM node:14-alpine

# serve 설치 (정적 파일 서빙용)
RUN npm install -g serve

WORKDIR /app
COPY --from=build /app/build /app

# 포트 3000 노출
EXPOSE 3000

# serve를 사용하여 정적 파일 제공
CMD ["serve", "-s", ".", "-l", "3000", "-H", "0.0.0.0"]



# 런타임이미지로nginx 1.21.4지정, /usr/share/nginx/html 폴더에권한추가
# FROM nginx:1.21.4-alpine
# COPY nginx.conf /etc/nginx/conf.d/default.conf
# # 빌드이미지에서생성된dist 폴더를nginx이미지로복사
# COPY --from=build /app/build /usr/share/nginx/html

# EXPOSE 80
# ENTRYPOINT ["/docker-entrypoint.sh"]
# CMD ["nginx", "-g", "daemon off;"]
