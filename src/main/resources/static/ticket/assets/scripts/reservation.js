const $body = document.querySelector('body');

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

    const $sideAdvertisementArray = ['https://adimg.cgv.co.kr/images/202411/Firefighters/1121_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_980x90.jpg'];
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.querySelector('.add');
        const $advertisementRandom = $sideAdvertisementArray[Math.floor(Math.random() * $sideAdvertisementArray.length)];
        const $a = $advertisement.querySelector(':scope > a')
        const $img = $advertisement.querySelector(':scope > a > img');
        if ($advertisementRandom === $sideAdvertisementArray[0]) {
            $a.setAttribute('href', '../movies/movieList/movieInfo/3651')
        } else if ($advertisementRandom === $sideAdvertisementArray[1]) {
            $a.setAttribute('href', '../movies/movieList/movieInfo/3628')
        } else {
            $a.setAttribute('href', '../movies/movieList/movieInfo/3611')
        }
        $img.setAttribute('src', $advertisementRandom);
    });

    const $advertisementContainerArray = ['https://adimg.cgv.co.kr/images/202412/PORORO/1231_160x300.jpg', 'https://adimg.cgv.co.kr/images/202412/Moana2/1218_160x300.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_160x300.png'];
    const $advertisementContainer = Array.from(document.querySelectorAll('.advertisement-container'));
    document.addEventListener("DOMContentLoaded", () => {
        $advertisementContainer.forEach((advertisement) => {
            const $advertisementMove = Array.from(advertisement.querySelectorAll(':scope > .advertisement-move'));
            $advertisementMove.forEach((ad) => {
                const $advertisementRandom = $advertisementContainerArray[Math.floor(Math.random() * $advertisementContainerArray.length)];
                if ($advertisementRandom === $advertisementContainerArray[0]) {
                    ad.setAttribute('href', '../movies/movieList/movieInfo/3628')
                } else if ($advertisementRandom === $advertisementContainerArray[1]) {
                    ad.setAttribute('href', '../movies/movieList/movieInfo/3669')
                } else {
                    ad.setAttribute('href', '../movies/movieList/movieInfo/3611')
                }
                const $img = ad.querySelector(':scope > img');
                $img.setAttribute('src', $advertisementRandom);
            })
        })
    });
}

window.onload = () => {
    if (sessionStorage.getItem('paymentComplete') !== 'true') {
        window.location.href = '../../';
        return;
    }

    $body.style.display = 'flex';

    const keys = [
        'meName', 'moTitle', 'ciName', 'thName',
        'seName', 'scStartDate', 'paPrice',
        'human', 'poster', 'paymentNumber'
    ];

    keys.forEach(key => {
        const element = document.getElementById(key);
        if (element) {
            if (key === 'poster') element.src = sessionStorage.getItem(key);
            else element.innerText = sessionStorage.getItem(key);
        }
    });

    sessionStorage.removeItem('paymentComplete');
    keys.forEach(key => sessionStorage.removeItem(key));
};
