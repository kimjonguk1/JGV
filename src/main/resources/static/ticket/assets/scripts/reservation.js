const $body = document.querySelector('body');

window.onload = () => {
    const paymentComplete = sessionStorage.getItem('paymentComplete');

    if (paymentComplete !== 'true') {
        // 결제가 완료되지 않았다면 다른 페이지로 리디렉션
        window.location.href = 'http://localhost:8080/'; // 결제 페이지로 리디렉션
    } else {
        $body.style.display = 'flex';
        // 결제 완료된 경우, 예매 정보 가져오기이이이이이이이이이!!!!!!!!!!!!
        const meName = sessionStorage.getItem('meName');
        const moTitle = sessionStorage.getItem('moTitle');
        const ciName = sessionStorage.getItem('ciName');
        const thName = sessionStorage.getItem('thName');
        const seName = sessionStorage.getItem('seName');
        const scStartDate = sessionStorage.getItem('scStartDate');
        const paPrice = sessionStorage.getItem('paPrice');
        const paPrice2 = sessionStorage.getItem('paPrice');
        const human = sessionStorage.getItem('human');
        const poster = sessionStorage.getItem('poster');
        const paymentNumber = sessionStorage.getItem('paymentNumber');

        // 예매 정보 표시이이이이이이이!!!!!!!!!!!!!!!!!!!!!!!!!
        document.getElementById('meName').innerText = meName;
        document.getElementById('movie').innerText = moTitle;
        document.getElementById('ciName').innerText = ciName;
        document.getElementById('thName').innerText = thName;
        document.getElementById('seName').innerText = seName;
        document.getElementById('scStartDate').innerText = scStartDate;
        document.getElementById('paPrice').innerText = paPrice;
        document.getElementById('paPrice2').innerText = paPrice2;
        document.getElementById('human').innerText = human;
        document.getElementById('poster').src = poster;
        document.getElementById('paymentNumber').innerText = paymentNumber;

        console.log("paymentNumber")
        // 결제 완료 후 예매 정보만 보이도록 설정
        sessionStorage.removeItem('paymentComplete');  // 예매 완료 후 sessionStorage에서 결제 완료 상태 제거
        // 세션 정보 제거 (결제 완료 후)
        sessionStorage.clear();  // 세션 스토리지의 모든 데이터 제거

    }
};