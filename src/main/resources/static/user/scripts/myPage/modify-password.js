const $modifyPassword = document.getElementById('modify-form');
const $cancel = $modifyPassword.querySelector('.cancel');

$cancel.onclick = () => {
    window.close();
}

$modifyPassword.onsubmit = (e) => {
    e.preventDefault();

    if ($modifyPassword['password'].value.length < 8 && $modifyPassword['password'].value.length > 20) {
        alert('올바른 비밀번호를 입력해주세요 비밀번호는 8~20 자리 입니다.');
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usPw', $modifyPassword['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE ) {

        return;
        }
        if (xhr.status < 200 || xhr.status >= 300 ) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
        return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'failure') {
            alert("올바르지 않은 양식 입니다. 비밀번호는 8~20 자리 입니다.")
        }

        if (response['result'] === 'success') {
            alert("비밀번호 변경에 성공하였습니다.");
            window.opener.location = '/user/myPage/main';
            window.close();
        }
        else {
            alert("잘못된 접근입니다.");
        }

    };
    xhr.open('PATCH', '/user/myPage/modifyPassword');
    xhr.send(formData);

}