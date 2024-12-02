const $main = document.getElementById('main');
const $items = $main.querySelector(':scope > .img > .main');
const $move = Array.from($items.querySelectorAll(':scope > .item'));
const $itemContainer = $main.querySelector(':scope > .img > .item-container');
const $containerItems = Array.from($itemContainer.querySelectorAll(':scope > .item'));

$move.forEach(($item) => {
    $item.onclick = () => {
        $move.forEach((x) => x.classList.remove('select'));
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
            console.log(response);
        };
        xhr.open('GET', url.toString());
        xhr.send();
    }
});

$containerItems.forEach(($item) => {
    $item.onclick = () => {
        $containerItems.forEach((x) => x.classList.remove('select'));
        $item.classList.add('select');
    }
})
