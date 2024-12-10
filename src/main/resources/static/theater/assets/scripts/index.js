const $main = document.getElementById('main');
const $items = $main.querySelector(':scope > .img > .main');
const $theater = Array.from($items.querySelectorAll(':scope > .item'));
const $itemContainer = $main.querySelector(':scope > .img > .item-container');

{
    $theater.forEach(($item) => {
        $item.onclick = () => {
            $theater.forEach((x) => x.classList.remove('select'));
            $item.classList.add('select'); // 선택한 지역에 관련해서 select class 부여
            const url = new URL(location.href);
            url.searchParams.set('region', $item.innerText);
            let responses = [];
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
                const response = JSON.parse(xhr.responseText);
                $itemContainer.innerText = "";
                response['result'].forEach((x) => {
                    responses.push(x);
                    const $li = document.createElement('li');
                    $li.innerText = x['thName'];
                    $li.classList.add('item');
                    $itemContainer.append($li); // 지역 밑에 있는 각 영화관 불러오기
                });
                const $containerItems = Array.from($itemContainer.querySelectorAll(':scope > .item'));
                $containerItems.forEach(($item) => {
                    $item.onclick = () => {
                        url.searchParams.set('theater', $item.innerText);
                        let counts = [];
                        const xhr1 = new XMLHttpRequest();
                        xhr1.onreadystatechange = () => {
                            if (xhr1.readyState !== XMLHttpRequest.DONE) {
                                return;
                            }
                            Loading.hide();
                            if (xhr1.status < 200 || xhr1.status >= 300) {
                                alert('오류 발생');
                                return;
                            }
                            const result = JSON.parse(xhr1.responseText);
                            counts = result['theater'].slice(1, -1).split(",");
                            $containerItems.forEach((x) => x.classList.remove('select'));
                            $item.classList.add('select');
                            responses.forEach((item) => {
                                if (item['thName'] === $item.innerText) {
                                    let $addr = item['thAddr'].slice(1, -1).split(",").map(item => item.trim());
                                    $addr = [$addr[0], $addr.slice(1).join(", ")];
                                    const $theaterHead = new DOMParser().parseFromString(`
        <div class="theater-container">
            <div class="header">
                <span class="text">${item['thName']}</span>
                <span class="stretch"></span>
                <span class="button">단체/대관 문의</span>
            </div>
        </div>
`, 'text/html').querySelector('.header');
                                    const $theater = new DOMParser().parseFromString(`
    <div class="theater-container">
        <div class="img">
            <img src="${item['thImg']}" alt="" class="image">
            <div class="theater-info-container">
                <div class="small-container">
                    <div class="header">
                        <div class="info-container">
                            <div class="theater-info">${$addr[0]}<br>${$addr[1]}</div>
                        </div>
                        <a href="#" class="theater-info guide" target="_blank">위치 / 주차 안내 ></a>
                        <div class="stretch"></div>
                        <div class="cinema-type">
                            <a href="#" class="screenX" target="_blank"></a>
                            <a href="#" class="screenX" target="_blank"></a>
                            <a href="#" class="screenX" target="_blank"></a>
                        </div>
                    </div>
                    <div class="theater-info">${counts[0]}관/${counts[1]}석</div>
                </div>
                <div class="stretch"></div>
                <div class="notice-container">
                    <div class="theater-info">공지사항</div>
                    <a href="#" class="button"></a>
                </div>
            </div>
        </div>
    </div>`, 'text/html').querySelector('.img');
                                    const $theaterContainer = $main.querySelector(':scope > .theater-container')
                                    $theaterContainer.innerHTML = "";
                                    $theaterContainer.append($theaterHead);
                                    $theaterContainer.append($theater);
                                }
                            })
                        };
                        xhr1.open('GET', url.toString());
                        xhr1.send();
                        Loading.show(0);
                    }
                })
            };
            xhr.open('GET', url.toString());
            xhr.send();
            Loading.show(0);
        }
    })
}

