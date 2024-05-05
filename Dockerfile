FROM openjdk
COPY target/*.jar BillReader.jar
ENTRYPOINT ["java","-jar","BillReader.jar"]