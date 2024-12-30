const $main = document.getElementById('main');
const $items = $main.querySelector(':scope > .img > .main');
const $theater = Array.from($items.querySelectorAll(':scope > .item'));
const $itemContainer = $main.querySelector(':scope > .img > .theater-container > .item-container');
const $buttonContainer = $main.querySelector(':scope > .img > .button-container');
const $images = Array.from($main.querySelectorAll(':scope > .image'));
const $movieButtonContainer = $main.querySelector(':scope > .img-movie > .button-container');
const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
const $movieButtons = Array.from($movieButtonContainer.querySelectorAll(':scope > .button'));
const $cinemaInformation = $main.querySelector(':scope > .information[data-id="theater"]');
const $cinemaInfo = $cinemaInformation.querySelector(':scope > .cinema-info');
const $dayContainers = $cinemaInfo.querySelector(':scope > .cinema-header > .day-containers');
const $screens = $cinemaInformation.querySelector(':scope > .cinema-info > .items');
const $informations = Array.from($main.querySelectorAll(':scope > .information'));

let text = null;

{
    const $cinemaInfoRating = document.querySelector('.cinema-info-detail > .cinema-detail.rating');
    const $modalRating = document.querySelector('.modal[rel="rating"]');
    const $buttonRating = $modalRating.querySelector(':scope > .head > .h1 > .button');
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
                                const $h1 = $main.querySelector(':scope > .h1');
                                if ($h1 == null) {
                                    const $div = document.createElement('div');
                                    $div.classList.add('h1');
                                    $div.innerText = x.innerText;
                                    $main.insertBefore($div, $main.children[2]);
                                } else {
                                    $h1.innerText = x.innerText;
                                }
                            }
                        })
                        const xhr = new XMLHttpRequest();
                        url.searchParams.set('theater', x.innerText);
                        text = x.innerText;
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
                                const $buttonContainer = $main.querySelector(':scope > .img > .button-container');
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
                                                const $screenContainer = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.information[data-id="theater"] >.cinema-info > .items > .item');
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
                                                            sessionStorage.setItem('ticketParams', JSON.stringify(params));
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
                                                const $main = document.getElementById('main');
                                                const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
                                                const $informations = Array.from($main.querySelectorAll(':scope > .information'));
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