// $theater.forEach(($item) => {
//     $item.onclick = () => {
//         $theater.forEach((x) => x.classList.remove('select'));
//         $item.classList.add('select');
//         const url = new URL(location.href)
//         url.searchParams.set('region', $item.innerText);
//         const xhr = new XMLHttpRequest();
//         xhr.onreadystatechange = () => {
//             if (xhr.readyState !== XMLHttpRequest.DONE) {
//                 return;
//             }
//             if (xhr.status < 200 || xhr.status >= 300) {
//                 alert('오류 발생');
//                 return;
//             }
//             const response = JSON.parse(xhr.responseText);
//             let responses = [];
//             $itemContainer.innerText = "";
//             response['result'].forEach((x) => {
//                 responses.push(x);
//                 const $li = document.createElement('li');
//                 $li.innerText = x['thName'];
//                 $li.classList.add('item');
//                 $itemContainer.append($li);
//             });
//             const $containerItems = Array.from($itemContainer.querySelectorAll(':scope > .item'));
//             $containerItems.forEach(($item) => {
//                 $item.onclick = () => {
//                     const xhr1 = new XMLHttpRequest();
//                     xhr1.onreadystatechange = () => {
//                         if (xhr1.readyState !== XMLHttpRequest.DONE) {
//                             return;
//                         }
//                         if (xhr1.status < 200 || xhr1.status >= 300) {
//                             alert('오류 발생');
//                             return;
//                         }
//                         let counts = [];
//                         url.searchParams.set('theater', $item.innerText);
//                         console.log(url.toString());
//                         console.log($item.innerText);
//                         console.log(response['theater']);
//                         counts = response['theater'].slice(1, -1).split(",");
//                         console.log(counts);
//                         $containerItems.forEach((x) => x.classList.remove('select'));
//                         $item.classList.add('select');
//                         responses.forEach((item) => {
//                             if (item['thName'] === $item.innerText) {
//                                 let $addr = item['thAddr'].slice(1, -1).split(",").map(item => item.trim());
//                                 $addr = [$addr[0], $addr.slice(1).join(", ")];
//                                 const $theaterHead = new DOMParser().parseFromString(`
//         <div class="theater-container">
//             <div class="header">
//                 <span class="text">${item['thName']}</span>
//                 <span class="stretch"></span>
//                 <span class="button">단체/대관 문의</span>
//             </div>
//         </div>
// `, 'text/html').querySelector('.header');
//                                 const $theater = new DOMParser().parseFromString(`
//     <div class="theater-container">
//         <div class="img">
//             <img src="${item['thImg']}" alt="" class="image">
//             <div class="theater-info-container">
//                 <div class="small-container">
//                     <div class="header">
//                         <div class="info-container">
//                             <div class="theater-info">${$addr[0]}<br>${$addr[1]}</div>
//                         </div>
//                         <a href="#" class="theater-info guide" target="_blank">위치 / 주차 안내 ></a>
//                         <div class="stretch"></div>
//                         <div class="cinema-type">
//                             <a href="#" class="screenX" target="_blank"></a>
//                             <a href="#" class="screenX" target="_blank"></a>
//                             <a href="#" class="screenX" target="_blank"></a>
//                         </div>
//                     </div>
//                     <div class="theater-info">${counts[0]}관/${counts[1]}석</div>
//                 </div>
//                 <div class="stretch"></div>
//                 <div class="notice-container">
//                     <div class="theater-info">공지사항</div>
//                     <a href="#" class="button"></a>
//                 </div>
//             </div>
//         </div>
//     </div>`, 'text/html').querySelector('.img');
//                                 const $theaterContainer = $main.querySelector(':scope > .theater-container')
//                                 $theaterContainer.innerHTML = "";
//                                 $theaterContainer.append($theaterHead);
//                                 $theaterContainer.append($theater);
//                             }
//                         })
//                     };
//                     xhr1.open('GET', url.toString());
//                     xhr1.send();
//                 }
//             })
//         }
//         xhr.open('GET', url.toString());
//         xhr.send();
//     }
// })

const $buttonContainer = $main.querySelector(':scope > .button-container');
const $buttons = Array.from($buttonContainer.querySelectorAll(':scope > .button'));
const $informations = Array.from($main.querySelectorAll(':scope > .information'));
const $selects = Array.from($buttonContainer.querySelectorAll(':scope > .button > .select'));
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

const $cinemaInformation = $main.querySelector(':scope > .information[data-id="cinema"]');
const $dayContainer = $cinemaInformation.querySelector(':scope > .cinema-info > .cinema-header > .day-containers');
const $days = Array.from($dayContainer.querySelectorAll(':scope > .day-container > .item'))
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
