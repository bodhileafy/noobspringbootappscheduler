FROM openjdk:8u252-slim
WORKDIR /opt
COPY ./build/libs/opman-1.0.jar /opt/opman/opman-1.0.jar
ENTRYPOINT ["java","-jar","/opt/opman-1.0.jar"]