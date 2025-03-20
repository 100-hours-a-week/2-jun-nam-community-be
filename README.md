# **Week 5 & 6. ERD 설계 및 FE와 BE 연동**

## 프로젝트 개요

Week 3 - 4에서 작업한 FE 결과물을 서버에 연걸하는 작업을 진행했습니다. 이를 위해 데이터를 DB 상에 저장하기 위해 ERD 설계를 진행했습니다. 이후, 설계된 ERD를 바탕으로 Spring Boot를 통해 BE를 구성했고, FE와 연동하여 각 기능들이 정상적으로 저장되고, 의도한 데이터들이 DB 상에 저장 및 반영 되는지를 확인했습니다. 

## 기술 스택

### **Frontend**

- HTML, CSS, JavaScript (Vanilla JS)
- Fetch API를 활용한 서버 통신

### **Backend**

- Spring Boot
- IntelliJ IDEA
- MySQL (데이터베이스)
- Gradle (빌드 도구)
- REST API 설계 및 구현

---

## 설치 및 실행 방법

### **1. 백엔드 실행**

### **1-1. 프로젝트 클론**

```
git clone https://github.com/100-hours-a-week/2-jun-nam-community-be.git
```

### **1-2. 환경 설정**

`.env` 또는 `application.properties` 파일을 생성하고, 다음과 같이 설정을 추가합니다.

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
file.upload-dir=uploads/

```

### **1-3. 빌드 및 실행**

```
1. IntelliJ를 실행해 프로젝트 폴더에 있는 build.gradle 파일을 프로젝트로 열어줍니다.
2. 최초 실행을 통해 프로젝트 실행에 사용되는 dependency 파일들을 다운로드합니다.
3. community_be/src/main/java/hello.hello_spring/HelloApplication 을 실행해 Spring Boot 프로젝트를 실행시킵니다. 실행이 정상적으로 완료되었다면, Spring Boot에 내장된 Tomcat 서버를 통해 로컬 환경에서 이용할 수 있는 서버가 실행된 상태입니다. 
```

---

### **2. 프론트엔드 실행**

### **2-1. 로컬 서버 실행**

백엔드가 정상적으로 실행된 후, `http://localhost:8080` 에 접속했을 때 다음과 같은 화면이 나오면 정상적으로 실행된 것입니다. 

### **2-2. API 요청 예제**

프론트엔드에서는 `fetch` API를 활용하여 백엔드와 통신합니다.

```
const response = await fetch('http://localhost:8080/api/data')
 
 if(!response.ok)
 {
	 throw new Error(,,,)
 }
```

---

## 주요 기능

### **백엔드**

- REST API 엔드포인트 구현
- MySQL과 연동하여 데이터 저장 및 관리
- 프론트엔드 요청 처리

### **프론트엔드**

- 백엔드 API와의 연동
- 사용자 입력 데이터 전송 및 응답 데이터 반영
- 오류 처리 및 UI 업데이트

---

## 📂 프로젝트 구조

```
community_be/
├── src/main/java/hello/hello_spring
│   ├── controller/   # API 엔드포인트
│   ├── service/      # 비즈니스 로직
│   ├── repository/   # DB 연동
│   ├── model/        # 엔티티
│   └── Application.java  # 메인 클래스
│   └── WebConfig.java # 설정 파일
└── build.gradle
```

---