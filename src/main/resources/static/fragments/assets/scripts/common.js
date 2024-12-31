const $logout = document.getElementById('logout');

if ($logout) {
    $logout.addEventListener('click', (e) => {
        e.preventDefault();

        sessionStorage.clear();
        window.location.assign($logout.href);
    });
}

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