const $main = document.getElementById('main');
const $mainContainer = $main.querySelector(':scope > .main-container');
const $mainContent = $mainContainer.querySelector(':scope > .main');
const $movie = $mainContent.querySelector(':scope > .movie > .body > .content > .movie')
const $movieItems = Array.from($movie.querySelectorAll(':scope > .item-container'));
const $orderItems = Array.from($mainContent.querySelectorAll(':scope > .movie > .body > .content > .order > .text'));
const $region = $mainContent.querySelector(':scope > .theater > .body > .content > .region-container')
const $regionItems = Array.from($region.querySelectorAll(':scope > .region'));
const $theater = $mainContent.querySelector(':scope > .theater > .body > .content > .theater-container')
const $contentContainer = $mainContent.querySelector(':scope > .day > .body > .content > .content-container')
const $dayContainer = Array.from($contentContainer.querySelectorAll(':scope > .day-container'));
const $controlBar = document.getElementById('control-bar');
const $containers = $controlBar.querySelector(':scope > .container > .containers');
const $theaterTheater = document.getElementById('theater-theater');
const $theaterInfo = $controlBar.querySelectorAll(':scope > .container > [data-id="theaterInfo"]')
const $theaterTime = document.getElementById('theater-time');
const $timeContainer = $mainContent.querySelector(':scope > .time > .time > .time-container');
const $firstButton = $controlBar.querySelector(':scope > .container > [data-id="main"]')
const $theaterCinema = document.getElementById('theater-cinema');
const ticketParams = JSON.parse(localStorage.getItem('ticketParams'));

{
    window.onload = () => {
        const $regions = Array.from(document.querySelectorAll('[data-id="region"]'));
        $regions.forEach((region) => {
            if (region.innerText.includes('대구')) {
                region.click();
            }
        })
        if (ticketParams) {
            $movieItems.forEach((movie) => {
                const $items = movie.querySelector(':scope > .text > .name');
                if ($items.innerText === ticketParams.moTitle) {
                    movie.click();
                }
            })
        }
    }
}