{
    const $imgMovie = document.querySelector('.img-movie');
    const $itemContainer = $imgMovie.querySelector(':scope > .movie-container > .movie-title > .movie');
    const $items = $itemContainer.querySelector(':scope > .item-container');
    $buttons.forEach(($item) => {
        $item.onclick = () => {
            $images.forEach((image) => {
                image.classList.add('hidden');
                if (image.getAttribute('data-id') === $item.getAttribute('data-id')) {
                    image.classList.remove('hidden');
                    if (image.getAttribute('data-id') === 'movie') {
                        $items.click();
                    }
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

{
    $movieButtons.forEach(($item) => {
        $item.onclick = () => {
            $images.forEach((image) => {
                image.classList.add('hidden');
                if (image.getAttribute('data-id') === $item.getAttribute('data-id')) {
                    image.classList.remove('hidden');
                    const $h1 = $main.querySelector(':scope > .h1');
                    $h1.innerText = text;
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

{
    const $schedule = document.getElementById('schedule');
    const $items = $schedule.querySelector(':scope > .main');
    const $theater = Array.from($items.querySelectorAll(':scope > .item'));
    const $screens = $schedule.querySelector(':scope > .cinema-info > .items');
    const $movie = document.querySelector('.movie-title > .movie');
    const $movieItems = Array.from($movie.querySelectorAll(':scope > .item-container'));

    $movieItems.forEach((movie) => {
        movie.onclick = () => {
            const $h1 = $main.querySelector(':scope > .h1');
            $h1.innerText = movie.innerText;
            $movieItems.forEach((item) => {
                item.classList.remove('select');
                if (movie === item) {
                    item.classList.add('select');
                    $theater.forEach(($item) => {
                        if ($item.innerText === '대구') {
                            setTimeout(() => {
                                $item.click();
                            }, 0);
                        }
                        $item.onclick = () => {
                            $theater.forEach((x) => x.classList.remove('select'));
                            $item.classList.add('select'); // 선택한 지역에 관련해서 select class 부여
                            const url = new URL(location.href);
                            url.searchParams.set('region', $item.innerText);
                            url.searchParams.set('movie', movie.innerText);
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
                                const $newMovie = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('.img-movie > .movie-container > .movie');
                                const $cinemaType = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector(' .img-movie > .movie-container > .cinema-type');
                                const $movieContainer = document.querySelector(' .img-movie > .movie-container');
                                const $realCinema = $movieContainer.querySelector(':scope > .cinema-type');
                                const $movie = document.querySelector('.img-movie > .movie-container > .movie')
                                if ($movie) {
                                    $movie.replaceWith($newMovie);
                                } else {
                                    $movieContainer.prepend($newMovie);
                                }
                                $realCinema.replaceWith($cinemaType);
                                const $cinemaItems = document.querySelector('.cinema-type > .items');
                                const $cinemaItem = Array.from($cinemaItems.querySelectorAll(':scope > .item'));
                                const $dayContainer = $schedule.querySelector('.day-containers');
                                $dayContainer.innerHTML = '';
                                const $dayContainers = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('#schedule > .cinema-info > .day-containers');
                                const $days = $dayContainers.querySelector(':scope > .day-container');
                                const $items = Array.from($days.querySelectorAll(':scope > .item'));
                                const $dayButtons = Array.from($dayContainers.querySelectorAll(':scope > .button'));
                                const $beforeButton = $dayContainers.querySelector(':scope > .before');
                                const $afterButton = $dayContainers.querySelector(':scope > .after');
                                $dayContainer.append($beforeButton);
                                $dayContainer.append($afterButton);
                                $dayContainer.append($days);
                                $items.forEach((item, index) => {
                                    if (index >= 9) {
                                        item.classList.add('hidden');
                                    }
                                    item.onclick = () => {
                                        $items.forEach((it) => {
                                            it.classList.remove('select');
                                            if (item === it) {
                                                it.classList.add('select');
                                                const currentDate = new Date();
                                                let year = currentDate.getFullYear();
                                                const currentMonth = currentDate.getMonth() + 1;
                                                const month = item.querySelector(':scope > .small-container > .day:nth-child(1)');
                                                if (month.innerText.substring(0, 2) < currentMonth) {
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
                                                    const $screenContainer = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('#schedule > .cinema-info > .items > .item');
                                                    $screenContainer.forEach((screen) => {
                                                        $screens.append(screen);
                                                    })
                                                    $cinemaItem.forEach((item) => {
                                                        if ($cinemaItems.firstElementChild) {
                                                            $cinemaItems.firstElementChild.click(); // 첫 번째 자식 클릭
                                                        }
                                                        item.onclick = () => {
                                                            $cinemaItem.forEach((cinema) => {
                                                                cinema.classList.remove('select');
                                                                if (item === cinema) {
                                                                    cinema.classList.add('select');
                                                                    url.searchParams.set('cinema', cinema.innerText);
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
                                                                        const $screenItems = document.querySelector('#schedule > .cinema-info > .items');
                                                                        const $newScreens = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('#schedule > .cinema-info > .items');
                                                                        $screenItems.replaceWith($newScreens);
                                                                    }
                                                                    xhr.open('GET', url.toString());
                                                                    xhr.send();
                                                                    Loading.show(0);
                                                                }
                                                            })
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
                                            Index += 9;
                                        }
                                        if (dayButton.classList.contains('before')) {
                                            Index -= 9;
                                        }
                                        if (Index < 0) {
                                            Index = 0;
                                        }
                                        if (Index >= $items.length) {
                                            Index = Index - 9;
                                        }
                                        $items.forEach((iem, index) => {
                                            if (index >= Index && index < Index + 9) {
                                                iem.classList.remove('hidden');
                                            } else {
                                                iem.classList.add('hidden');
                                            }
                                        })
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
        }
    })
}
