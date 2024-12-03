{
    const $loading = document.getElementById('loading')
    $loading.style.display = 'block'

    fetch('/admin/crawling')
        .then(response => response.text())
        .then(html => {
            // 서버에서 받은 결과로 페이지 갱신
            document.body.innerHTML = html;
        })
        .catch(error => {
            $loading.style.display = 'none';
            alert('크롤링 작업 중 오류가 발생했습니다.');
            console.error(error);
        });

}