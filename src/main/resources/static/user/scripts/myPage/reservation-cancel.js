const $reservationCancel = document.querySelector('.reservation-info');


$reservationCancel.onsubmit = (event) => {
    event.preventDefault();  // 폼 제출 시 페이지 리로드 방지

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    // paPrice 값을 숫자 형식으로 변환
    const paPriceText = document.getElementById("paPrice").innerText;  // "38,000원"
    const paPriceInt = parseInt(paPriceText.replace(/[,원]/g, ''));   // "38,000원" → "38000" → 38000

    // FormData에 추가
    formData.append("paNum", document.getElementById("paNum").innerText);
    formData.append("paPrice", paPriceInt);  // 숫자를 그대로 추가
    formData.append("paCreatedAt", document.getElementById("paCreatedAt").innerText);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }

        // 서버 응답 확인
        const response = JSON.parse(xhr.responseText);

        if (response['result'] === 'failure') {
            alert("알 수 없는 이유로 예매취소에 실패하였습니다. 잠시 후 다시 시도해주세요.");
            window.close();  // 팝업을 자동으로 닫지 않음
        }

        if (response['result'] === 'failure_cancel_complete') {
            alert('이미 취소된 영화 입니다.');
            window.close();  // 팝업을 자동으로 닫지 않음
        }

        if (response['result'] === 'success') {
            alert("예매가 취소 되었습니다.");
            // 팝업에서 DOM 갱신 후 팝업 창을 닫지 않도록
            window.opener.location = '/user/myPage/reservation';  // 메인 페이지 리로드
            window.close();  // 팝업을 자동으로 닫지 않음
        } else {
            alert("잘못된 접근입니다.");
            window.close();  // 팝업을 자동으로 닫지 않음

        }
    };

    // 요청 보내기
    xhr.open('PATCH', '/user/myPage/reservationCancel');
    xhr.send(formData);
};