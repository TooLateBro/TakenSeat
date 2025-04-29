# 🎟️ 뮤지컬 & 콘서트 대규모 티켓팅 서비스 프로젝트

<div>
  <img src="https://github.com/user-attachments/assets/95785809-3949-4b04-a353-e1d958c3ff76" style="width: 100%; height: 450px;">
</div>

## 🗣️ 프로젝트 소개

- 실시간 대규모 트래픽에도 끊김 없는 예매 경험을 제공하는 고성능 공연 티켓팅 플랫폼
- 콘서트, 연극, 뮤지컬 등 다양한 공연에 대해 유저가 실시간으로 예매할 수 있는 티켓팅 시스템 구축

## 🛠️ 기술 스택

<table>
  <tr>
    <td><h3 style="margin: 0;">Database</h3></td>
    <td>MySQL 8.0 <br> Redis 7.2</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/MySQL-Light.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Redis-Light.svg" width="80">
    </td>
  </tr>

  <tr>
    <td><h3 style="margin: 0;">Backend / Framework</h3></td>
    <td>Spring Boot 3.4.4  <br> Kafka 2.8.1</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Spring-Light.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Kafka.svg" width="80">
    </td>
  </tr>

  <tr>
    <td><h3 style="margin: 0;">Cloud / DevOps</h3></td>
    <td>AWS <br> Docker <br> GitHub Actions</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/AWS-Light.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Docker.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/GithubActions-Light.svg" width="80">
    </td>
  </tr>

  <tr>
    <td><h3 style="margin: 0;">Monitoring / Observability</h3></td>
    <td>Prometheus <br> Grafana 3.3.0</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Prometheus.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Grafana-Light.svg" width="80">
    </td>
  </tr>

  <tr>
    <td><h3 style="margin: 0;">Collaboration / Tools</h3></td>
    <td>GitHub <br> Notion <br> Postman</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Github-Light.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Notion-Light.svg" width="80">
      <img src="https://github.com/tandpfun/skill-icons/blob/main/icons/Postman.svg" width="80">
    </td>
  </tr>

  <tr>
    <td><h3 style="margin: 0;">Test</h3></td>
    <td>JMeter</td>
  </tr>
  <tr>
    <td colspan="2">
      <img src="https://github.com/karpulix/jmeter-icon/blob/main/Icon.png" width="100">
    </td>
  </tr>
</table>
<br>

## 🗂️ 프로젝트 구조

```
TakenSeat                                                  # 루트 프로젝트
├──com.taken_seat.eureka-service                           # 서비스 디스커버리
│  ├──src/main/java/com/taken_seat/eureka_service
│  ├──Dockerfile
│
├──com.taken_seat.gateway-service                          # API 게이트 웨이
│  ├──src/main/java/com/taken_seat/gateway_service
│  │       ├──config/
│  │       ├──exception/
│  │       ├──filter/
│  │       ├──util/
│  ├──Dockerfile
│
├──com.taken_seat.auth-service                             # 유저 서비스 
│  ├──src/main/java/com/taken_seat/auth_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/auth_service
│  ├──Dockerfile
│
├──com.taken_seat.coupon-service                           # 쿠폰 서비스
│  ├──src/main/java/com/taken_seat/coupon_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/coupon_service
│  ├──Dockerfile
│
├──com.taken_seat.queue-service                            # 대기열 서비스
│  ├──src/main/java/com/taken_seat/queue_service
│  │   ├──application/
│  │   ├──infrastructure/
│  │   ├──presentation/    
│  ├──test/java/com/taken_seat/queue_service
│  ├──Dockerfile
│
├──com.taken_seat.booking-service                          # 예매 서비스
│  ├──src/main/java/com/taken_seat/booking_service
│  │   ├──booking/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──ticket/
│  │       ├──application/
│  │       ├──domain/
│  │       ├──infrastructure/
│  │       ├──presentation/
│  ├──common/
│  ├──test/java/com/taken_seat/booking_service
│  ├──Dockerfile
│
├──com.taken_seat.performance-service                      # 공연 서비스
│  ├──src/main/java/com/taken_seat/performance_service
│  │   ├──performance/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──performancehall/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──performanceticket/
│  │   │   ├──application/
│  │   │   ├──presentation/
│  ├──common/
│  ├──test/java/com/taken_seat/performance_service
│  ├──Dockerfile
│
├──com.taken_seat.payment-service                          # 결제 서비스
│  ├──src/main/java/com/taken_seat/payment_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/payment_service
│  ├──Dockerfile
│
├──com.taken_seat.review-service                           # 리뷰 서비스
│  ├──src/main/java/com/taken_seat/review_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/review_service
│  ├──Dockerfile
│
├──com.taken_seat.common-service                           # 공통 모듈
│  ├──prometheus/
│  ├──docker-compose.common.yml
│  ├──Dockerfile
│  ├──src/main/java/com/taken_seat/review_service
│  │   ├──component/
│  │   ├──config/
│  │   ├──dto/
│  │   ├──entity/
│  │   ├──exception/
│  │   ├──message/
│
├──loki-config.yml                                          # loki 설정
```

