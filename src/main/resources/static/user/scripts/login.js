$logonForm = document.getElementById('login-form');

if (typeof localStorage.getItem('rememberedId') === 'string') {
    $logonForm['id'].value = localStorage.getItem('rememberedId');
    $logonForm['checkbox'].checked = true;
    $logonForm['password'].focus();
}

$logonForm.onsubmit = (e) => {
    e.preventDefault();
    if ($logonForm['id'].value.length >= 2 && $logonForm['id'].value.length <= 20) {
        alert('올바른 아이디를 입력해 주세요.');
    }
    if ($logonForm['password'].value.length >= 8 && $logonForm['password'].value.length <= 50) {
        alert('올바른 비밀번호를 입력해 주세요.');
    }
    const xhr = new XMLHttpRequest();
    const url = new URL(location.href);
    url.pathname = '/user/login';
    url.searchParams.set('id', $logonForm['id'].value);
    url.searchParams.set('password', $logonForm['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE ) {

        return;
        }
        if (xhr.status < 200 || xhr.status >= 300 ) {
        alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
        return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'success') {
            localStorage.setItem('rememberedId', $logonForm['id'].value);

            location.href = `/home/`;
        }
        else {
            localStorage.removeItem('rememberedId');
        }
    };
    xhr.open('POST', '/user/login');
    xhr.send();


}