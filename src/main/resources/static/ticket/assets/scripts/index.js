//seat
const $mainPayment = document.getElementById("main-payment");
const $mainSeat = document.getElementById("main-seat");
const $paymentSection = document.getElementById("payment-section");
let t = 0;
let m = 0;

const $section = document.getElementById("control-bar");
const $seatContent = $section.querySelector(':scope > .space > .seat-content');
const $seatCommon = document.createElement('span');
$seatCommon.className = 'seat-common';
$seatContent.appendChild($seatCommon);

const $theaterContent = $section.querySelector(':scope > .space > .theater-content');
const $seatHuman = document.createElement('span');
$seatHuman.className = 'seat-human';
$theaterContent.appendChild($seatHuman);


const adults = document.querySelectorAll('.adults');
const seats = [];


const table = document.createElement('table');
table.className = 'table';

const rows = ['A', 'B', 'C', 'D', 'E'];
rows.forEach(row => {
    const tr = document.createElement('tr'); // 행 생성
    const th = document.createElement('th');
    th.textContent = row;
    tr.appendChild(th);
    for (let i = 1; i <= 8; i++) {
        const td = document.createElement('td');
        td.id = `${row}${i}`;
        td.className = 'seat';
        td.textContent = i;
        tr.appendChild(td);
        seats.push(td);
    }
    table.appendChild(tr);
});
const screen = document.querySelector("#screen");
if (screen) {
    screen.appendChild(table);
}

const $seatNumber = document.createElement('span');
$seatNumber.className = 'seat-number';
$seatContent.appendChild($seatNumber);


let selectedSeats = [];

const $controlBar = document.getElementById("control-bar");
const $seatRightButtonBefore = $controlBar.querySelector(':scope > .space > .seat-right-button-before');
const $seatRightButtonAfter = $controlBar.querySelector(':scope > .space > .seat-right-button-after');
const $LeftButton = $controlBar.querySelector(':scope > .space > .left-button');
const $paymentButton = $controlBar.querySelector(':scope > .space > .payment-button');
const $payForm = document.getElementById('pay-form');
const $realCancel = $payForm.querySelector(':scope > .real-cancel');

seats.forEach((seat) => {
    seat.addEventListener('click', () => {
        if (t > 0 && !seat.classList.contains('selected-seat')) {
            t--;
            seat.classList.add('selected-seat');
            selectedSeats.push(seat.id);
            $seatNumber.textContent = `${selectedSeats.join(', ')}`;
        } else {
            for (j = 0; j < m; j++) {
                if (seat.id == selectedSeats[j]) {
                    t++;
                    seat.classList.remove('selected-seat');
                    const index = selectedSeats.indexOf(seat.id);
                    if (index !== -1) {
                        selectedSeats.splice(index, 1);
                    }
                    $seatNumber.textContent = `${selectedSeats.join(', ')}`;
                    // else if (seat.classList.contains('selected-seat')) {
                    //         t++;
                    //         seat.classList.remove('selected-seat');
                    //         const index = selectedSeats.indexOf(seat.id);
                    //         if (index !== -1) {
                    //             selectedSeats.splice(index, 1);
                    //         }
                    //         $seatNumber.textContent = `${selectedSeats.join(', ')}`;
                }
            }
        }
        $seatRightButtonBefore.style.display = 'flex';
        $seatRightButtonAfter.style.display = 'none';
        $seatRightButtonBefore.style.background = 'rgb(51, 51, 51)';
        if (t === 0 && m !== 0) {
            $seatRightButtonAfter.style.display = 'flex';
            $seatRightButtonBefore.style.display = 'none';
            $seatRightButtonBefore.style.background = 'red';
        }

    })
});
let selectedHuman = [];
const $priceTitle = $controlBar.querySelector(':scope > .space > .price-title');
const $priceContent = $controlBar.querySelector(':scope > .space > .price-content');
const $seatPriceCommon = document.createElement('span');
const $seatPricePay = document.createElement('span');
const $seatPrice = document.createElement('span');
const $seatPriceAdd = document.createElement('span');
$seatPriceCommon.className = 'seat-Price-Common';
$seatPricePay.className = 'seat-Price-Pay';
$seatPrice.className = 'seat-Price-Pay';
$seatPriceAdd.className = 'seat-Price-Pay';
$priceTitle.appendChild($seatPriceCommon);
$priceTitle.appendChild($seatPricePay);
$priceContent.appendChild($seatPrice);
$priceContent.appendChild($seatPriceAdd);
let price = 0;

adults.forEach((radio) => {
    radio.addEventListener('change', () => {
        t = parseInt(radio.value);
        m = parseInt(radio.value);
        if (radio.value !== '0') {
            $seatCommon.textContent = '일반석';
            $seatPriceCommon.textContent = '일반';
            $seatPricePay.textContent = '총금액';
            price = radio.value * 15_000;
            const price2 = price.toLocaleString();

            $seatPrice.textContent = `15,000원 X ${radio.value}`;
            $seatPriceAdd.textContent = `${price2}원`;
            selectedHuman.push(`일반 ${(radio.value)} 명`);
            $seatHuman.textContent = `${selectedHuman.join(', ')}`;


        } else {
            $seatCommon.textContent = '';
            $seatPriceCommon.textContent = '';
            $seatPricePay.textContent = '';
            $seatPrice.textContent = '';
            $seatPriceAdd.textContent = '';
        }

        seats.forEach((seat) => {
            seat.classList.remove('selected-seat');
            const index = selectedHuman.indexOf(`일반 ${(radio.value)} 명`);
            if (index !== -1) {
                selectedHuman.splice(index, 1);
            }
        });
        selectedSeats = [];
        $seatNumber.textContent = '';

    });
});


