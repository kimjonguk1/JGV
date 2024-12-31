const $movieCrawl = document.getElementById('movie-crawl-button');

$movieCrawl.onclick = () => {
    window.open(
        "http://localhost:8080/admin/movie-crawl",
        "영화 정보 크롤링",
        "width=600,height=800,left=200,top=200"
    );
};

const $theaterCrawl = document.getElementById('theater-crawl-button');

$theaterCrawl.onclick = () => {
    window.open(
        "http://localhost:8080/admin/theater-crawl",
        "상영 정보 크롤링",
        "width=600,height=800,left=200,top=200"
    );
};


{
    const $titleChange = document.getElementById('admin-title-container');
    const $buttonWrapper = $titleChange.querySelector(':scope > .admin-button-wrapper');
    const $movieInfo = $titleChange.querySelector(':scope > .movie-information');
    const $theaterInfo = $titleChange.querySelector(':scope > .theater-information');
    const $movieButton = document.getElementById('movie-crawl-button');
    const $theaterButton = document.getElementById('theater-crawl-button');
    const $movieMain = document.getElementById('movie-admin-page');
    const $theaterMain = document.getElementById('theater-admin-page');
    const $movieSearchForm = document.getElementById('movie-search-form');
    const $theaterSearchForm = document.getElementById('theater-search-form');
    const $moviePage = document.getElementById('movie-page-container');
    const $theaterPage = document.getElementById('theater-page-container');

    $movieInfo.onclick = () => {
        $movieButton.style.display = 'block';
        $movieInfo.style.color = '#000000';
        $movieMain.style.display = 'block';
        $movieSearchForm.style.display = 'block';
        $theaterButton.style.display = 'none';
        $theaterInfo.style.color = '#666666';
        $theaterMain.style.display = 'none';
        $theaterSearchForm.style.display = 'none';
        $buttonWrapper.style.border = '2px solid #fb4357';
        $moviePage.style.display = 'block';
        $theaterPage.style.display = 'none';

        const url = new URL(location.href);
        url.searchParams.set('mode', 'movie');
        history.pushState(undefined, undefined, url.toString());
    }

    $theaterInfo.onclick = () => {
        $movieButton.style.display = 'none';
        $movieInfo.style.color = '#666666';
        $movieMain.style.display = 'none';
        $movieSearchForm.style.display = 'none';
        $theaterInfo.style.color = '#000000';
        $theaterButton.style.display = 'block';
        $theaterMain.style.display = 'block';
        $theaterButton.style.backgroundColor = '#2275a4';
        $theaterButton.style.border = '1px solid #2275a4';
        $theaterSearchForm.style.display = 'block';
        $buttonWrapper.style.border = '2px solid #2275a4'
        $moviePage.style.display = 'none';
        $theaterPage.style.display = 'block';

        const url = new URL(location.href);
        url.searchParams.set('mode', 'theater');
        history.pushState(undefined, undefined, url.toString());
    }

    const url = new URL(location.href);
    if ((url.searchParams.get('mode') ?? 'movie') === 'movie') {
        $movieInfo.dispatchEvent(new Event('click'));
    } else {
        $theaterInfo.dispatchEvent(new Event('click'));
    }
}



{
    const $screenItem = Array.from(document.querySelectorAll('.screen-item'));
    $screenItem.forEach((screen) => {
        const $theaterDelete = Array.from(screen.querySelectorAll(':scope > .theater-delete'));
        $theaterDelete.forEach((del) => {
            del.onclick = () => {
                $theaterDelete.forEach((item) => {
                    if (item === del) {
                        const scNum = screen.querySelector(':scope > .scNum');
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('scNum', scNum.innerText);
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState !== XMLHttpRequest.DONE) {
                                return;
                            }
                            if (xhr.status < 200 || xhr.status >= 300) {
                                alert('오류 발생');
                                return;
                            }
                            const response = JSON.parse(xhr.responseText);
                            console.log(response);
                        };
                        xhr.open('DELETE', './is_admin');
                        xhr.send(formData);
                    }
                })
            }
        })
    })
}

document.addEventListener("DOMContentLoaded", function () {
    const $deleteButton = document.querySelectorAll('.movie-delete')
    const $modifyButton = document.querySelectorAll('.movie-modify')
    $deleteButton.forEach(button => {
        button.addEventListener("click", function () {
            const movieNum = this.getAttribute("data-mo-num")
            const result = confirm("정말 삭제하시겠습니까?")
            if (result) {
                const xhr = new XMLHttpRequest();
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    if (xhr.status < 200 || xhr.status >= 300) {
                        alert('삭제에 실패하였습니다. 다시 시도해 주세요')
                        return;
                    }
                    const response = JSON.parse(xhr.responseText);
                    console.log(response);
                    if (response === 'SUCCESS') {
                        alert('삭제가 완료되었습니다.');
                        window.location.reload();
                    } else if (response === 'NOT_LOGGED_IN') {
                        alert('삭제에 실패했습니다. 세션이 만료되었거나 로그인이 해제되었을 수 있습니다. 로그인 상태를 확인한 후 다시 시도해 주세요.');
                    } else if (response === 'FAILURE') {
                        alert('삭제에 실패하였습니다. 다시 시도해 주세요')
                    } else {
                        alert('알 수 없는 응답입니다. 관리자에게 문의하세요')
                    }
                };
                xhr.open('POST', "/admin/delete");
                xhr.setRequestHeader('Content-Type', 'application/json');
                const Data = {
                    moNum: movieNum
                };
                xhr.send(JSON.stringify(Data));
            } else {
                return
            }
        })
    })
    $modifyButton.forEach(button => {
        button.addEventListener("click", function (){
            const movieNum = this.getAttribute("data-mo-num")
            window.location.href = `/admin/modify/${movieNum}`;
        })
    })
})