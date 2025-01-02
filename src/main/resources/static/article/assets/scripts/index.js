// region nav 광고
{
    const $advertisementArray = ['https://adimg.cgv.co.kr/images/202412/Moana2/1218_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1224_980x80.png', 'https://adimg.cgv.co.kr/images/202411/jjanggu/1209_980x80.png']
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.getElementById('advertisement');
        const $advertisementRandom = $advertisementArray[Math.floor(Math.random() * $advertisementArray.length)];
        const $img = $advertisement.querySelector(':scope > a > img');
        const $a = $advertisement.querySelector(':scope > a')
        if ($advertisementRandom === $advertisementArray[0]) {
            $advertisement.style.backgroundColor = '#2B53AB'
            $a.setAttribute('href', '../movies/movieList/movieInfo/3669')
        } else if ($advertisementRandom === $advertisementArray[1]) {
            $advertisement.style.backgroundColor = '#4184D2'
            $a.setAttribute('href', '../movies/movieList/movieInfo/3628')
        } else if ($advertisementRandom === $advertisementArray[2]) {
            $advertisement.style.backgroundColor = '#191413'
            $a.setAttribute('href', '../movies/movieList/movieInfo/3611')
        } else {
            $advertisement.style.backgroundColor = '#2B82DD'
            $a.setAttribute('href', '../movies/movieList/movieInfo/3666')
        }
        $img.setAttribute('src', $advertisementRandom);
    });
}
// endregion

document.addEventListener('DOMContentLoaded', function () {
    const tabs = document.querySelectorAll('.tabs a');
    const sections = document.querySelectorAll('main section');

    tabs.forEach(tab => {
        tab.addEventListener('click', function (e) {
            e.preventDefault();

            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            sections.forEach(section => section.style.display = 'none');
            const target = document.querySelector(this.getAttribute('href'));
            target.style.display = 'block';
        });
    });

    // 초기 활성화 상태
    document.querySelector('.tabs a.active').click();
});

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
        xhr.send(); // GET 요청에는 본문(formData)이 필요하지 않습니다.
    };
}

document.addEventListener('click', (e) => {
    if (e.target.classList.contains('reserve-btn')) {
        // 클릭된 버튼에서 영화 제목 가져오기
        const movieTitle = e.target.dataset.moTitle;

        // 세션 스토리지에 영화 제목 저장
        const ticket = {
            moTitle: movieTitle
        };

        sessionStorage.setItem('ticketParams', JSON.stringify(ticket));
        console.log('Session storage updated:', ticket);
    }
});
