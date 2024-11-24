 #PostgreSQL 13 버전을베이스이미지로사용
 FROM mysql:8.0.33
 #/docker-entrypoint-initdb.d/에있는sql문은컨테이너가처음실행시자동실행됨
COPY ./init.sql /docker-entrypoint-initdb.d/
 #기본설정파일을덮어쓰기하여새로운설정적용
 #계정정보설정
ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=lolstagram
EXPOSE 3306