const $loginForm = document.getElementById('login-form');


const savedId = localStorage.getItem('rememberId');
if (savedId) {
    $loginForm['id'].value = savedId;
    $loginForm['rememberId'].checked = true;
    $loginForm['password'].focus();
}
$loginForm.addEventListener('submit', () => {
    const isRememberChecked = $loginForm['rememberId'].checked;
    const enteredId = $loginForm['id'].value.trim();

    if (isRememberChecked) {

        localStorage.setItem('rememberId', enteredId);
    } else {

        localStorage.removeItem('rememberId');
    }
});


// region 로그인
$loginForm.onsubmit = (e) => {
    e.preventDefault();

    if ($loginForm['id'].value.length <= 6 && $loginForm['id'].value.length >= 20) {
        alert('올바른 아이디를 입력해 주세요.');
    }
    if ($loginForm['password'].value.length <= 6 && $loginForm['password'].value.length >= 20) {
        alert('올바른 아이디를 입력해 주세요.');
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usId', $loginForm['id'].value);
    formData.append('usPw', $loginForm['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {

            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');

            return;
        }
        const response = JSON.parse(xhr.responseText);

        if (response['result'] === 'success') {
            sessionStorage.setItem('user', response['MemberNum']);
            const redirectUrl = response['redirect'] || '/';  // 서버에서 리디렉션 URL 받기
            location.href = redirectUrl;  // 리디렉션
        } else if (response['result'] === 'failure_suspended') {
            alert('해당 계정은 이용이 정지된 상태입니다. 관리자에게 문의해 주세요.');
        } else if (response['result'] === 'failure_not_verified') {
            alert(`해당 계정의 이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.<br><br>혹시 이메일이 오지 않았다면 인증 링크가 포함된 이메일을 <a href="/user/resend-register-email-token?email=${$loginForm['email'].value}" target="_blank">다시 전송</a>할 수 있습니다.`)
        } else if (response['result'] === 'failure') {
            alert('올바른 아이디, 비밀번호를 입력해 주세요.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    };
    xhr.open('POST', '/user/login');
    xhr.send(formData);


}
// endregion


