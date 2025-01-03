const $recoverPassword = document.getElementById('new-password-form');
const urlParams = new URLSearchParams(window.location.search);
const emEmail = urlParams.get('userEmail');
const emKey = urlParams.get('key');

const $passwordInput = $recoverPassword[`recover-password`];
const $strengthDisplay = document.getElementById('password-strength');

const $lowercaseRegex = /[a-z]/;
const $uppercaseRegex = /[A-Z]/;
const $numberRegex = /\d/;
const $specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;

$passwordInput.addEventListener('keyup', (e) => {
    const $password = e.target.value;
    let strength = 0;
    if ($password.length > 6) {
        if ($lowercaseRegex.test($password)) strength++;
        if ($uppercaseRegex.test($password)) strength++;
        if ($numberRegex.test($password)) strength++;
        if ($specialCharRegex.test($password)) strength++;
    }

    let strengthText = '';
    let strengthColor = '';

    if (strength <= 1) {
        strengthText = '약함';
        strengthColor = '#FB4357';
    } else if (strength === 2) {
        strengthText = '보통';
        strengthColor = '#e6731f';
    } else if (strength > 2) {
        strengthText = '강함';
        strengthColor = '#45c645';
    }


    if ($password === '') {
        $strengthDisplay.style.display = 'none';
    } else {
        $strengthDisplay.style.display = 'block';
    }
    $strengthDisplay.textContent = `비밀번호 강도: ${strengthText}`;
    $strengthDisplay.style.color = strengthColor;
})

$recoverPassword.onsubmit = (e) => {
    e.preventDefault();

    if ($recoverPassword['recover-password'].value.length < 6 && $recoverPassword['recover-password'].value.length > 50) {
        alert('올바른 비밀번호를 입력해 주세요.');
        return;
    }
    if ($recoverPassword['recover-password'].value !== $recoverPassword['recover-password-check'].value) {
        alert('비밀번호가 서로 일치하지 않습니다.');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('userEmail', emEmail);
    formData.append('key', emKey);
    formData.append('usPw', $recoverPassword['recover-password'].value);


    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {

            return;
        }
        Loading.hide();
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
        const response = JSON.parse(xhr.responseText);

        if (response['result'] === 'success') {
            alert('새로운 비밀번호가 설정 되었습니다. 확인을 클릭하시면 로그인 페이지로 이동합니다.');
            return location.href = `/user/login`;

        }
        else if (response['result'] === 'failure') {
            alert('올바른 비밀번호 형식이 아닙니다. 비밀번호는 8~100자 사이에 대소문자, 숫자, 특수문자를 포함해야 합니다.');
            return;
        }

        else if (response['result'] === 'failure_invalid_password') {
            alert('비밀번호는 8~100자 사이에 대소문자, 숫자, 특수문자를 포함해야 합니다.');
            return;
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
    };
    xhr.open('PATCH', '/user/find-password-result');
    xhr.send(formData);
    Loading.show(0);

}