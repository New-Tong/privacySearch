FROM openjdk:17-ea-33-oracle
# 工作目录
WORKDIR /app
#挂载目录
VOLUME /logs
VOLUME /data
#对外开放接口
EXPOSE 9221

ENV APP_DIR=/app
RUN ["mkdir", "${APP_DIR}"]
ENV TIME_ZONE=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/Asia/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone
COPY ./target/private_communication-1.0-SNAPSHOT.jar /app/private_communication-1.0-SNAPSHOT.jar

COPY ./start.sh /app/start.sh
RUN chmod +x /app/start.sh

CMD [ "/app/start.sh" ]
