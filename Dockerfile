FROM maven:3.9.6-amazoncorretto-21 AS stage1
COPY pom.xml /app/
COPY src /app/src
WORKDIR /app
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine AS stage2
EXPOSE 8080
COPY --from=stage1 /app/target/*.jar core-customer.jar
ENTRYPOINT ["java", "-jar", "/core-customer.jar"]