<details>
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
</details> 5 ~ 6주차 과제 개요 

# Week 7 & 8. 과제 구현물 고도화

## 프로젝트 개요

Week 3 ~ 6에서 진행한 FE, BE 결과물에 대해 고도화를 진행했습니다. BE 고도화 과정에서 효율적이고 좋은 테스트란 무엇인가에 대해 고민하고, 이러한 생각을 바탕으로 실제 테스트 코드를 작성해보았습니다. 

또한, 교육 과정에서 배운 Docker와 CI/CD 내용을 바탕으로 다른 환경에서 실행하며 발생할 수 있는 "제 환경에서는 잘 되던데요?" 문제를 방지할 수 있도록 로컬 환경에서의 배포를 구현했습니다. 

## 기술 스택

### **Frontend**

- HTML, CSS, JavaScript (Vanilla JS)
- Fetch API를 활용한 서버 통신
- Frontend 결과물 repository [링크]

### **Backend**

- Spring Boot
- IntelliJ IDEA
- MySQL (데이터베이스)
- Gradle (빌드 도구)
- REST API 설계 및 구현
- Docker를 이용한 환경 구축
- Backend 결과물 repository [링크]

---

## 설치 및 실행 방법

### **1. 백엔드 실행**

### **1-1. 프로젝트 클론**

```
git clone https://github.com/100-hours-a-week/2-jun-nam-community-be.git
```

### **1-2. 환경 설정**

프로젝트의 root directory인 community_be 폴더에 `.env`을 생성해 다음과 같은 옵션을 추가합니다. 

```
MYSQL_ROOT_PASSWORD= your_password  //mysql root 비밀번호
MYSQL_DATABASE= your_db  //mysql에서 사용할 database의 이름. 프로젝트 실행 전 create database 명령어로 미리 만들어 두어야 합니다.

SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD= your_root_pasword
SPRING_DATASOURCE_DB= your_db
SPRING_DATASOURCE_HOST=mysql
SPRING_DATASOURCE_PORT=3306
SPRING_DATASOURCE_URL=jdbc:mysql://${SPRING_DATASOURCE_HOST}:${SPRING_DATASOURCE_PORT}/${SPRING_DATASOURCE_DB}

```

### **1-3. Docker & Docker Compose 설치

#### 🐳 Docker & Docker Compose 설치 방법

#### ✅ Windows (Windows 10 이상)

1. [Docker Desktop for Windows 다운로드](https://www.docker.com/products/docker-desktop)
2. 설치 과정에서 **WSL2** 활성화 권장 (WSL2가 설치되어 있지 않다면 자동으로 설치 안내)
3. Docker Desktop 설치 후, PC 재부팅
4. Docker Desktop 실행
5. PowerShell 또는 CMD에서 설치 확인
    ```bash
    docker --version
    docker compose version
    ```

##### 💡 참고
- Windows 10 Home도 WSL2를 통해 사용 가능
- WSL2 설치 방법: [Microsoft 공식 WSL2 가이드](https://learn.microsoft.com/ko-kr/windows/wsl/install)
- Docker Compose는 V2부터 `docker-compose` → `docker compose`로 통합됨  
  (둘 다 사용 가능하나, 가급적 `docker compose` 명령 사용 추천)


---

#### ✅ macOS (Intel & Apple Silicon)

1. [Docker Desktop for Mac 다운로드](https://www.docker.com/products/docker-desktop)
2. 본인의 Mac에 맞는 아키텍처 선택
    - Intel 칩 → Intel 버전 다운로드
    - M1/M2/M3 (Apple Silicon) → Apple Silicon 버전 다운로드
3. 설치 완료 후 Docker Desktop 실행
4. 터미널에서 설치 확인
    ```bash
    docker --version
    docker compose version
    ```

##### 💡 참고
- macOS Monterey 이상 권장
- Apple Silicon 환경에서도 공식 Docker Desktop 완전 지원
- Docker Compose 역시 `docker compose`로 사용


---

#### 🟣 설치 후 공통 확인 사항
- Docker Desktop 실행 후, 오른쪽 하단 / 상단 바에 고래 아이콘 🐳 표시
- 정상 동작 여부 확인
    ```bash
    docker run hello-world
    ```
    - 정상적으로 Hello from Docker! 메세지가 출력되면 완료

#### ⚠️ 주의사항
- **포트 충돌** 확인 (이미 다른 프로그램이 `3306`, `8080` 등을 점유할 수 있음)
- **방화벽/백신**이 Docker를 차단하지 않는지 확인
- Windows의 경우 Docker Desktop 설정에서 WSL2 Backend 설정 추천



### **1-4. 빌드 및 실행**

```
1. IntelliJ를 실행해 프로젝트 폴더에 있는 build.gradle 파일을 프로젝트로 열어줍니다.
2. 최초 실행을 통해 프로젝트 실행에 사용되는 dependency 파일들을 다운로드합니다.
3. 프로젝트 루트 폴더인 community_be에서 다음과 같은 명령어를 실행시킵니다. 
	docker compose up --build
4. docker ps 명령어를 통해 컨테이너의 상태를 확인합니다.
	community_be-v1, mysql 컨테이너가 띄워진 상태라면 정상적으로 프로젝트의 실행이 진행된 것입니다. 
5. 종료를 원할 시 docker compose down을 통해 종료시킵니다. 
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

### **3. 테스트 코드 커버리지

테스트 실행을 위해서 다음과 같은 과정을 수행해야 합니다

```
1. test는 8080포트, 3306 포트를 사용하므로 백엔드 프로젝트가 Docker를 통해 배포되고 있는 상태에서는 실행될 수 없습니다. 따라서, 우선 Docker desktop을 통해 실행되고 있는 community_be 컨테이너를 중지시킵니다.
2. /src/test/resources 폴더의 application-test.properties의 다음부분을 수정합니다.
	spring.datasource.username= your_mysql_username (default = root)
	spring.datasource.password= your_mysql_password (default = root password)
3. test는 test database를 사용합니다. 테스트 실행에 앞서 mysql에서 create database test를 통해 test db를 생성해줍니다. 
4. 프로젝트의 루트 폴더인 community_be 폴더에서  ./gradlew clean test jacocoTestReport 명령어를 실행시킵니다. 
5. /build/jacoco/index/index.html을 통해 테스트 커버리지를 확인합니다.
```

수행 결과 
<img width="1502" alt="스크린샷 2025-03-30 오후 11 01 31" src="https://github.com/user-attachments/assets/22be0701-c8c7-4e81-9fee-9f3fda4ed3c7" />


### **4. 프로젝트 시연 영상
[▶ 영상 보기](https://youtu.be/MZMSUfAWnyg)