// 상영 정보를 찾아오는 함수
function checkScreen() {
    const {movie, theater, date} = $data;
    if (movie && !theater && !date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('moTitle', movie);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $contentContainer.innerHTML = '';
            const $days = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.day > .body > .content > .content-container > .day-container')));
            $days.forEach((x) => {
                $contentContainer.append(x);
                const $day = Array.from(x.querySelectorAll(':scope > .day'));
                $day.forEach((item) => {
                    item.onclick = () => {
                        const $dayTitle = Array.from(x.querySelectorAll(':scope > .title'));
                        document.querySelectorAll('.day.select').forEach((selectedItem) => {
                            selectedItem.classList.remove('select');
                        })
                        item.classList.add('select');
                        let array = item.innerText.split('\n');
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $dayTitle.forEach((day) => {
                                const $year = day.querySelector('.year');
                                const $month = day.querySelector('.month');
                                $theaterTime.innerText = $year.innerText + '.' + $month.innerText + '.' + array[1] + '(' + array[0] + ')';
                            })
                        })
                        $data.date = $theaterTime.innerText;
                        $data.date = $data.date.split('(')[0].replaceAll('.', '-');
                        checkScreen();
                    }
                })
            })
            $theater.innerHTML = "";
            const $theaterItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.theater > .body > .content > .theater-container > .theater'));
            $theaterItem.forEach((x) => {
                $theater.append(x);
                if (ticketParams) {
                    if (ticketParams.thName.replace('CGV', '') === x.innerText) {
                        setTimeout(() => {
                            x.click();
                        }, 0); // 클릭 이벤트가 등록될 때까지 대기
                    }
                }
                x.onclick = () => {
                    $theaterItem.forEach((item) => {
                        item.classList.remove('select');
                        if (x === item) {
                            item.classList.add('select');
                        }
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $data.theater = 'CGV' + x.innerText;
                            $theaterTheater.innerText = 'CGV' + x.innerText + '>';
                        })
                    })
                    checkScreen();
                }
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
    if (!movie && theater && !date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('thName', theater);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $contentContainer.innerHTML = '';
            const $days = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.day > .body > .content > .content-container > .day-container')));
            $days.forEach((x) => {
                $contentContainer.append(x);
                const $day = Array.from(x.querySelectorAll(':scope > .day'));
                $day.forEach((item) => {
                    item.onclick = () => {
                        $theaterTime.innerText = '';
                        const $dayTitle = Array.from(x.querySelectorAll(':scope > .title'));
                        document.querySelectorAll('.day.select').forEach((selectedItem) => {
                            selectedItem.classList.remove('select');
                        })
                        item.classList.add('select');
                        let array = item.innerText.split('\n');
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $dayTitle.forEach((day) => {
                                const $year = day.querySelector('.year');
                                const $month = day.querySelector('.month');
                                $theaterTime.innerText = $year.innerText + '.' + $month.innerText + '.' + array[1] + '(' + array[0] + ')';
                            })
                        })
                        $data.date = $theaterTime.innerText;
                        $data.date = $data.date.split('(')[0].replaceAll('.', '-');
                        checkScreen();
                    }
                })
            })
            $movie.innerHTML = '';
            const $movieItems = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.main > .movie > .body > .content > .movie > .item-container')));
            $movieItems.forEach((x) => {
                $movie.append(x);
                movieItem($movieItems);
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
    if (!movie && !theater && date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('scStartDate', date);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $theater.innerHTML = "";
            const $theaterItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.theater > .body > .content > .theater-container > .theater'));
            $theaterItem.forEach((x) => {
                $theater.append(x);
                x.onclick = () => {
                    $theaterItem.forEach((item) => {
                        item.classList.remove('select');
                        if (x === item) {
                            item.classList.add('select');
                        }
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $data.theater = 'CGV' + x.innerText;
                            $theaterTheater.innerText = 'CGV' + x.innerText + '>';
                        })
                    })
                    checkScreen();
                }
            })
            $movie.innerHTML = '';
            const $movieItems = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.main > .movie > .body > .content > .movie > .item-container')));
            $movieItems.forEach((x) => {
                $movie.append(x);
                movieItem($movieItems);
            })
            Loading.hide();
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
    if (movie && theater && !date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('moTitle', movie);
        url.searchParams.set('thName', theater);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $contentContainer.innerHTML = '';
            const $days = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.day > .body > .content > .content-container > .day-container')));
            $days.forEach((x) => {
                $contentContainer.append(x);
                if (ticketParams) {
                    const $title = Array.from(x.querySelectorAll(':scope > .title'));
                    $title.forEach((title) => {
                        const $year = title.querySelector(':scope > .year');
                        const $month = title.querySelector(':scope > .month');
                        const $day = Array.from(x.querySelectorAll(':scope > .day'));
                        $day.forEach((day) => {
                            const $date = day.querySelector(':scope > .day-container > .date');
                            if ($year.innerText + '-' + $month.innerText + '-' + $date.innerText === ticketParams.scStartDate) {
                                setTimeout(() => day.click(), 0);
                            }
                        })
                    })
                }
                const $day = Array.from(x.querySelectorAll(':scope > .day'));
                $day.forEach((item) => {
                    item.onclick = () => {
                        $theaterTime.innerText = '';
                        const $dayTitle = Array.from(x.querySelectorAll(':scope > .title'));
                        document.querySelectorAll('.day.select').forEach((selectedItem) => {
                            selectedItem.classList.remove('select');
                        })
                        item.classList.add('select');
                        let array = item.innerText.split('\n');
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $dayTitle.forEach((day) => {
                                const $year = day.querySelector('.year');
                                const $month = day.querySelector('.month');
                                $theaterTime.innerText = $year.innerText + '.' + $month.innerText + '.' + array[1] + '(' + array[0] + ')';
                            })
                        })
                        $data.date = $theaterTime.innerText;
                        $data.date = $data.date.split('(')[0].replaceAll('.', '-');
                        checkScreen();
                    }
                })
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
    if (movie && !theater && date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('moTitle', movie);
        url.searchParams.set('scStartDate', date);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $theater.innerHTML = "";
            const $theaterItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.theater > .body > .content > .theater-container > .theater'));
            $theaterItem.forEach((x) => {
                $theater.append(x);
                x.onclick = () => {
                    $theaterItem.forEach((item) => {
                        item.classList.remove('select');
                        if (x === item) {
                            item.classList.add('select');
                        }
                        $theaterInfo.forEach((theater) => {
                            theater.classList.add('hidden');
                            if (theater.classList.contains('theater')) {
                                theater.classList.remove('hidden');
                            }
                            $data.theater = 'CGV' + x.innerText;
                            $theaterTheater.innerText = 'CGV' + x.innerText + '>';
                        })
                    })
                    checkScreen();
                }
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
    }
    if (!movie && theater && date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('thName', theater);
        url.searchParams.set('scStartDate', date);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $movie.innerHTML = '';
            const $movieItems = Array.from((new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.main > .movie > .body > .content > .movie > .item-container')));
            $movieItems.forEach((x) => {
                $movie.append(x);
                movieItem($movieItems);
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
    if (movie && theater && date) {
        const xhr = new XMLHttpRequest();
        const url = new URL(location.href);
        url.searchParams.set('moTitle', movie);
        url.searchParams.set('thName', theater);
        url.searchParams.set('scStartDate', date);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            $firstButton.classList.remove('after');
            document.querySelectorAll('.day.select').forEach((selectedItem) => {
                let array = selectedItem.innerText.split('\n');
                $theaterTime.innerText = $data.date.replaceAll('-', '.') + '(' + array[0] + ')';
            })
            const $oldText = $theaterTime.innerText;
            $theaterCinema.innerText = '';
            const $screen = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.time-table'));
            const $cinemaType = $controlBar.querySelector('.unique-cinema-type');
            const $rating = $controlBar.querySelector('.unique-rating');
            $rating.classList.add('hidden');
            $cinemaType.innerText = '';
            $timeContainer.innerHTML = '';
            $screen.forEach((screen) => {
                $timeContainer.append(screen);
                const $times = Array.from(screen.querySelectorAll(':scope > .times'))
                $times.forEach((time) => {
                    const $items = Array.from(time.querySelectorAll(':scope > .item'));
                    $items.forEach((item) => {
                        if (ticketParams) {
                            const $time = item.querySelector(':scope > .time > .text');
                            if ($time.innerText === ticketParams.time) {
                                setTimeout(() => item.click(), 0);
                            }
                        }
                        item.onclick = () => {
                            const $cinema = screen.querySelector(':scope > .title > .cinema');
                            const $type = screen.querySelector(':scope > .title > .cinema-type');
                            const $text = item.querySelector(':scope > .time > .text');
                            document.querySelectorAll('.item.select').forEach((selectedItem) => {
                                selectedItem.classList.remove('select');
                            });
                            item.classList.add('select');
                            $theaterCinema.innerText = $cinema.innerText;
                            $cinemaType.innerText = $type.innerText;
                            $theaterTime.innerText = $oldText + $text.innerText;
                            $rating.classList.remove('hidden');
                            $firstButton.classList.add('after');
                            $data.movie = null;
                            $data.theater = null;
                            $data.date = null;
                            localStorage.removeItem('ticketParams');
                        }
                    })
                })
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
        Loading.show(0);
    }
}

// 지정한 데이터
const $data = {
    movie: null,   // 선택된 영화
    theater: null, // 선택된 지점
    date: null,    // 선택된 날짜
};

// 영화 정보를 불러오는 함수
function movieItem($movieItems) {
    $movieItems.forEach((x) => {
        x.onclick = () => {
            $movieItems.forEach((item) => {
                item.classList.remove('select');
                if (x === item) {
                    item.classList.add('select');
                    const xhr = new XMLHttpRequest();
                    const url = new URL(location.href);
                    url.searchParams.set('moTitle', item.innerText);
                    $data.movie = item.innerText;
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState !== XMLHttpRequest.DONE) {
                            return;
                        }
                        Loading.hide();
                        if (xhr.status < 200 || xhr.status >= 300) {
                            alert('오류 발생');
                            return;
                        }
                        const $info = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll(' #control-bar > .container > .containers > [data-id="movieInfo"]'));
                        $containers.innerHTML = "";
                        $info.forEach((info) => {
                            info.classList.add('hidden');
                            if (info.classList.contains('posters')) {
                                info.classList.remove('hidden');
                            }
                            $containers.append(info);
                        })
                    };
                    xhr.open('GET', url.toString());
                    xhr.send();
                    Loading.show(0);
                }
            })
            checkScreen();
        }
    })
}

// region 영화정보
$orderItems.forEach((x) => {
    x.onclick = () => {
        $orderItems.forEach((item) => {
            item.classList.remove('select');
            if (x === item) {
                item.classList.add('select');
                if (x.innerText === '가나다순') {
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
                        $movie.innerHTML = '';
                        const $movieItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.main > .movie > .body > .content > .movie > .item-container'));
                        $movieItem.forEach((x) => {
                            $movie.append(x);
                            movieItem($movieItem);
                            checkScreen();
                        })
                    };
                    xhr.open('GET', './movies');
                    xhr.send();
                    Loading.show(0);
                } else if (x.innerText === '예매율순') {
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
                        $movie.innerHTML = '';
                        const $movieItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.main > .movie > .body > .content > .movie > .item-container'));
                        $movieItem.forEach((x) => {
                            $movie.append(x);
                            movieItem($movieItem);
                            checkScreen();
                        })
                    };
                    xhr.open('GET', './');
                    xhr.send();
                    Loading.show(0);
                }
            }
        })
    }
})

movieItem($movieItems);
// endregion

$regionItems.forEach((x) => {
    x.onclick = () => {
        $regionItems.forEach((item) => {
            item.classList.remove('select');
            if (x === item) {
                item.classList.add('select');
                const xhr = new XMLHttpRequest();
                const url = new URL(location.href);
                url.searchParams.set('region', x.innerText.split('(')[0]);
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    Loading.hide();
                    if (xhr.status < 200 || xhr.status >= 300) {
                        alert('오류 발생');
                        return;
                    }
                    $theater.innerHTML = "";
                    const $theaterItem = Array.from(new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.theater > .body > .content > .theater-container > .theater'));
                    $theaterItem.forEach((x) => {
                        $theater.append(x);
                        x.onclick = () => {
                            $theaterItem.forEach((item) => {
                                item.classList.remove('select');
                                if (x === item) {
                                    item.classList.add('select');
                                }
                                $theaterInfo.forEach((theater) => {
                                    theater.classList.add('hidden');
                                    if (theater.classList.contains('theater')) {
                                        theater.classList.remove('hidden');
                                    }
                                    $data.theater = 'CGV' + x.innerText;
                                    $theaterTheater.innerText = 'CGV' + x.innerText + '>';
                                })
                            })
                            checkScreen();
                        }
                    })
                };
                xhr.open('GET', url.toString());
                xhr.send();
                Loading.show(0);
            }
        })
    }
})

$dayContainer.forEach((days) => {
    const $dayTitle = Array.from(days.querySelectorAll(':scope > .title'));
    const $dayItems = Array.from(days.querySelectorAll(':scope > .day'));
    $dayItems.forEach((x) => {
        x.onclick = () => {
            document.querySelectorAll('.day.select').forEach((selectedItem) => {
                selectedItem.classList.remove('select');
            })
            x.classList.add('select');
            let array = x.innerText.split('\n');
            $theaterInfo.forEach((theater) => {
                theater.classList.add('hidden');
                if (theater.classList.contains('theater')) {
                    theater.classList.remove('hidden');
                }
                $dayTitle.forEach((day) => {
                    const $year = day.querySelector('.year');
                    const $month = day.querySelector('.month');
                    $theaterTime.innerText = $year.innerText + '.' + $month.innerText + '.' + array[1] + '(' + array[0] + ')';
                })
            })
            $data.date = $theaterTime.innerText;
            $data.date = $data.date.split('(')[0].replaceAll('.', '-');
            checkScreen();
        }
    })
})

// ---------------------------------- 분리점

const $whiteBlow = document.getElementById('whiteBlow');
const $mains = Array.from($mainContainer.querySelectorAll(':scope > .mains'));
const $seatContainer = $controlBar.querySelector(':scope > .container > .seat-container');
const $paymentContainer = $controlBar.querySelector(':scope > .container > .payment-container');
const $leftButtons = Array.from($controlBar.querySelectorAll(':scope > .container > .left-button'));
const $rightButtons = Array.from($controlBar.querySelectorAll(':scope > .container > .right-button'));
const $RightThird = $controlBar.querySelector(':scope > .container > .third.right-button');
const $RightSecond = $controlBar.querySelector(':scope > .container > .second.right-button');
const $LeftSecond = $controlBar.querySelector(':scope > .container > .second.left-button');
const $paymentSection = document.getElementById('payment-section');


const $mainPayment = document.getElementById("main-payment");
const $payForm = $paymentSection.querySelector(':scope > .pay-form');
let t = 0;
let m = 0;
const $seatContent = $controlBar.querySelector(':scope > .container > .seat > .seat-content');
const $seatInfo = $controlBar.querySelector(':scope > .container > .seat.info');
const $priceInfo = $controlBar.querySelector(':scope > .container > .price.info');
const $seatTitle = $controlBar.querySelector(':scope > .container > .seat > .seat-title');
const $seatCommon = document.createElement('span');
$seatCommon.className = 'seat-common data';
$seatTitle.appendChild($seatCommon);

const $theaterHuman = document.getElementById('theater-human');
const $seatHuman = document.createElement('span');
$seatHuman.className = 'seat-human data';
$theaterHuman.appendChild($seatHuman);

const $payPriceWon1 = document.getElementById("pay-price-won1");
const $payPriceWon2 = document.getElementById("pay-price-won2");
const $payPriceWon3 = document.getElementById("pay-price-won3");
const $payPriceWon4 = document.getElementById("pay-price-won4");
const $payPriceWon5 = document.getElementById("pay-price-won5");
const $payPriceWon6 = document.getElementById("pay-price-won6");
const $payPriceWonInt = document.getElementById("pay-price-won-int");
const $seatDate = document.getElementById("seat-date");
const $seatTime = document.getElementById("seat-time");
const $posterImg = document.getElementById("poster-img");


const adults = document.querySelectorAll('.adults');
const seats = [];


const table = document.createElement('table');
table.className = 'table';

const rows = ['A', 'B', 'C', 'D', 'E'];

const screen = document.querySelector("#screen");
if (screen) {
    screen.appendChild(table);
}

const $seatNumber = document.createElement('span');
$seatNumber.className = 'seat-number data';
$seatContent.appendChild($seatNumber);

let selectedSeats = [];


const $payButton = document.getElementById('pay-button');
const $realCancel = $payButton.querySelector(':scope > .real-cancel');
const $seatColor = document.getElementById('seat-color');
const $method = document.getElementById('method');


let selectedHuman = [];
const $priceTitle = $controlBar.querySelector(':scope > .container > .price > .price-title');
const $priceContent = $controlBar.querySelector(':scope > .container > .price > .price-content');
const $seatPriceCommon = document.createElement('span');
const $seatPricePay = document.createElement('span');
const $seatPrice = document.createElement('span');
const $seatPriceAdd = document.createElement('span');
$seatPriceCommon.className = 'seat-Price-Common head';
$seatPricePay.className = 'seat-Price-Pay head';
$seatPrice.className = 'seat-Price-Pay data';
$seatPriceAdd.className = 'seat-Price-Pay data2';
$priceTitle.appendChild($seatPriceCommon);
$priceContent.appendChild($seatPricePay);
$priceTitle.appendChild($seatPrice);
$priceContent.appendChild($seatPriceAdd);
let price = 0;

const $payHuman = $payForm.querySelector(':scope > .table-div > .div-content1 > .pay-human')
const $paySeat = $payForm.querySelector(':scope > .table-div > .div-content1 > .pay-seat')
const $payMovie = document.getElementById('pay-movie')
const $payTheater = document.getElementById('pay-theater')
const $payCinema = document.getElementById('pay-cinema')
const $payTime = document.getElementById('pay-time')

const $checkboxAgreeAll = document.getElementById('checkbox-agree-all');
const $checkboxAgreeSolo = document.getElementById('checkbox-agree-solo');
const $checkboxAgrees = $payForm.querySelectorAll(':scope > .agree > .agree-div1 > .agree-label > .checkbox-agree');

//payment
let pay = "card";
let pay2 = "";
const $paymentCheck = document.querySelectorAll('input[name="payment-check"]');
const $payContainers = $mainPayment.querySelectorAll(':scope > .left-container > .pay-containers');
const $cardContainer = $mainPayment.querySelector(':scope > .left-container > .card-container');
const $card = document.querySelector('[name = card]')
const $cellphoneContainer = $mainPayment.querySelector(':scope > .left-container > .cellphone-container');
const $simplePayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container');
const $creditContainer = $mainPayment.querySelector(':scope > .left-container > .credit-container');
const $tossContainer = $mainPayment.querySelector(':scope > .left-container > .toss-container');


const $simplePayCheck = document.querySelectorAll('input[name="simple-pay-check"]');
const $simplePayTextContainers = $mainPayment.querySelectorAll(':scope > .left-container > .simple-pay-container > .simple-pay-text-container');
const $naverPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .naver-pay-container')
const $smilePaynaverContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .smile-pay-container')
const $ssgPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .ssg-pay-container')
const $kakaoPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .kakao-pay-container')
const $paycoContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .payco-container')
const $cultureCard = document.querySelector('input[name="culture-card"]');
const $noneCulturePayText = $mainPayment.querySelector(':scope > .left-container >.simple-pay-container > .naver-pay-container > .none-culture-pay-text')
const $CulturePayText = $mainPayment.querySelector(':scope > .left-container >.simple-pay-container > .naver-pay-container > .culture-pay-text')
let $theaterTheater2 = "";

$rightButtons.forEach((x) => {
    x.onclick = () => {
        if (x.classList.contains('after')) {
            // 메인 바꾸기
            if (sessionStorage.getItem('user') === null) {
                const userCheck = confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")
                if (userCheck) {
                    const redirectUrl = window.location.pathname;  // 현재 페이지 경로
                    window.location.replace(`.././user/login?redirect=${encodeURIComponent(redirectUrl)}`);  // URL 파라미터로 redirect 전달
                } else {
                    window.location.reload();
                }
            }


            const $theaterMovie = $containers.querySelector(':scope > .posters > .movie-info > .title');

            $mains.forEach((main) => {
                main.classList.add('hidden');
                if (x.getAttribute('data-id') === 'main' &&
                    main.getAttribute('data-id') === 'main-seat') {
                    main.classList.remove('hidden');
                    $seatDate.innerText = `${$theaterTime.innerText}`;
                    $theaterTheater2 = `${$theaterTheater.innerText.substring(0, $theaterTheater.innerText.length - 1)}`;

                    let rawDateStr = $theaterTime.innerText;
                    let formattedDate = rawDateStr
                        .replace(/\([^)]+\)/, "T") // "(금)" 제거
                        .replace(/\./g, "-"); // "." -> "-"

                    const xhr = new XMLHttpRequest();
                    const url = new URL("http://localhost:8080/ticket/seat"); //ticket
                    url.searchParams.set('thName', $theaterTheater2);
                    url.searchParams.set('ciName', $theaterCinema.innerText);
                    url.searchParams.set('moTitle', $theaterMovie.innerText);
                    url.searchParams.set('scStartDate', formattedDate);
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState !== XMLHttpRequest.DONE) {
                            return;
                        }
                        if (xhr.status < 200 || xhr.status >= 300) {
                            return;
                        }
                        const response = JSON.parse(xhr.responseText);
                        const result = response['result'];
                        const result2 = response['results'];
                        console.log(result);
                        console.log(result2);
                        $seatColor.innerText = 40 - result.length;

                        $payMovie.innerText = `${$theaterMovie.innerText}`;
                        $payTheater.innerText = `${$theaterTheater2}`;
                        $payCinema.innerText = `${$theaterCinema.innerText}`;

                        // 주어진 날짜와 시간
                        let dateTime = $theaterTime.innerText;
                        let addMinutes = `${result2[0].moTime}`;
// 시와 분을 추출
                        let timePart = dateTime.split(')')[1]; // "08:45" 추출
                        let [hour, minute] = timePart.split(':').map(Number); // hour = 8, minute = 45

// 추가 시간 계산
                        let extraHours = Math.floor(addMinutes / 60); // 124 ÷ 60 = 2
                        let extraMinutes = addMinutes % 60;          // 124 % 60 = 4

// 기존 시간에 추가
                        let newHour = hour + extraHours;             // 8 + 2 = 10
                        let newMinute = minute + extraMinutes;       // 45 + 4 = 49

// 분이 60을 넘는 경우 처리 (현재는 필요 없음)
                        if (newMinute >= 60) {
                            newHour += Math.floor(newMinute / 60);
                            newMinute %= 60;
                        }
                        console.log(newHour)
                        console.log(newMinute)

// 시와 분을 두 자리로 맞추기
                        newHour = newHour.toString().padStart(2, '0'); // 2자리 형식의 시
                        newMinute = newMinute.toString().padStart(2, '0'); // 2자리 형식의 분

// 결과 출력
                        $seatTime.innerText = `${newHour}:${newMinute}`;

                        $payTime.innerText = `${$theaterTime.innerText}` + ` ~ ` + `${$seatTime.innerText}`;


                        rows.forEach(row => {
                            const tr = document.createElement('tr'); // 행 생성
                            const th = document.createElement('th');
                            th.textContent = row;
                            tr.appendChild(th);
                            for (let i = 1; i <= 8; i++) {
                                const td = document.createElement('td');
                                td.id = `${row}${i}`;
                                td.className = 'seat';
                                td.textContent = i;
                                tr.appendChild(td);
                                seats.push(td);
                                if (result.some(item => item.seName === td.id)) {
                                    td.style.backgroundImage = "url('/ticket/assets/images/icon3.png')"; // 일치하면 배경 변경
                                    td.style.backgroundSize = 'cover'; // 크기 조정
                                    td.style.backgroundPosition = 'center'; // 위치 설정
                                    td.style.pointerEvents = 'none';
                                    td.innerText = '';
                                }
                            }
                            table.appendChild(tr);
                        });

                        adults.forEach((radio) => {
                            radio.addEventListener('change', () => {
                                t = parseInt(radio.value);
                                m = parseInt(radio.value);
                                if (radio.value !== '0') {
                                    $seatInfo.classList.remove('hidden')
                                    $priceInfo.classList.remove('hidden')
                                    $seatCommon.textContent = '일반석';
                                    $seatPriceCommon.textContent = '일반';
                                    $seatPricePay.textContent = '총금액';
                                    price = radio.value * result2[0].citPrice;
                                    const price2 = price.toLocaleString();
                                    const price3 = result2[0].citPrice.toLocaleString();

                                    $payPriceWon1.textContent = `${price2}`;
                                    $payPriceWon2.textContent = `${price2}`;
                                    $payPriceWon3.textContent = `${price2}`;
                                    $payPriceWon4.textContent = `${price2}`;
                                    $payPriceWon5.textContent = `${price2}`;
                                    $payPriceWon6.textContent = `${price2}`;
                                    $payPriceWonInt.textContent = `${price}`;

                                    $seatPrice.textContent = `${price3} X ${radio.value}`;
                                    $seatPriceAdd.textContent = `${price2}원`;
                                    selectedHuman.push(`일반 ${(radio.value)} 명`);
                                    $seatHuman.textContent = `${selectedHuman.join(', ')}`
                                    $payHuman.textContent = selectedHuman;
                                    $posterImg.src = `${result2[0].MImgUrl}`


                                } else {
                                    $RightSecond.classList.remove('after')
                                    $seatCommon.textContent = '';
                                    $seatPriceCommon.textContent = '';
                                    $seatPricePay.textContent = '';
                                    $seatPrice.textContent = '';
                                    $seatPriceAdd.textContent = '';
                                    $seatHuman.textContent = `${selectedHuman.join(', ')}`;

                                }

                                seats.forEach((seat) => {
                                    seat.classList.remove('selected-seat');
                                    const index = selectedHuman.indexOf(`일반 ${(radio.value)} 명`);
                                    if (index !== -1) {
                                        selectedHuman.splice(index, 1);
                                    }
                                });
                                selectedSeats = [];
                                $seatNumber.textContent = '';
                            });
                        });

                        seats.forEach((seat) => {
                            seat.addEventListener('click', () => {
                                if (t > 0 && !seat.classList.contains('selected-seat')) {
                                    t--;
                                    seat.classList.add('selected-seat');
                                    selectedSeats.push(seat.id);
                                    $seatNumber.textContent = `${selectedSeats.join(', ')}`;
                                } else {
                                    for (j = 0; j < m; j++) {
                                        if (seat.id === selectedSeats[j]) {
                                            t++;
                                            seat.classList.remove('selected-seat');
                                            const index = selectedSeats.indexOf(seat.id);
                                            if (index !== -1) {
                                                selectedSeats.splice(index, 1);
                                            }
                                            $seatNumber.textContent = `${selectedSeats.join(', ')}`;

                                            // else if (seat.classList.contains('selected-seat')) {
                                            //         t++;
                                            //         seat.classList.remove('selected-seat');
                                            //         const index = selectedSeats.indexOf(seat.id);
                                            //         if (index !== -1) {
                                            //             selectedSeats.splice(index, 1);
                                            //         }
                                            //         $seatNumber.textContent = `${selectedSeats.join(', ')}`;
                                        }
                                    }
                                }
                                $paySeat.textContent = selectedSeats;
                                if (t === 0 && m !== 0) {
                                    $RightSecond.classList.add('after');
                                } else if (t !== 0 && m !== 0) {
                                    $RightSecond.classList.remove('after')

                                }

                            })
                        });
                    }
                    xhr.open('GET', url.toString()); //ticket/ciName=2관&thName=CGV대구
                    xhr.send();
                }
                if (x.getAttribute('data-id') === 'main-seat' &&
                    main.getAttribute('data-id') === 'main-payment') {
                    main.classList.remove('hidden');
                    $priceInfo.classList.add('hidden')
                }


                if ((x.getAttribute('data-id') === 'main-payment' &&
                    main.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value !== "카드를 선택해주세요.") || (x.getAttribute('data-id') === 'main-payment' &&
                    main.getAttribute('data-id') === 'main-payment' && pay !== "card")) {
                    main.classList.remove('hidden');
                    $paymentSection.classList.remove('hidden');
                    $RightThird.classList.remove('hidden');
                    $whiteBlow.classList.remove('hidden')

                } else if (x.getAttribute('data-id') === 'main-payment' &&
                    main.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                    main.classList.remove('hidden');
                    alert("결제수단을 선택해주세요.")
                }
            })
            $rightButtons.forEach((button) => {
                button.classList.add('hidden');
                //오른쪽 버튼 바꾸기
                if (x.getAttribute('data-id') === 'main' &&
                    button.getAttribute('data-id') === 'main-seat') {
                    $seatContainer.classList.add('hidden');
                    $paymentContainer.classList.add('hidden');
                    button.classList.remove('hidden');
                }
                if (x.getAttribute('data-id') === 'main-seat' &&
                    button.getAttribute('data-id') === 'main-payment') {
                    button.classList.remove('hidden');
                }
                if ((x.getAttribute('data-id') === 'main-payment' &&
                    button.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value !== "카드를 선택해주세요.") || (x.getAttribute('data-id') === 'main-payment' &&
                    button.getAttribute('data-id') === 'main-payment' && pay !== "card")) {
                    button.classList.remove('hidden');
                } else if (x.getAttribute('data-id') === 'main-payment' &&
                    button.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                    button.classList.remove('hidden');
                }
            })


// 왼쪽버튼 바꾸기
            $leftButtons.forEach((left) => {
                left.classList.add('hidden');
                if (x.getAttribute('data-id') === 'main' &&
                    left.getAttribute('data-id') === 'main-seat') {
                    left.classList.remove('hidden');
                    $RightSecond.classList.remove('after')

                }
                if (x.getAttribute('data-id') === 'main-seat' &&
                    left.getAttribute('data-id') === 'main-payment') {
                    left.classList.remove('hidden');
                }
                if ((x.getAttribute('data-id') === 'main-payment' &&
                    left.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value !== "카드를 선택해주세요.") || (x.getAttribute('data-id') === 'main-payment' &&
                    left.getAttribute('data-id') === 'main-payment' && pay !== "card")) {
                    left.classList.remove('hidden');
                } else if (x.getAttribute('data-id') === 'main-payment' &&
                    left.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                    left.classList.remove('hidden');

                }
            })
        } else {
            alert("영화를 선택해 주세요.");
        }
    }
});

$leftButtons.forEach((x) => {
    x.onclick = () => {
        x.classList.add('hidden');
        $leftButtons.forEach((left) => {
            left.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                left.getAttribute('data-id') === 'main') {
                left.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                left.getAttribute('data-id') === 'main-seat') {
                left.classList.remove('hidden');
            }
        })
        $rightButtons.forEach((right) => {
            right.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                right.getAttribute('data-id') === 'main') {
                right.classList.remove('hidden');
                table.innerHTML = ""
                adults.forEach(radio => {
                    radio.checked = false;
                });
                $seatCommon.textContent = '';
                $seatPriceCommon.textContent = '';
                $seatPricePay.textContent = '';
                $seatPrice.textContent = '';
                $seatPriceAdd.textContent = '';
                selectedSeats = [];
                selectedHuman = [];
                $seatHuman.textContent = `${selectedHuman.join(', ')}`;
                $seatNumber.textContent = '';
                t = 0;
                m = 0;
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                right.getAttribute('data-id') === 'main-seat') {
                right.classList.remove('hidden');
            }
        })
        $mains.forEach((main) => {
            main.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                main.getAttribute('data-id') === 'main') {
                $seatContainer.classList.remove('hidden');
                $paymentContainer.classList.remove('hidden');
                main.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                main.getAttribute('data-id') === 'main-seat') {
                main.classList.remove('hidden');
            }
        })
    }
})
$realCancel.onclick = () => {
    $paymentSection.classList.add('hidden');
    $whiteBlow.classList.add('hidden')

}

