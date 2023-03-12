FROM openjdk:11

COPY app/target/purchasems.jar /target/purchasems.jar

EXPOSE 8080

CMD ["java","-jar","-Dspring.profiles.active=deploy","/target/purchasems.jar"]