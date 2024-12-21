const $cancel = document.querySelector('.cancel');
const $accept = document.querySelector('.accept');

$cancel.onclick = () => {
    window.close();
};


$accept.onclick = () => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {

            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'failure') {
            alert('회원탈퇴에 실패하였습니다. 잠시 후 다시 시도해주세요.');
        }
        if (response['result'] === 'success') {
            alert('회원 탈퇴가 완료되었습니다. 그동안 JGV를 이용해 주셔서 감사합니다.');
            window.close();
            window.opener.location.href = '/logout';

        }

    };
    xhr.open('DELETE', '/user/myPage/userWithdraw');
    xhr.send();
};

