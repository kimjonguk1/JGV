const $findPassword = document.getElementById('find-password-form');
const $findResult = document.getElementById('find-result');

$findPassword.onsubmit = (e) => {
    e.preventDefault()

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usId', $findPassword['id'].value);
    formData.append('usEmail', $findPassword['email'].value);
    formData.append('usContact', $findPassword['contact'].value);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE ) {

        return;
        }
        if (xhr.status < 200 || xhr.status >= 300 ) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
        return;
        }
        try {
            const response = JSON.parse(xhr.responseText);

            switch (response['result']) {
                case 'failure_deleted':
                    alert('입력하신 회원 정보는 삭제 처리된 회원 입니다. 고객센터에 문의해주세요.');
                    break;
                case 'failure_suspended':
                    alert('정지된 회원 정보입니다. 고객센터에 문의해주세요.');
                    break;
                case 'failure_verified':
                    alert('이메일 인증이 완료되지 않은 회원입니다. 인증 후 다시 시도해주세요.');
                    break;
                case 'failure':
                    alert('입력하신 정보가 올바르지 않습니다. 다시 입력해 주세요.');
                    break;
                case 'success':
                    alert('입력하신 이메일로 비밀번호로 재설정 할 수 있는 링크를 포함한 메일을 전송하였습니다. 확인을 클릭하면 로그인 페이지로 이동합니다.');
                    break;
                default:
                    alert('알 수 없는 이유로 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                    break;
            }
        } catch (error) {
            alert('서버 응답 처리 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    };
    xhr.open('POST', location.href);
    xhr.send(formData);


}