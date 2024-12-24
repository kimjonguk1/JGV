# 🎬 CGV 클론코딩
<img src="https://newsroom.etomato.com/userfiles/20240401_112826_116930170.jpg">

## 프로젝트 소개
- CGV의 주요 기능을 클론하여 영화 예매 웹사이트 제작

## 팀원 구성

| <a href="https://github.com/Nyeonjae"><img src="https://avatars.githubusercontent.com/u/185048916?v=4" width="80"></a> | <a href="https://github.com/geniyuls"><img src="https://avatars.githubusercontent.com/u/181185824?v=4" width="80"></a> | <a href="https://github.com/escurse"><img src="https://avatars.githubusercontent.com/u/180259666?v=4" width="80"></a> | <a href="https://github.com/kimjonguk1"><img src="https://avatars.githubusercontent.com/u/148532342?v=4" width="80"></a> |
|:-----:|:-----:|:-----:|:-----:|
| :crown:전현재 | 김종율 | 박재형 | 김종욱 |
| 회원(회원가입, 마이페이지) | 예매, 좌석 | 상영정보, 영화관 | 영화정보, 영화 검색, 인물 검색, 리뷰(평점) |

## 개발 환경
| **기술 스택** | **설명**                     |
|:-------------:|------------------------------|
|  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">           | 웹 페이지 구조 설계          |
| <img src="https://img.shields.io/badge/Scss-green?style=flat&logo=Sass&logoColor=CC6699"/>        | CSS 전처리기로 스타일 관리    |
| <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">    | 동적 기능 및 프론트엔드 로직 |
| <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">          | 백엔드 애플리케이션 개발     |
| <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">    | 백엔드 프레임워크 사용       |

## 개발 기간
- 2024.12.03 ~

## 주요 기능
### 사용자 관리
- /사용자/회원가입 -> 사용자가 아이디 비밀번호로 회원가입
- /사용자/로그인 -> 사용자가 아이디, 비밀번호로 로그인
- /사용자/프로필 -> 사용자가 작성한 리뷰(평점)확인, 예매 내역 확인
- /사용자/프로필 수정 -> 사용자가 프로필 정보 수정

### 영화 목록 및 상세 정보
- /영화/목록조회 -> 현재 상영작 표시 (포스터, 영화 제목, 개봉일 표시)
- /영화/상세조회 -> 특정 영화의 상세 정보를 확인 (줄거리, 감독/출연진 정보, 관람 등급, 영화 장르, 제작국가 표시)
- /영화/검색 -> 특정 영화를 검색하거나 특정 인물을 검색
- /관리자/크롤링 -> 영화 데이터를 cgv 공식 홈페이지에서 크롤링

### 상영 일정
- /상영시간표/조회 -> 특정 극장에서 상영 중인 영화의 시간표를 확인

### 좌석 선택 및 예매
- /예매/좌석선택 -> 사용자가 상영 시간과 좌석 선택 (예약된 좌석과 남은 좌석 표시)
- /예매/결제 -> 사용자가 선택한 예매를 결제

## ERD
<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Fbba77931-e5b0-47ad-bf2b-0d24647cd0c4%2Fc9bcf8ca-95a3-4400-bea5-3b8252e163e2%2Ferd.png/size/w=1420?exp=1733387405&sig=NANjttxYuuVZxz6YQJ32RL4lcnW3ZA_sv9co8MMLEww">

## 노션 링크
<a href="https://tricky-bloom-e01.notion.site/Jongyul-Pirates-Movie-Theater-130221d78586808aa580cbaf5e7a7e6f?pvs=4" target="_blank">
  <img src="https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png" alt="Notion" width="80">
</a>