$method.innerText = "신용카드"
$paymentCheck.forEach((radio) => {
    radio.addEventListener('change', () => {
            pay = String(`${(radio.value)}`);

            $payContainers.forEach(container => {
                container.style.display = 'none';
            });

            if (pay === "card") {
                $method.innerText = "신용카드"
                $cardContainer.style.display = 'block';
            } else if (pay === "cellPhone") {
                $method.innerText = "휴대폰 결제"
                $cellphoneContainer.style.display = 'block';
            } else if (pay === "simple-pay") {
                $method.innerText = "간편결제"
                $simplePayContainer.style.display = 'block';
                $simplePayCheck.forEach((radio) => {
                    radio.addEventListener('change', () => {
                        pay2 = String(`${(radio.value)}`);
                        $simplePayTextContainers.forEach(container => {
                            container.style.display = 'none';
                            if (pay2 === "NAVERPAY") {
                                $naverPayContainer.style.display = 'flex';
                            } else if (pay2 === "SMILEPAY") {
                                $smilePaynaverContainer.style.display = 'flex';
                            } else if (pay2 === "SSGPAY") {
                                $ssgPayContainer.style.display = 'flex';
                            } else if (pay2 === "KAKAOPAY") {
                                $kakaoPayContainer.style.display = 'flex';
                            } else if (pay2 === "PAYCO") {
                                $paycoContainer.style.display = 'flex';
                            }
                        });
                    })
                })
            } else if (pay === "credit") {
                $method.innerText = "내통장결제"

                $creditContainer.style.display = 'block';
            } else if (pay === "toss") {
                $method.innerText = "토스"
                $tossContainer.style.display = 'block';
            }
        }
    )
})
$cultureCard.addEventListener('change', () => {
    if ($cultureCard.checked) {
        $noneCulturePayText.style.display = 'none';
        $CulturePayText.style.display = 'flex';
    } else {
        $noneCulturePayText.style.display = 'flex';
        $CulturePayText.style.display = 'none';
    }
})


