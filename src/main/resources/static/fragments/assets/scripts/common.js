const $logout = document.getElementById('logout');

if ($logout) {
    $logout.addEventListener('click', (e) => {
        e.preventDefault();

        sessionStorage.clear();
        location.href = $logout.href;
    });
}

// 주석 처리한 부분이 오류가 뜸 (null)

// const $movieCrawl = document.getElementById('movie-crawl-button');
//
// $movieCrawl.onclick = () => {
//     window.open(
//         "http://localhost:8080/admin/movie-crawl",
//         "영화 정보 크롤링",
//         "width=600,height=800,left=200,top=200"
//     );
// };
//
// const $theaterCrawl = document.getElementById('theater-crawl-button');
//
// $theaterCrawl.onclick = () => {
//     window.open(
//         "http://localhost:8080/admin/theater-crawl",
//         "상영 정보 크롤링",
//         "width=600,height=800,left=200,top=200"
//     );
// };
//
// {
//     const $titleChange = document.getElementById('admin-title-container');
//     const $buttonWrapper = $titleChange.querySelector(':scope > .admin-button-wrapper');
//     const $movieInfo = $titleChange.querySelector(':scope > .movie-information');
//     const $theaterInfo = $titleChange.querySelector(':scope > .theater-information');
//     const $movieButton = document.getElementById('movie-crawl-button');
//     const $theaterButton = document.getElementById('theater-crawl-button');
//     const $movieMain = document.getElementById('movie-admin-page');
//     const $theaterMain = document.getElementById('theater-admin-page');
//
//     $movieInfo.onclick = () => {
//         $movieButton.style.display = 'block';
//         $movieInfo.style.color = '#000000';
//         $movieMain.style.display = 'block';
//         $theaterButton.style.display = 'none';
//         $theaterInfo.style.color = '#666666';
//         $theaterMain.style.display = 'none';
//         $buttonWrapper.style.border = '2px solid #fb4357';
//     }
//
//     $theaterInfo.onclick = () => {
//         $movieButton.style.display = 'none';
//         $movieInfo.style.color = '#666666';
//         $movieMain.style.display = 'none';
//         $theaterInfo.style.color = '#000000';
//         $theaterButton.style.display = 'block';
//         $theaterMain.style.display = 'block';
//         $theaterButton.style.backgroundColor = '#2275a4';
//         $theaterButton.style.border = '1px solid #2275a4';
//         $buttonWrapper.style.border = '2px solid #2275a4'
//     }
// }

{
    const $form = document.querySelector('.nav > .nav-wrapper > .search-wrapper > .search-form')
    const $searchKeyword = $form.querySelector('.search > .keyword');


    $form.onsubmit = (e) => {
        e.preventDefault(); // 기본 폼 제출 동작 방지

        if ($searchKeyword.value === '') {
            alert("검색어를 입력해 주세요");
            return;
        }

        // GET 요청: 검색 키워드를 쿼리 파라미터로 전달
        const searchUrl = `/movies/search?keyword=${encodeURIComponent($searchKeyword.value)}`;
        const xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status >= 200 && xhr.status < 300) {
                // 성공적으로 응답을 받은 경우
                console.log("응답 성공:", xhr.responseText);
                // 브라우저를 해당 URL로 리다이렉트
                window.location.href = searchUrl;
            } else {
                alert("서버가 알수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.");
            }
        };

        xhr.open('GET', searchUrl);
        xhr.send();
    };
}