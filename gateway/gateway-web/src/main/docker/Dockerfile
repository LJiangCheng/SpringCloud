FROM java:alpine
VOLUME /tmp
ADD gateway-web-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","-Xms300m","-Xmx300m","/app.jar"]
