const $logout = document.getElementById('logout');

if ($logout) {
    $logout.addEventListener('click', (e) => {
        e.preventDefault();

        sessionStorage.clear();
        location.href = $logout.href;
    });
}

const $adminCrawl = document.getElementById('movie-crawl-button');

$adminCrawl.onclick = () => {
    window.open(
        "http://localhost:8080/admin/crawl",
        "크롤링",
        "width=600,height=800,left=200,top=200"
    );
};

{
    const $titleChange = document.getElementById('admin-title-container');
    const $movieInfo = $titleChange.querySelector(':scope > .movie-information');
    const $theaterInfo = $titleChange.querySelector(':scope > .theater-information');
    const $movieButton = document.getElementById('movie-crawl-button');
    const $theaterButton = document.getElementById('theater-crawl-button');
    const $movieMain = document.getElementById('movie-admin-page');
    const $theaterMain = document.getElementById('theater-admin-page');

    $movieInfo.onclick = () => {
        $movieButton.style.display = 'block';
        $theaterButton.style.display = 'none';
        $movieMain.style.display = 'block';
        $theaterMain.style.display = 'none';

    }

    $theaterInfo.onclick = () => {
        $movieButton.style.display = 'none';
        $theaterButton.style.display = 'block';
        $movieMain.style.display = 'none';
        $theaterMain.style.display = 'block';
    }
}