$checkboxAgreeAll.addEventListener('change', function () {
    $checkboxAgrees.forEach(checkbox => {
        checkbox.checked = $checkboxAgreeAll.checked;
    });
});
$checkboxAgrees.forEach(checkbox => {
    checkbox.addEventListener('change', function () {
        // 모든 체크박스가 체크되었는지 확인
        const allChecked = Array.from($checkboxAgrees).every(cb => cb.checked);
        $checkboxAgreeAll.checked = allChecked;
    });
});

function ReservationRefundRegulation() {
    window.open(
        "http://localhost:8080/ticket/ReservationRefundRegulations", // 팝업에서 열릴 URL
        "ReservationRefundRegulation",         // 팝업창 이름
        "width=600,height=800,left=200,top=200" // 크기와 위치
    );
}

function TCUElectronicFinancialTransactions() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/PGApply.htm", // 팝업에서 열릴 URL
        "TCUElectronicFinancialTransactions",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

function TCCollectingPersonalInformation() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/IDCollectApply.htm", // 팝업에서 열릴 URL
        "TCCollectingPersonalInformation",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

function TCPPIC() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/IDProvideApply.htm", // 팝업에서 열릴 URL
        "TCPPIC",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

$payForm.onsubmit = (e) => {
    e.preventDefault(); // 기본 폼 제출 방지
    const $theaterMovie = $containers.querySelector(':scope > .posters > .movie-info > .title');
    // 약관 동의를 체크했는지 확인
    if ($checkboxAgreeAll.checked && $checkboxAgreeSolo.checked) {
        // 새로운 XMLHttpRequest 객체 생성
        const xhr = new XMLHttpRequest();

        // FormData 객체 생성
        const formData = new FormData();

        // 세션에서 userId 값을 가져옴
        const sessionUsId = sessionStorage.getItem('user').trim();  // 세션에서 userId를 가져오기
        if (sessionUsId) {
            formData.append("usNum", sessionUsId);  // 세션에서 가져온 userId 추가
        } else {
            return alert('로그인해주세요.');
        }
        let rawDateStr = $theaterTime.innerText;
        let formattedDate = rawDateStr
            .replace(/\([^)]+\)/, "T") // "(금)" 제거
            .replace(/\./g, "-"); // "." -> "-"

        // 폼 데이터 추가 (span의 텍스트는 innerText로 가져오기)
        formData.append("paPrice", document.getElementById('pay-price-won-int').innerText.trim()); // span 요소의 텍스트
        formData.append("meName", document.getElementById('method').innerText.trim());  // span 요소에서 결제 방법 번호 가져오기
        formData.append("moTitle", $theaterMovie.innerText);
        formData.append("ciName", $theaterCinema.innerText);
        formData.append("thName", $theaterTheater2);
        formData.append("scStartDate", formattedDate);
        selectedSeats.forEach(seat => {
            console.log(seat)
            formData.append("seName", seat);
        });
        // 요청 상태 변화 처리
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status >= 200 && xhr.status < 300) {

            } else {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
            const response = JSON.parse(xhr.responseText);
            const result2 = response['results'];
            if (response['result'] === "success") {
                alert("결제가 완료되었습니다.");
                // 결제 완료 후 sessionStorage에 상태 저장
                sessionStorage.setItem('paymentComplete', 'true');
                // 예매 정보도 sessionStorage에 저장
                sessionStorage.setItem('meName', document.getElementById('method').innerText);
                sessionStorage.setItem('moTitle', $theaterMovie.innerText);
                sessionStorage.setItem('ciName', $theaterCinema.innerText);
                sessionStorage.setItem('thName', $theaterTheater2);
                sessionStorage.setItem('scStartDate', $theaterTime.innerText);
                sessionStorage.setItem('paPrice', $seatPriceAdd.innerText);
                sessionStorage.setItem('human', $seatHuman.innerText);
                sessionStorage.setItem('seName', $seatNumber.innerText);
                sessionStorage.setItem('poster', $posterImg.src);
                sessionStorage.setItem('paymentNumber', `${result2}`);
                console.log(`${result2}`)
                window.location.href = ("../../ticket/reservation")
            }
        };


        // 요청 설정 및 전송
        xhr.open('POST', location.href);
        xhr.send(formData);
    } else {
        alert("약관을 모두 동의해주세요");
    }
};