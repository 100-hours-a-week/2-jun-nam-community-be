# 최신 17-jdk-alpine 이미지로부터 시작
FROM openjdk:17-jdk-slim

# 작업 디렉토리를 /app으로 설정
WORKDIR /app

# 현재 디렉토리의 모든 파일을 컨테이너의 /app 디렉토리로 복사
COPY . .

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew
# 프로젝트 빌드
RUN ./gradlew clean build -x test

ENTRYPOINT [ "java","-jar","/app/build/libs/community-be-0.0.1-SNAPSHOT.jar" ]
#ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","app.jar"]