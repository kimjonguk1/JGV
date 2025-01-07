HTMLElement.prototype.hide = function () {
    this.classList.remove('-visible');
    return this;
}

HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    return this;
}

class Loading {
    /** @type {HTMLElement} */
    static $element;

    static hide() {
        Loading.$element?.hide();
    }

    /**
     * @param {number} delay
     */
    static show(delay = 50) {
        if (Loading.$element == null) {
            const $element = document.createElement('div');
            $element.classList.add('---loading');
            const $icon = document.createElement('img');
            $icon.classList.add('_icon');
            $icon.setAttribute('alt', '');
            $icon.setAttribute('src', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAAsTAAALEwEAmpwYAAAC7ElEQVR4nO3aT6hVVRTH8V1PzTTCf6ApCA0KKnRg0FBUFJ9FZhBB6iQwB6KIUweWk6BEGjUpcCJOnDQoREEoK7KwfxQlr7ICywYROEnTkk9szrpwBtf7zr2e++45j/eFOzpnr/VbZ5+99j5r3ZRmmKbgfmzGYZzC17iMv/EP/sJFvIcjeAYLUhPArBCUxf2nf/KY09iBe0YVwF5cKYm6ifPxtJ/FGqzE/CwSi/BYXDuED2KmOvyOA5g7VUFswnclARdDwKIBbC3AbnxRsvcTxoc9C2+UHP6Ap3BXTfbH8VXJ/pu1zw6W4Vw4uI6Dw3inMYb9uBa+LmBpXcYfwI9h+Dc8UYvh3j5XYyJ8Zt8P3qnBxfg2DH6Wg6pNbTXf50vBDDYzmINPw9A3WFi72sk1zC8Fc2GgNYPXwsCvWDEUpdVnZqKTAPodvB63Ym9YMzSV/a2ZaxHMeD9pNqfWzOHUEBTZrLNeJn/F8GIMyIt8TmoIitTc2WcOTHbzbFyKm59PDQNbQtuVnrOC7aXpG0sNBJ+Hxh29bjoTN+1JDUVxNsuc7nWAuxm/Kd8zqqLQmU/N/3b9nsG6iPRsajh4P7RuvV3a3YOHU8PByxHIkdRmsC0CeTe1GTwagUykNqM4f2X+7HbxBI6nFqA4mWdudLuY01lmVmoB+BAfdbtwNQJp7B5SidKJ95HUZhSFsu6bTJvA6xHIK6nNKCqA02KTGcOrWDtqLTNMa9RU0x0piqLYmaZ+7lYCd5cKEL2rFU1H0S4QRbFVqc3grVJFpe8mTmPAvaXSyyeYNwINs/H4HSceLMfPEczG2hRWr8R/HL6frqtb9cJUf6fgyVI3YMr6MrWjqOpsr631dpu1k9Pzl3gup+ua7NZipx+HuWf+fUx7p2q/Ky/MAWzNxUvxUP7Ir/FwVPf++N8d72+HfQPYOVoa/wuWDEdxtYDy3y+O4aEuT7vTDc6802X8BpzEzpH8haMKuK/UEc68XWngDGl68D/icIn0PTqFnwAAAABJRU5ErkJggg==')
            const $text = document.createElement('span');
            $text.classList.add('_text');
            $text.innerText = '잠시만 기다려 주세요.';
            $element.append($icon, $text);
            document.body.prepend($element);
            Loading.$element = $element;
        }
        setTimeout(() => Loading.$element.show(), delay);
    }
}

//검색 기능 추가
document.addEventListener('DOMContentLoaded', () => {
    const $form = document.querySelector('.nav > .nav-wrapper > .search-wrapper > .search-form')
    if($form) {
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
                    window.location.href = searchUrl;
                } else {
                    alert("서버가 알수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.");
                }
            };

            xhr.open('GET', searchUrl);
            xhr.send(); // GET 요청에는 본문(formData)이 필요하지 않습니다.
        }
    }
})