const $main = document.getElementById('main');
const $items = $main.querySelector(':scope > .img > .main');
const $theater = Array.from($items.querySelectorAll(':scope > .item'));
const $itemContainer = $main.querySelector(':scope > .img > .item-container');

$theater.forEach(($item) => {
    $item.onclick = () => {
        $theater.forEach((x) => x.classList.remove('select'));
        $item.classList.add('select');
        const url = new URL(location.href)
        url.searchParams.set('region', $item.innerText);
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('오류 발생');
                return;
            }
            const response = JSON.parse(xhr.responseText);
            let responses = [];
            $itemContainer.innerText = "";
            response['result'].forEach((x) => {
                responses.push(x);
                const $li = document.createElement('li');
                $li.innerText = x['thName'];
                $li.classList.add('item');
                $itemContainer.append($li);
                const $containerItems = Array.from($itemContainer.querySelectorAll(':scope > .item'));
                $containerItems.forEach(($item) => {
                    $item.onclick = () => {
                        $containerItems.forEach((x) => x.classList.remove('select'));
                        $item.classList.add('select');
                        responses.forEach((item) => {
                            if (item['thName'] === $item.innerText) {
                                //     $thName.innerText = item['thName'];
                                //
                                //     $thImage.innerHTML =
                                //         `<img src="${item['thImg']}" alt=""
                                //              class="image"/>;`
                                //     console.log(item['thAddr'])

                                const $theaterHead = new DOMParser().parseFromString(`
        <div class="theater-container">        
            <div class="header">
                <span class="text">${item['thName']}</span>
                <span class="stretch"></span>
                <button class="button">단체/대관 문의</button>
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
                            <div class="theater-info">${item['thAddr']}</div>
                        </div>
                        <a href="#" class="theater-info guide" target="_blank">위치 / 주차 안내 ></a>
                        <div class="stretch"></div>
                        <div class="cinema-type">
                            <a href="#" class="screenX" target="_blank"></a>
                            <a href="#" class="screenX" target="_blank"></a>
                            <a href="#" class="screenX" target="_blank"></a>
                        </div>
                    </div>
                    <div class="theater-info">6관/874석</div>
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
                    }
                })
            })
        };
        xhr.open('GET', url.toString());
        xhr.send();
    }
});

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

