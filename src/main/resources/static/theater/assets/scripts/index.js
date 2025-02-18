const $main = document.getElementById('main');
const $items = $main.querySelector(':scope > .img > .main');
const $theater = Array.from($items.querySelectorAll(':scope > .item'));
const $itemContainer = $main.querySelector(':scope > .img > .theater-container > .item-container');
const $buttonContainer = $main.querySelector(':scope > .button-container');
const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
const $informations = Array.from($main.querySelectorAll(':scope > .information'));
const $selects = Array.from($buttonContainer.querySelectorAll(':scope > .button > .select'));
const $cinemaInformation = $main.querySelector(':scope > .information[data-id="cinema"]');
const $dayContainers = $cinemaInformation.querySelector(':scope > .cinema-info > .cinema-header > .day-containers');
const $screens = $cinemaInformation.querySelector(':scope > .cinema-info > .items');
const theaterParams = JSON.parse(sessionStorage.getItem('theater'));

// region 광고
{
    const $sideAdvertisementArray = ['https://adimg.cgv.co.kr/images/202411/Firefighters/1121_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/RealPain/0114_980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/Panda/980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/DarkNuns/980x90.jpg'];
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.querySelector('.advertisement-info');
        const $advertisementRandom = $sideAdvertisementArray[Math.floor(Math.random() * $sideAdvertisementArray.length)];
        const $a = $advertisement.querySelector(':scope > a');
        const $img = $advertisement.querySelector(':scope > a > img');        switch ($advertisementRandom) {
            case ($sideAdvertisementArray[0]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4317');
                break;
            case ($sideAdvertisementArray[1]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4266');
                break;
            case ($sideAdvertisementArray[2]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4267');
                break;
            case ($sideAdvertisementArray[3]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4283')
                break;
            case ($sideAdvertisementArray[4]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4290')
                break;
            case ($sideAdvertisementArray[5]):
                $a.setAttribute('href', '../movies/movieList/movieInfo/4261')
                break;
        }
        $img.setAttribute('src', $advertisementRandom);
    });

    const $advertisementContainerArray = ['https://adimg.cgv.co.kr/images/202412/PORORO/1231_160x300.jpg', 'https://adimg.cgv.co.kr/images/202412/Moana2/1218_160x300.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_160x300.png', 'https://adimg.cgv.co.kr/images/202501/RealPain/0114_160x300.jpg', 'https://adimg.cgv.co.kr/images/202501/Panda/160x300.jpg', 'https://adimg.cgv.co.kr/images/202501/DarkNuns/160x300.png'];
    const $advertisementContainer = Array.from(document.querySelectorAll('.advertisement-container'));
    document.addEventListener("DOMContentLoaded", () => {
        $advertisementContainer.forEach((advertisement) => {
            const $advertisementMove = Array.from(advertisement.querySelectorAll(':scope > .advertisement-move'));
            $advertisementMove.forEach((ad) => {
                const $advertisementRandom = $advertisementContainerArray[Math.floor(Math.random() * $advertisementContainerArray.length)];
                switch ($advertisementRandom) {
                    case ($advertisementContainerArray[0]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4266')
                        break;
                    case ($advertisementContainerArray[1]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4318')
                        break;
                    case ($advertisementContainerArray[2]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4267')
                        break;
                    case ($advertisementContainerArray[3]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4283')
                        break;
                    case ($advertisementContainerArray[4]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4290')
                        break;
                    case ($advertisementContainerArray[5]):
                        ad.setAttribute('href', '../movies/movieList/movieInfo/4261')
                        break;
                }
                const $img = ad.querySelector(':scope > img');
                $img.setAttribute('src', $advertisementRandom);
            })
        })
    });
}
// endregion

{
    const $cinemaInfoRating = document.querySelector('.cinema-info-detail > .cinema-detail.rating');
    const $cinemaInfoPrice = document.querySelector('.cinema-info-detail > .cinema-detail.price');
    const $modalPrice = document.querySelector('.modal[rel="price"]');
    const $modalRating = document.querySelector('.modal[rel="rating"]');
    const $buttonPrice = $modalPrice.querySelector(':scope > .head > .h1 > .button');
    const $buttonRating = $modalRating.querySelector(':scope > .head > .h1 > .button');
    $cinemaInfoPrice.onclick = () => {
        $modalPrice.classList.remove('hidden');
    }
    $buttonPrice.onclick = () => {
        $modalPrice.classList.add('hidden');
    }
    $cinemaInfoRating.onclick = () => {
        $modalRating.classList.remove('hidden');
    }
    $buttonRating.onclick = () => {
        $modalRating.classList.add('hidden');
    }
}

// region 첫 화면 대구로 지정
{
    window.onload = () => {
        const $regions = document.querySelector('[rel="대구"]');
        $regions.click();
    }
}
// endregion

{
    $theater.forEach(($item) => {
        $item.onclick = () => {
            $theater.forEach((x) => x.classList.remove('select'));
            $item.classList.add('select'); // 선택한 지역에 관련해서 select class 부여
            const url = new URL(location.href);
            url.searchParams.set('region', $item.innerText);
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                Loading.hide();
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('오류 발생');
                    return;
                }
                $itemContainer.innerHTML = "";
                const $container = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.img > .theater-container > .item-container > .item'));
                $container.forEach((x) => {
                    $itemContainer.append(x);
                    x.onclick = () => {
                        $container.forEach((item) => {
                            item.classList.remove('select');
                            if (item === x) {
                                item.classList.add('select');
                            }
                        })
                        const xhr = new XMLHttpRequest();
                        url.searchParams.set('theater', x.innerText);
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState !== XMLHttpRequest.DONE) {
                                return;
                            }
                            Loading.hide();
                            if (xhr.status < 200 || xhr.status >= 300) {
                                alert('오류 발생');
                                return;
                            }
                            const $days = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('.day-container');
                            const $beforeButton = $dayContainers.querySelector(':scope > .before');
                            const $afterButton = $dayContainers.querySelector(':scope > .after');
                            const $dayContainer = document.createElement('ul');
                            $dayContainer.classList.add('day-container');
                            $dayContainers.innerHTML = '';
                            $dayContainers.append($beforeButton);
                            $dayContainers.append($afterButton);
                            $dayContainers.append($dayContainer);
                            const $items = Array.from($days.querySelectorAll(':scope > .item'));
                            const $dayButtons = Array.from($dayContainers.querySelectorAll(':scope > .button'));
                            $items.forEach((item, index) => {
                                if (index >= 8) {
                                    item.classList.add('hidden');
                                }
                                $dayContainer.append(item);
                                const $buttonContainer = $main.querySelector(':scope > .button-container');
                                const $selects = Array.from($buttonContainer.querySelectorAll(':scope > .button > .select'));
                                $selects.forEach((button) => {
                                    if (button.dataset.id === "cinema") {
                                        button.click();
                                    }
                                });
                                item.onclick = () => {
                                    $items.forEach((it) => {
                                        it.classList.remove('select');
                                        if (item === it) {
                                            it.classList.add('select');
                                            const currentDate = new Date();
                                            let year = currentDate.getFullYear();
                                            const currentMonth = currentDate.getMonth() + 1;
                                            const month = item.querySelector(':scope > .small-container > .day:nth-child(1)');
                                            if (month.innerText.replace('월', '') < currentMonth || (month.innerText.replace('월', '') === currentMonth)) {
                                                year += 1;
                                            }
                                            const day = item.querySelector(':scope > .day');
                                            url.searchParams.set('date', year + '-' + month.innerText.substring(0, 2) + '-' + day.innerText);
                                            const xhr = new XMLHttpRequest();
                                            xhr.onreadystatechange = () => {
                                                if (xhr.readyState !== XMLHttpRequest.DONE) {
                                                    return;
                                                }
                                                Loading.hide();
                                                if (xhr.status < 200 || xhr.status >= 300) {
                                                    alert('오류 발생');
                                                    return;
                                                }
                                                $screens.innerHTML = '';
                                                const $screenContainer = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.cinema-info > .items > .item');
                                                $screenContainer.forEach((screen) => {
                                                    $screens.append(screen);
                                                    const $timeTable = Array.from(screen.querySelectorAll(':scope > .screen-container > .time-table-container > .time-table'));
                                                    $timeTable.forEach((time) => {
                                                        time.onclick = () => {
                                                            const $moTitle = screen.querySelector(':scope > .movie-container > .text');
                                                            // 파라미터 값들을 객체로 저장
                                                            const params = {
                                                                moTitle: $moTitle.innerText,
                                                                thName: x.innerText,
                                                                scStartDate: year + '-' + month.innerText.substring(0, 2) + '-' + day.innerText,
                                                                time: time.innerText.split('\n')[0]
                                                            };
                                                            sessionStorage.setItem('ticketParams', JSON.stringify(params));
                                                        }
                                                    })
                                                })
                                                const $theaterInfo = document.querySelector('.theater-info.guide');
                                                const $main = document.getElementById('main');
                                                const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
                                                const $informations = Array.from($main.querySelectorAll(':scope > .information'));
                                                const $finding = document.querySelector('.find');
                                                switch (x.innerText) {
                                                    case ("CGV대구"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC&lng=128.5897380095846&lat=35.885288432104254&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구수성"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%88%98%EC%84%B1&lng=128.6404587&lat=35.8211314&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구스타디움"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%8A%A4%ED%83%80%EB%94%94%EC%9B%80&lng=128.69173494246&lat=35.829630597172&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구아카데미"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%95%84%EC%B9%B4%EB%8D%B0%EB%AF%B8&lng=128.5943053&lat=35.8699880&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구연경"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%97%B0%EA%B2%BD&lng=128.62276493855003&lat=35.941400738412334&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구월성"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%9B%94%EC%84%B1&lng=128.52622410000004&lat=35.8244452&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구죽전"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%EC%A3%BD%EC%A0%84&lng=128.53663879067838&lat=35.85053962373264&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구한일"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%ED%95%9C%EC%9D%BC&lng=128.5953569&lat=35.8704771&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                    case ("CGV대구현대"):
                                                        $finding.setAttribute('href', "https://map.naver.com/p?title=CGV%EB%8C%80%EA%B5%AC%ED%98%84%EB%8C%80&lng=128.5905864&lat=35.8667736&zoom=15&type=0&c=15.00,0,0,0,dh");
                                                        break;
                                                }
                                                $buttons.forEach(($item) => {
                                                    $theaterInfo.onclick = () => {
                                                        $selects.forEach((x) => {
                                                            x.classList.add('hidden');
                                                            if (x.getAttribute('data-id') === $item.getAttribute('data-id')) {
                                                                x.classList.remove('hidden');
                                                            }
                                                        })
                                                        $informations.forEach((x) => {
                                                            x.classList.add('hidden');
                                                            if (x.getAttribute('data-id') === $item.getAttribute('data-id')) {
                                                                x.classList.remove('hidden');
                                                            }
                                                        });
                                                    }
                                                })
                                            }
                                            xhr.open('GET', url.toString());
                                            xhr.send();
                                            Loading.show(0);
                                        }
                                    })
                                }
                            })
                            if ($items.length > 0) {
                                $items[0].click();
                            }
                            let Index = 0;
                            $dayButtons.forEach((dayButton) => {
                                dayButton.onclick = () => {
                                    if (dayButton.classList.contains('after')) {
                                        Index += 8;
                                    }
                                    if (dayButton.classList.contains('before')) {
                                        Index -= 8;
                                    }
                                    if (Index < 0) {
                                        Index = 0;
                                    }
                                    if (Index >= $items.length) {
                                        Index = Index - 8;
                                    }
                                    $items.forEach((iem, index) => {
                                        if (index >= Index && index < Index + 8) {
                                            iem.classList.remove('hidden');
                                        } else {
                                            iem.classList.add('hidden');
                                        }
                                    })
                                }
                            })
                            const $theaterContainer = document.querySelector(' #main > .theater-container');
                            const $theaters = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll(' #main > .theater-container'));
                            $theaters.forEach((theater) => {
                                $theaterContainer.replaceWith(theater);
                            })
                            const $busInfo = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('.bus.detail');
                            const $carInfo = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('.car.detail');
                            const $bus = document.querySelector('.bus.detail');
                            const $car = document.querySelector('.car.detail');
                            $bus.innerHTML = '';
                            $bus.replaceWith($busInfo);
                            $car.innerHTML = '';
                            $car.replaceWith($carInfo);
                        }
                        xhr.open('GET', url.toString());
                        xhr.send();
                        Loading.show(0);
                    }
                    if (theaterParams) {
                        if (x.innerText === theaterParams['thName']) {
                            setTimeout(() => {
                                x.click();
                            }, 0);
                            sessionStorage.removeItem('theater');
                        }
                    } else {
                        if (x.innerText === 'CGV대구') {
                            x.click();  // 클릭 이벤트 트리거
                        }
                    }
                })
            }
            xhr.open('GET', url.toString());
            xhr.send();
            Loading.show(0);
        }
    });
}

$buttons.forEach(($item) => {
    $item.onclick = () => {
        $selects.forEach((x) => {
            x.classList.add('hidden');
            if (x.getAttribute('data-id') === $item.getAttribute('data-id')) {
                x.classList.remove('hidden');
            }
        })
        $informations.forEach((x) => {
            x.classList.add('hidden');
            if (x.getAttribute('data-id') === $item.getAttribute('data-id')) {
                x.classList.remove('hidden');
            }
        });
    }
})
