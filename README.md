# 🎬 CGV 클론코딩
<img src="https://newsroom.etomato.com/userfiles/20240401_112826_116930170.jpg">

## 프로젝트 소개
- CGV의 주요 기능을 클론하여 영화 예매 웹사이트 제작

## 팀원 구성

| <a href="https://github.com/Nyeonjae"><img src="https://avatars.githubusercontent.com/u/185048916?v=4" width="80"></a> | <a href="https://github.com/geniyuls"><img src="https://avatars.githubusercontent.com/u/181185824?v=4" width="80"></a> | <a href="https://github.com/escurse"><img src="https://avatars.githubusercontent.com/u/180259666?v=4" width="80"></a> | <a href="https://github.com/kimjonguk1"><img src="https://avatars.githubusercontent.com/u/148532342?v=4" width="80"></a> |
|:-----:|:-----:|:-----:|:-----:|
| :crown:전현재 | 김종율 | 박재형 | 김종욱 |
| 회원(회원가입, 마이페이지) | 예매, 결제, 좌석, 예매내역  | 예매(영화선택), 상영정보, 영화관, 상영관 | 영화정보, 영화/인물 검색, 리뷰(평점) |

## 개발 환경
| **기술 스택** | **설명**                     |
|:-------------:|------------------------------|
|  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">           | 웹 페이지 구조 설계          |
| <img src="https://img.shields.io/badge/Scss-green?style=flat&logo=Sass&logoColor=CC6699"/>        | CSS 전처리기로 스타일 관리    |
| <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">    | 동적 기능 및 프론트엔드 로직 |
| <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">          | 백엔드 애플리케이션 개발     |
| <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">    | 백엔드 프레임워크 사용       |

## 개발 기간
- 2024.12.03 ~ 2025.01.03

## 주요 기능
### 사용자 관리
- /user/register -> 회원가입 ( 아이디, 닉네임 중복검사 ) 
- /user/login -> 사용자가 아이디, 비밀번호로 로그인
- /user/find-id -> 사용자가 입력한 이메일, 연락처 값으로 아이디 조회 
- /user/find-password -> 사용자가 아이디, 이메일 인증을 통해 비밀번호를 찾는 기능
- /user/myPage/main -> 사용자가 예매한 영화 내역 결제 및 리뷰 작성 내역 확인
- /user/myPage/personal -> 사용자의 비밀번호 인증후, 회원 정보 수정 및 회원 탈퇴 페이지로 이동 
- /user/myPage/modify -> 회원 정보 수정 ( 닉네임, 비밀번호 )
- /user/myPage/userWithdraw -> 회원 탈퇴 기능 
- /user/myPage/reservation -> 사용자가 예매한 영화 결제 취소 내역


### 영화 목록 및 상세 정보
- /movies/movieList -> 현재 상영작 및 상영예정작 표시 (포스터, 영화 제목, 개봉일 표시)
- /movies/movieList/movieInfo -> 특정 영화의 상세 정보를 확인 (주요정보, 출연진, 리뷰, 상영시간표 확인)

### 영화 및 인물 검색
- /movies/search -> 특정 영화를 검색하거나 특정 인물을 검색

### 상영 일정
- /theater/ -> 극장의 정보 및 특정 극장에서 상영 중인 영화의 시간표를 확인
- /ticket/showTimes -> 특정 극장에서 상영 중인 영화의 시간표 및 특정 영화마다 상영 중인 극장의 시간표 확인.

### 좌석 선택 및 예매
- /ticket/ -> 사용자가 영화, 영화관, 상영관, 상영 시간과 좌석 선택 (예약된 좌석과 남은 좌석 표시) 및 결제
- /ticket/reservation -> 사용자가 선택하여 예매한 내역 확인

### 관리자 페이지
- /admin/is_admin?mode=movie -> 영화 수정 및 삭제
- /admin/crawling -> JSOUP 라이브러리를 활용하여 영화 목록 크롤링(cgv)

## ERD
<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Fbba77931-e5b0-47ad-bf2b-0d24647cd0c4%2F42ec2d71-2054-41c3-bab0-73d9ce079b5b%2Ferd.png/size/w=1380?exp=1737181515&sig=Y3AUkHhScoZihlG0uJdmfolROwdmu2Bpol70oqtyaRs">

## 노션 링크
<a href="https://tricky-bloom-e01.notion.site/Jongyul-Pirates-Movie-Theater-130221d78586808aa580cbaf5e7a7e6f?pvs=4" target="_blank">
  <img src="https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png" alt="Notion" width="80">
</a>

## [JGV] 공식 웹사이트 링크
https://jgv.jwkim.dev/