//payment
let pay = "card";
let pay2 = "";
const $paymentCheck = document.querySelectorAll('input[name="payment-check"]');
const $payContainers = $mainPayment.querySelectorAll(':scope > .left-container > .pay-containers');
const $cardContainer = $mainPayment.querySelector(':scope > .left-container > .card-container');
const $card = document.querySelector('[name = card]')
const $cellphoneContainer = $mainPayment.querySelector(':scope > .left-container > .cellphone-container');
const $simplePayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container');
const $creditContainer = $mainPayment.querySelector(':scope > .left-container > .credit-container');
const $tossContainer = $mainPayment.querySelector(':scope > .left-container > .toss-container');


const $simplePayCheck = document.querySelectorAll('input[name="simple-pay-check"]');
const $simplePayTextContainers = $mainPayment.querySelectorAll(':scope > .left-container > .simple-pay-container > .simple-pay-text-container');
const $naverPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .naver-pay-container')
const $smilePaynaverContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .smile-pay-container')
const $ssgPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .ssg-pay-container')
const $kakaoPayContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .kakao-pay-container')
const $paycoContainer = $mainPayment.querySelector(':scope > .left-container > .simple-pay-container > .payco-container')
const $cultureCard = document.querySelector('input[name="culture-card"]');
const $noneCulturePayText = $mainPayment.querySelector(':scope > .left-container >.simple-pay-container > .naver-pay-container > .none-culture-pay-text')
const $CulturePayText = $mainPayment.querySelector(':scope > .left-container >.simple-pay-container > .naver-pay-container > .culture-pay-text')
let page = 0;

$paymentCheck.forEach((radio) => {
    radio.addEventListener('change', () => {
            pay = String(`${(radio.value)}`);

            $payContainers.forEach(container => {
                container.style.display = 'none';
            });

            if (pay === "card") {
                $cardContainer.style.display = 'block';
            } else if (pay === "cellPhone") {
                $cellphoneContainer.style.display = 'block';
            } else if (pay === "simple-pay") {
                $simplePayContainer.style.display = 'block';
                $simplePayCheck.forEach((radio) => {
                    radio.addEventListener('change', () => {
                        pay2 = String(`${(radio.value)}`);
                        $simplePayTextContainers.forEach(container => {
                            container.style.display = 'none';
                            if (pay2 === "NAVERPAY") {
                                $naverPayContainer.style.display = 'block';
                            } else if (pay2 === "SMILEPAY") {
                                $smilePaynaverContainer.style.display = 'block';
                            } else if (pay2 === "SSGPAY") {
                                $ssgPayContainer.style.display = 'block';
                            } else if (pay2 === "KAKAOPAY") {
                                $kakaoPayContainer.style.display = 'block';
                            } else if (pay2 === "PAYCO") {
                                $paycoContainer.style.display = 'block';
                            }
                        });
                    })
                })
            } else if (pay === "credit") {
                $creditContainer.style.display = 'block';
            } else if (pay === "toss") {
                $tossContainer.style.display = 'block';
            }
        }
    )
})
$cultureCard.addEventListener('change', () => {
    if ($cultureCard.checked) {
        $noneCulturePayText.style.display = 'none';
        $CulturePayText.style.display = 'flex';
    } else {
        $noneCulturePayText.style.display = 'flex';
        $CulturePayText.style.display = 'none';
    }
})

$seatRightButtonAfter.onclick = () => {
    $mainPayment.style.display = 'flex';
    $mainSeat.style.display = 'none';
    $seatRightButtonBefore.style.display = 'flex';
    $seatRightButtonAfter.style.display = 'none';
    $seatRightButtonBefore.style.background = 'rgb(51, 51, 51)';
    $seatRightButtonBefore.style.display = 'none';
    $paymentButton.style.display = 'flex';
    $priceContent.style.display = 'none';
    $priceTitle.style.display = 'none';
    page = 3;
};

$LeftButton.onclick = () => {
    if (page === 3) {
        $mainPayment.style.display = 'none';
        $mainSeat.style.display = 'flex'
        $seatRightButtonAfter.style.display = 'flex';
        $paymentButton.style.display = 'none';
        $priceContent.style.display = 'flex';
        $priceTitle.style.display = 'flex';
        page = 2;
    } else if (page === 2) {
    }
};

$paymentButton.onclick = () => {
    if (page === 3 && pay === "card" && $card.value !== "카드를 선택해주세요."){
        $paymentSection.style.display = 'flex';
        $mainPayment.style.pointerEvents = 'none';
    }
    if (page === 3 && pay !== "card") {
        $paymentSection.style.display = 'flex';
        $mainPayment.style.pointerEvents = 'none';
    }
}
$realCancel.addEventListener('click', () => {
    if (page === 3) {
        $paymentSection.style.display = 'none';
        $mainPayment.style.pointerEvents = 'auto';
    }
})