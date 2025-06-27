# SSAFitLife
 
건강한 라이프스타일을 위한 종합 건강관리 웹 서비스

## 📋 프로젝트 소개

SSAFitLife는 사용자의 일일 활동량과 식단을 종합적으로 관리할 수 있는 웹 서비스입니다. 개인화된 기초대사량 계산을 기반으로 정확한 칼로리 관리를 지원하며, 체계적인 식단 관리 기능을 제공합니다.

## ✨ 주요 기능

**체중 관리**
- 일일 체중 기록 및 차트 시각화
- 개인별 기초대사량 자동 계산
- 체중 기록을 통한 체중 분석 기능

**활동량 관리**
- 일일 활동량 기록
- 활동별 소모 칼로리 계산
- 홛동 검색 및 추가 기능


**식단 관리**
- 끼니별 식단 기록 (아침/점심/저녁/간식)
- 실시간 칼로리 소모량 업데이트
- 음식 검색 및 추가 기능
- 영양소 비율 분석 (탄수화물, 단백질, 지방)
- 일일 섭취 칼로리 계산

**게시판**
- 자유게시판
- 페이징 기능
- 검색 기능

**제품 추천**
- 헬스 보조제 추천
- 닭가슴살 추천

# 👨🏻‍💻 담당 업무 (Full-Stack & Team Lead)

## :bust_in_silhouette: 이대현

* **Back-End API 개발 (`Spring Boot`)**
    * 사용자 활동(체중, 식단, 운동) 기록 및 관리 기능 API 개발.
    * 입력된 사용자 데이터(활동량, 섭취 칼로리 등) 기반 활동 대사량 계산 로직 구현.
    * 계산된 대사량 및 사용자 목표(체중 감량/증량 등)를 고려한 식단 추천 로직 구현.
    * 기록된 데이터를 바탕으로 한 통계 API 개발 및 제공.

* **사용자 인증 및 보안 (`Spring Security`, `JWT`)**
    * `Spring Security`와 `JWT`(JSON Web Token)를 활용하여 안전한 사용자 로그인 인증 및 API 접근 권한 관리 시스템 구축.
    * 토큰 기반 인증 필터 구현 및 Security 설정 커스터마이징.

* **데이터베이스 설계 및 구현 (`MySQL`)**
    * 서비스 요구사항 분석 기반의 데이터베이스 스키마 설계 (ERD 작성 포함 가능).
    * `MySQL`을 사용하여 테이블 생성 및 데이터 관계 설정.

* **Front-End 연동 및 UX 개선 (`Vue.js`, `Chart.js`)**
    * 개발된 Back-End API 명세 정의 및 `Vue.js` 기반 Front-End와의 데이터 연동 작업.
    * `Chart.js` 라이브러리를 활용하여 사용자의 체중 변화, 영양소 섭취 비율 등 통계 데이터를 시각화하여 사용자 경험 개선.

* **팀 리딩 및 프로젝트 관리**
    * 2인 팀의 리더로서 프로젝트 목표 설정 및 개발 방향 조율.
    * 개발 일정 계획 수립 및 진행 상황 관리.
    * `GitHub`를 활용한 소스 코드 버전 관리 및 협업 진행.

## 🛠 기술 스택

**Frontend**
- Vue.js
- Vuex (상태관리)
- Vue Router
- Axios (HTTP 클라이언트)
- Chart.js (데이터 시각화)

**Backend**
- Java
- Spring Boot
- Spring Security
- JWT (Access Token + Refresh Token)
- MyBatis (ORM)

**Database**
- MySQL

## 🔒 보안

- Spring Security를 통한 안전한 인증 구현
- JWT 기반의 토큰 관리
  - Access Token
  - Refresh Token

## 📌 프로젝트 관리

- 프로젝트 문서화: Notion
- 버전 관리: Git
- 협업 도구: GitHub


## 👥 팀 구성원

- 이대현 : 팀장, DB 설계 및 구현, 백엔드 아키텍처 설계, 체중관리, 활동관리, 식단관리 구현
- 함동건 : UI/UX 디자인, 메인화면, 게시판, 제품추천 구현

## 📱 화면 구성

![메인화면](https://github.com/user-attachments/assets/36b4e2a8-9a31-4c80-85e1-58a285bdc2f5)

![체중관리](https://github.com/user-attachments/assets/de5c5513-fd8e-421c-89ee-06c33d4644d1)

![활동관리](https://github.com/user-attachments/assets/4715e55a-6bd1-4da0-82d9-0a5eb7e73950)

![활동추가-목록](https://github.com/user-attachments/assets/f4b7dbab-61e0-493b-bcb4-1e261812a1f2)

![식단짜기](https://github.com/user-attachments/assets/5c7d8276-009d-4337-892a-4d7d93833ae4)

![식단추가-목록](https://github.com/user-attachments/assets/4f88d577-6dd8-4728-8c21-0114d4a5c614)

![게시판](https://github.com/user-attachments/assets/69eba481-2140-4835-adcf-98284bd837ec)

![제품추천](https://github.com/user-attachments/assets/a230dd95-3b3c-420b-8842-31da89a2bd40)

