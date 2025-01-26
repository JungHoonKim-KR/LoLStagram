## 배포 주소 : http://ec2-3-39-119-168.ap-northeast-2.compute.amazonaws.com:3030/ <br>
## 임시 모니터링 : [http://ec2-3-39-119-168.ap-northeast-2.compute.amazonaws.com:3000/](http://ec2-3-39-119-168.ap-northeast-2.compute.amazonaws.com:3000/d/spring_boot_21/spring-boot-2-1-system-monitor?orgId=1&refresh=5s)
주의 사항 필독 ! 
- 첫 로그인 시 네트워크 환경마다 시간 차이가 많이 발생합니다. 평균 4초 정도 소요됩니다.
- 회원가입 시 닉네임과 태그명의 대소문자나 공백을 잘 확인해주세요.



## 🎮 LoLStagram 
LoLStagram은 목표로 하는 플레이어를 등록하거나 검색하여 해당 플레이어의 전적을 확인하고 동기를 얻으며, 인상 깊었던 오늘의 플레이를 공유할 수 있는 플랫폼입니다.

## 📌 프로젝트 목표
- Riot Games Open API를 활용해 실시간 데이터를 수집 및 처리하며, 외부 API 연동 경험을 쌓습니다.

- **Java(Spring Boot, JPA, Spring Security)** 와 **JavaScript(React)** 를 통해 백엔드와 프론트엔드 개발 역량을 강화합니다.

- 효율적인 데이터 처리와 시스템 설계를 통해 유지보수성과 확장성을 고려한 애플리케이션을 제작합니다.

## 🛠️ 기술 스택
**Backend**<br>
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Redis

**Database**<br>
- MySQL
- Redis
  
**Frontend**<br>
- React: 사용자 인터페이스 설계 및 구현

**DevOps**<br>
- Docker: 컨테이너화를 통한 일관된 개발 및 배포 환경 제공
- CI/CD : Git Actions + Docker
  -  CI 단계: 코드 푸시 -> 빌드 -> Docker Hub에 배포.
  -  CD 단계: Docker Hub에서 새로운 이미지를 Pull -> EC2에서 컨테이너 재배포.

 **서버**<br>
 - EC2, S3 버킷
 
## 📋 주요 기능
1. 플레이어 등록 및 검색
- 플레이어 닉네임을 등록하거나 검색하여 전적과 통계를 확인
2. 데이터 시각화
- 챔피언, 룬, 스펠, 아이템 데이터를 기반으로 플레이어의 경기 결과를 시각적으로 표시
3. 오늘의 플레이 공유
- 사용자가 선택한 경기를 저장하고 공유

