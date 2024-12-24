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
const $dayContainer = $dayContainers.querySelector(':scope > .day-container')
const $days = Array.from($dayContainer.querySelectorAll(':scope > .item'))
const $screens = $cinemaInformation.querySelector(':scope > .cinema-info > .items');

{
    window.onload = () => {
        const $regions = document.querySelector('[rel="대구"]');
        $regions.click();
    }
}

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
                                            if (month < currentMonth || (month === currentMonth)) {
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
                                                        time.onclick = (e) => {
                                                            e.preventDefault();
                                                            const $moTitle = screen.querySelector(':scope > .movie-container > .text');
                                                            const xhr = new XMLHttpRequest();
                                                            const url = new URL('http://localhost:8080/ticket/');
                                                            // 파라미터 값들을 객체로 저장
                                                            const params = {
                                                                moTitle: $moTitle.innerText,
                                                                thName: x.innerText,
                                                                scStartDate: year + '-' + month.innerText.substring(0, 2) + '-' + day.innerText,
                                                                time: time.innerText.split('\n')[0]
                                                            };
                                                            localStorage.setItem('ticketParams', JSON.stringify(params));
                                                            xhr.onreadystatechange = () => {
                                                                if (xhr.readyState !== XMLHttpRequest.DONE) {
                                                                    return;
                                                                }
                                                                if (xhr.status < 200 || xhr.status >= 300) {
                                                                    alert('오류 발생');
                                                                    return;
                                                                }
                                                                window.location.href = url.toString();
                                                                Loading.hide();
                                                            };
                                                            xhr.open('GET', url.toString());
                                                            xhr.send();
                                                            Loading.show(0);
                                                        }
                                                    })
                                                })
                                                const $theaterInfo = document.querySelector('.theater-info.guide');
                                                const $main = document.getElementById('main');
                                                const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
                                                const $informations = Array.from($main.querySelectorAll(':scope > .information'));
                                                const $finding = document.querySelector('.find');
                                                $finding.setAttribute('href', `https://map.naver.com/p?title=${x.innerText}&lng=128.5897380095846&lat=35.885288432104254&zoom=15&type=0&c=15.00,0,0,0,dh`)
                                                // finding 교체하기

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
                    if (x.innerText === 'CGV대구') {
                        x.click();  // 클릭 이벤트 트리거
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

$days.forEach(($item) => {
    $item.onclick = () => {
        $days.forEach((y) => {
            y.classList.remove('select');
            if ($item === y) {
                y.classList.add('select');
            }
        })
    }
});