<br><br>

## 📖 서비스 아키텍처

<img src="https://github.com/user-attachments/assets/22d3b7e9-3fe0-4179-86a0-f07222781394" >

<br><br>

## ✏️ 이벤트 흐름

<img src="https://github.com/user-attachments/assets/6f4059fd-f91e-47b6-9404-7e5cc9909adc">

<br><br>

## ⁉️ 트러블 슈팅

- [정다예 - Grafana에서 Prometheus 메트릭이 404 뜨던 문제 해결](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EB%8B%A4%EC%98%88%5D-Grafana%EC%97%90%EC%84%9C-Prometheus-%EB%A9%94%ED%8A%B8%EB%A6%AD%EC%9D%B4-404-%EB%9C%A8%EB%8D%98-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [정다예 - Windows 환경에서 JVM CPU 메트릭 수집 불가 이슈](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EB%8B%A4%EC%98%88%5D-Windows-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-JVM-CPU-%EB%A9%94%ED%8A%B8%EB%A6%AD-%EC%88%98%EC%A7%91-%EB%B6%88%EA%B0%80-%EC%9D%B4%EC%8A%88)
- [전승현 - Spring Redis hashOps.keys()로 패턴 조회 안 되는 이유와 SCAN을 사용한 해결법](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%ED%98%84%5D--Spring-Redis-hashOps.keys()%EB%A1%9C-%ED%8C%A8%ED%84%B4-%EC%A1%B0%ED%9A%8C-%EC%95%88-%EB%90%98%EB%8A%94-%EC%9D%B4%EC%9C%A0%EC%99%80-SCAN%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%9C-%ED%95%B4%EA%B2%B0%EB%B2%95)
- [전승현 - 결제 도메인에서 마일리지 쿠폰 차감 책임을 분리한 이유](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%ED%98%84%5D-%EA%B2%B0%EC%A0%9C-%EB%8F%84%EB%A9%94%EC%9D%B8%EC%97%90%EC%84%9C-%EB%A7%88%EC%9D%BC%EB%A6%AC%EC%A7%80-%EC%BF%A0%ED%8F%B0-%EC%B0%A8%EA%B0%90-%EC%B1%85%EC%9E%84%EC%9D%84-%EB%B6%84%EB%A6%AC%ED%95%9C-%EC%9D%B4%EC%9C%A0)
- [백승규 - access_token을 BlackList로 관리하면서 만료시간을 설정하는 과정에서 깨달음](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%EA%B7%9C%5D-access_token%EC%9D%84-BlackList%EB%A1%9C-%EA%B4%80%EB%A6%AC%ED%95%98%EB%A9%B4%EC%84%9C-%EB%A7%8C%EB%A3%8C%EC%8B%9C%EA%B0%84%EC%9D%84-%EC%84%A4%EC%A0%95%ED%95%98%EB%8A%94-%EA%B3%BC%EC%A0%95%EC%97%90%EC%84%9C-%EA%B9%A8%EB%8B%AC%EC%9D%8C)

<br><br>

## 📌 팀원 역할분담

|                                        🫅 Leader                                         |                                        👷 Member                                         |                                        👷 Member                                         |                                        👷 Member                                         |                                        👷 Member                                        |   
|:----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/128787964?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/140582940?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/155501200?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/100333239?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/81623522?v=4" width="120px;" alt=""/> |
|                            [전승현](https://github.com/jjsh0208)                            |                           [백승규](https://github.com/seungg8361)                           |                           [정다예](https://github.com/Jungdaye89)                           |                           [강성준](https://github.com/Goldbar97)                            |                           [이채연](https://github.com/dkki4887)                            
|                              BE / Payment / Review / DevOps                              |                               BE / Auth / Coupon / Mileage                               |                                  BE / Performance /  /                                   |                                  BE / Booking / Ticket                                   |                                      BE / Queue /                                       |

<br>
