const _isSocialRegister = document.head.querySelector(':scope > meta[name="_isSocialRegister"]')?.getAttribute('content') === 'true';
/** @type {HTMLFormElement} */
const $loginForm = document.getElementById('loginForm');
/** @type {HTMLFormElement} */
const $registerForm = document.getElementById('registerForm');

{
    if ($loginForm instanceof HTMLFormElement) {
        /** @param {PointerEvent} e */
        $loginForm.querySelector(':scope > .menu > .item > .link[rel="register"]').onclick = (e) => {
            e.preventDefault();
            $loginForm.hide();
            $registerForm.reset();
            $registerForm.show();
            $registerForm['email'].focus();
        };

        /** @param {PointerEvent} e */
        $loginForm.querySelector(':scope > .menu > .item > .link[rel="recover"]').onclick = (e) => {
            e.preventDefault();
            alert('ㅋ');
        };

        /** @param {SubmitEvent} e */
        $loginForm.onsubmit = (e) => {
            e.preventDefault();
            if (!$loginForm['email'].value.lengthBetween(6, 50)) {
                alert('올바른 이메일을 입력해 주세요.');
                $loginForm['email'].focusAndSelect();
                return;
            }
            if (!$loginForm['password'].value.lengthBetween(6, 50)) {
                alert('올바른 비밀번호를 입력해 주세요.');
                $loginForm['password'].focusAndSelect();
                return;
            }
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('email', $loginForm['email'].value);
            formData.append('password', $loginForm['password'].value);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) return;
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('로그인 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                const result = response['result'];
                if (result === 'failure') {
                    alert('이메일 혹은 비밀번호가 올바르지 않습니다. 다시 확인해 주세요.');
                    $loginForm['password'].focusAndSelect();
                } else if (result === 'success') {
                    location.href = './my';
                } else {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            };
            xhr.open('POST', './login');
            xhr.send(formData);
        };
    }
}

{
    /** @param {PointerEvent} e */
    $registerForm['back'].onclick = (e) => {
        e.preventDefault();
        if (_isSocialRegister === true) {
            location.href = '../login';
        } else {
            $loginForm.show();
            $loginForm['email'].focusAndSelect();
            $registerForm.reset();
            $registerForm.hide();
        }
    };

    /** @param {SubmitEvent} e */
    $registerForm.onsubmit = (e) => {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        const url = new URL(location.href);
        formData.append("code", url.searchParams.get('code'))
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) return;
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('회원가입 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                return;
            }
            const response = JSON.parse(xhr.responseText);
            const result = response['result'];
            if (result === 'failure') {
                alert('알 수 없는 이유로 회원가입에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
            } else if (result === 'failure_duplicate_email') {
                alert(`같은 이메일 정보를 가진 계정이 있음을 확인했습니다. 계정 통합을 완료했으며, 확인을 누를시 로그인 페이지로 이동합니다.`);
                location.href = '../login';
            } else if (result === 'failure_duplicate_nickname') {
                alert(`입력하신 닉네임 [${$registerForm['nickname'].value}]은 이미 사용 중입니다. 다른 닉네임을 사용해 주세요.`);
            } else if (result === 'success') {
                alert('회원가입에 성공하였습니다. 확인을 클릭하면 로그인 페이지로 이동합니다.');
                location.href = '../login';
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        };
        xhr.open('POST', _isSocialRegister === true ? '../social' : './social');
        xhr.send(formData);
    };
}