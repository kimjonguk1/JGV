const $reservationCancel = document.querySelector('.reservation-cancel-button');

$reservationCancel.onclick = () => {
    window.open(
        "http://localhost:8080/user/myPage/reservationCancel",
        "예매취소",
        "width=600,height=800,left=200,top=200"
    );
};



