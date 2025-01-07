const $reservationCancel = document.querySelector('.reservation-info');

// 메시지 수신 이벤트 핸들러
window.addEventListener("message", (event) => {
    const data = event.data;

    // 데이터가 없으면 처리하지 않음
    if (!data || Object.values(data).some(value => !value)) {
        alert("필요한 데이터가 없습니다. 창을 닫습니다.");
        window.close();  // 팝업을 닫습니다.
        return;
    }
    //
    // // 전달받은 데이터를 콘솔에 출력
    // console.log("팝업에서 받은 데이터:", data);

    // 전달받은 데이터를 DOM에 반영
    document.getElementById("paNum").innerText = data.paNum || "데이터 없음";
    document.getElementById("moTitle").innerText = data.moTitle || "데이터 없음";
    document.getElementById("paPrice").innerText = data.paPrice || "데이터 없음";
    document.getElementById("paPrice2").innerText = data.paPrice || "데이터 없음";
    document.getElementById("thName").innerText = data.thName || "데이터 없음";
    document.getElementById("seHuman").innerText = data.seHuman || "데이터 없음";
    document.getElementById("scStartDate").innerText = data.scStartDate || "데이터 없음";
    document.getElementById("meName").innerText = data.meName || "데이터 없음";
    document.getElementById("paCreatedAt").innerText = data.paCreatedAt || "데이터 없음";
});

$reservationCancel.onsubmit = (event) => {
    event.preventDefault();  // 폼 제출 시 페이지 리로드 방지

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const sessionUsId = sessionStorage.getItem('user').trim();  // 세션에서 userId를 가져오기

    // paPrice 값을 숫자 형식으로 변환
    const paPriceText = document.getElementById("paPrice").innerText;  // "38,000원"
    const paPriceInt = parseInt(paPriceText.replace(/[,원]/g, ''));   // "38,000원" → "38000" → 38000

    // FormData에 추가
    formData.append("usNum", sessionUsId);
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

// F5 또는 새로 고침을 눌렀을 때 팝업을 닫도록 keydown 이벤트 추가
window.addEventListener('keydown', (event) => {
    if (event.key === 'F5' || (event.key === 'r' && event.ctrlKey)) {
        alert("잘못된 접근입니다.");
        window.close();  // 팝업을 자동으로 닫지 않음
        event.preventDefault();  // 새로 고침 방지
        window.close();  // 팝업을 닫음
    }
});