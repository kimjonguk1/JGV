const $special = Array.from(document.querySelectorAll('.special'));
const $items = Array.from(document.querySelectorAll('.item-container > .item > .text'));

$items.forEach((x) => {
    x.onmouseover = () => {
        $special.forEach((special) => {
            special.classList.add('hidden');
            if (special.getAttribute('rel') === x.getAttribute('rel')) {
                special.classList.remove('hidden');
            }
        })
    }
})
