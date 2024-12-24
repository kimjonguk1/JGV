const $modifyNickname = document.getElementById('modify-form');
const $cancel = $modifyNickname.querySelector('.cancel');

$cancel.onclick = () => {
    window.close();
}

$modifyNickname.onsubmit = (e) => {
    e.preventDefault();

    if ($modifyNickname['nickname'].value.length < 2 && $modifyNickname['nickname'].value.length > 10) {
        alert('올바른 닉네임을 입력해주세요 닉네임은 2~10 자리 입니다.');
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usNickname', $modifyNickname['nickname'].value);
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
            alert("올바르지 않은 양식 입니다. 닉네임은 2~10 자리 입니다.")
        }
        if (response['result'] === 'failure_duplicate_nickname') {
            alert("사용중인 닉네임 입니다.");
        }
        if (response['result'] === 'success') {
            alert("닉네임 변경에 성공하였습니다.");

            window.opener.location.reload();
            window.close();
        }
        else {
            alert("잘못된 접근입니다.");
        }

    };
    xhr.open('PATCH', '/user/myPage/modifyNickname');
    xhr.send(formData);

}