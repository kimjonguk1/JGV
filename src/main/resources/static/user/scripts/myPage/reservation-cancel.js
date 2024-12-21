const $reservationCancel = document.querySelector('.cancel');

$reservationCancel.onclick = () => {

    const xhr = new XMLHttpRequest();

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
            alert("알 수 없는 이유로 예매취소에 실패하였습니다. 잠시 후 다시 시도해주세요.");
        }

        if (response['result'] === 'failure_cancel_complete') {
            alert('이미 취소된 영화 입니다.');
        }

        if (response['result'] === 'success') {
            alert("예매가 취소 되었습니다.");
            window.opener.location = '/user/myPage/reservation';
            window.close();
        }
        else {
            alert("잘못된 접근입니다.");
        }

    };
    xhr.open('PATCH', '/user/myPage/reservationCancel');
    xhr.send();

};