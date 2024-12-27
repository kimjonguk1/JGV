const $logout = document.getElementById('logout');

if ($logout) {
    $logout.addEventListener('click', (e) => {
        e.preventDefault();

        sessionStorage.clear();
        location.href = $logout.href;
    });
}

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
    }
}