const $main = document.getElementById('main');
const $mainContainer = $main.querySelector(':scope > .main-container');
const $mains = Array.from($mainContainer.querySelectorAll(':scope > .mains'));
const $controlBar = document.getElementById('control-bar');
const $seatContainer = $controlBar.querySelector(':scope > .container > .seat-container');
const $paymentContainer = $controlBar.querySelector(':scope > .container > .payment-container');
const $leftButtons = Array.from($controlBar.querySelectorAll(':scope > .container > .left-button'));
const $rightButtons = Array.from($controlBar.querySelectorAll(':scope > .container > .right-button'));
const $third = $controlBar.querySelector(':scope > .container > .third');
const $paymentSection = document.getElementById('payment-section');


const $mainPayment = document.getElementById("main-payment");
const $mainSeat = document.getElementById("main-seat");
const $payForm = $paymentSection.querySelector(':scope > .pay-form');
const $realMain = document.getElementById("main");
let t = 0;
let m = 0;
const $seatContent = $controlBar.querySelector(':scope > .container > .seat > .seat-content');
const $seatTitle = $controlBar.querySelector(':scope > .container > .seat > .seat-title');
const $seatCommon = document.createElement('span');
$seatCommon.className = 'seat-common data';
$seatTitle.appendChild($seatCommon);

const $theaterHuman = document.getElementById('theater-human');
const $seatHuman = document.createElement('span');
$seatHuman.className = 'seat-human data';
$theaterHuman.appendChild($seatHuman);

const $payPriceWon1 = document.getElementById("pay-price-won1");
const $payPriceWon2 = document.getElementById("pay-price-won2");
const $payPriceWon3 = document.getElementById("pay-price-won3");
const $payPriceWon4 = document.getElementById("pay-price-won4");
const $payPriceWon5 = document.getElementById("pay-price-won5");
const $payPriceWon6 = document.getElementById("pay-price-won6");


const adults = document.querySelectorAll('.adults');
const seats = [];


const table = document.createElement('table');
table.className = 'table';

const rows = ['A', 'B', 'C', 'D', 'E'];

const screen = document.querySelector("#screen");
if (screen) {
    screen.appendChild(table);
}

const $seatNumber = document.createElement('span');
$seatNumber.className = 'seat-number data';
$seatContent.appendChild($seatNumber);

let selectedSeats = [];


const $payButton = document.getElementById('pay-button');
const $realCancel = $payButton.querySelector(':scope > .real-cancel');
const $seatColor = document.getElementById('seat-color');
const $method = document.getElementById('method');


let selectedHuman = [];
const $priceTitle = $controlBar.querySelector(':scope > .container > .price > .price-title');
const $priceContent = $controlBar.querySelector(':scope > .container > .price > .price-content');
const $seatPriceCommon = document.createElement('span');
const $seatPricePay = document.createElement('span');
const $seatPrice = document.createElement('span');
const $seatPriceAdd = document.createElement('span');
$seatPriceCommon.className = 'seat-Price-Common head';
$seatPricePay.className = 'seat-Price-Pay head';
$seatPrice.className = 'seat-Price-Pay data';
$seatPriceAdd.className = 'seat-Price-Pay data2';
$priceTitle.appendChild($seatPriceCommon);
$priceContent.appendChild($seatPricePay);
$priceTitle.appendChild($seatPrice);
$priceContent.appendChild($seatPriceAdd);
let price = 0;

const $payHuman = $payForm.querySelector(':scope > .table-div > .div-content1 > .pay-human')
const $paySeat = $payForm.querySelector(':scope > .table-div > .div-content1 > .pay-seat')
const $payMovie = document.getElementById('pay-movie')
const $payTheater = document.getElementById('pay-theater')
const $payCinema = document.getElementById('pay-cinema')
const $payTime = document.getElementById('pay-time')
const $theaterTheater = document.getElementById('theater-theater');
const $theaterDay = document.getElementById('theater-day]');
const $theaterTime = document.getElementById('theater-time]');
const $theaterCinema = document.getElementById('theater-cinema');

const $checkboxAgreeAll = document.getElementById('checkbox-agree-all');
const $checkboxAgreeSolo = document.getElementById('checkbox-agree-solo');
const $checkboxAgrees = $payForm.querySelectorAll(':scope > .agree > .agree-div1 > .agree-label > .checkbox-agree');

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


$rightButtons.forEach((x) => {
    x.onclick = () => {
        $mains.forEach((main) => {
            main.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main' &&
                main.getAttribute('data-id') === 'main-seat') {
                main.classList.remove('hidden');


                const xhr = new XMLHttpRequest();
                const url = new URL(location.href); //ticket
                url.searchParams.set('thName', $theaterTheater.innerText);
                url.searchParams.set('ciName', $theaterCinema.innerText);
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    if (xhr.status < 200 || xhr.status >= 300) {
                        return;
                    }
                    const response = JSON.parse(xhr.responseText);
                    const result = response['result'];
                    const result2 = response['results'];
                    const result3 = response['resultss'];
                    console.log(result);
                    console.log(result2);
                    console.log(result3);
                    $seatColor.innerText = 40 - result.length;


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
                            if (result2.some(item => item.seName === td.id)) {
                                td.style.backgroundImage = "url('/ticket/assets/images/icon3.png')"; // 일치하면 배경 변경
                                td.style.backgroundSize = 'cover'; // 크기 조정
                                td.style.backgroundPosition = 'center'; // 위치 설정
                                td.style.pointerEvents = 'none';
                                td.innerText = '';
                            }
                        }
                        table.appendChild(tr);
                    });

                    adults.forEach((radio) => {
                        radio.addEventListener('change', () => {
                            t = parseInt(radio.value);
                            m = parseInt(radio.value);
                            if (radio.value !== '0') {
                                $seatCommon.textContent = '일반석';
                                $seatPriceCommon.textContent = '일반';
                                $seatPricePay.textContent = '총금액';
                                price = radio.value * result3[0].citPrice;
                                const price2 = price.toLocaleString();
                                const price3 = result3[0].citPrice.toLocaleString();

                                $payPriceWon1.textContent = `${price2}`;
                                $payPriceWon2.textContent = `${price2}`;
                                $payPriceWon3.textContent = `${price2}`;
                                $payPriceWon4.textContent = `${price2}`;
                                $payPriceWon5.textContent = `${price2}`;
                                $payPriceWon6.textContent = `${price2}`;

                                $seatPrice.textContent = `${price3} X ${radio.value}`;
                                $seatPriceAdd.textContent = `${price2}원`;
                                selectedHuman.push(`일반 ${(radio.value)} 명`);
                                $seatHuman.textContent = `${selectedHuman.join(', ')}`
                                $payHuman.textContent = selectedHuman;


                            } else {
                                $seatCommon.textContent = '';
                                $seatPriceCommon.textContent = '';
                                $seatPricePay.textContent = '';
                                $seatPrice.textContent = '';
                                $seatPriceAdd.textContent = '';
                                $seatHuman.textContent = `${selectedHuman.join(', ')}`;

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

                    seats.forEach((seat) => {
                        seat.addEventListener('click', () => {
                            if (t > 0 && !seat.classList.contains('selected-seat')) {
                                t--;
                                seat.classList.add('selected-seat');
                                selectedSeats.push(seat.id);
                                $seatNumber.textContent = `${selectedSeats.join(', ')}`;
                            } else {
                                for (j = 0; j < m; j++) {
                                    if (seat.id === selectedSeats[j]) {
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
                            $paySeat.textContent = selectedSeats;
                            // if (t === 0 && m !== 0) {
                            //     $seatRightButtonAfter.style.display = 'flex';
                            //     $seatRightButtonBefore.style.display = 'none';
                            // }

                        })
                    });
                }
                xhr.open('GET', url.toString()); //ticket/ciName=2관&thName=CGV대구
                xhr.send();
            }
            if (x.getAttribute('data-id') === 'main-seat' &&
                main.getAttribute('data-id') === 'main-payment') {
                main.classList.remove('hidden');
            }


            if ((x.getAttribute('data-id') === 'main-payment' &&
                main.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value !== "카드를 선택해주세요.") || (x.getAttribute('data-id') === 'main-payment' &&
                main.getAttribute('data-id') === 'main-payment' && pay !== "card")) {
                $paymentSection.classList.remove('hidden');

            } else if (x.getAttribute('data-id') === 'main-payment' &&
                main.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                main.classList.remove('hidden');
                alert("결제수단을 선택해주세요.")
            }
        })
        $rightButtons.forEach((button) => {
            button.classList.add('hidden');
            //오른쪽 버튼 바꾸기
            if (x.getAttribute('data-id') === 'main' &&
                button.getAttribute('data-id') === 'main-seat') {
                $seatContainer.classList.add('hidden');
                $paymentContainer.classList.add('hidden');
                button.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-seat' &&
                button.getAttribute('data-id') === 'main-payment') {
                button.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                button.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                button.classList.remove('hidden');
            }
        })
        // 메인 바꾸기



// 왼쪽버튼 바꾸기
        $leftButtons.forEach((left) => {
            left.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main' &&
                left.getAttribute('data-id') === 'main-seat') {
                left.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-seat' &&
                left.getAttribute('data-id') === 'main-payment') {
                left.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                left.getAttribute('data-id') === 'main-payment' && pay === "card" && $card.value === "카드를 선택해주세요.") {
                left.classList.remove('hidden');
            }
        })
    }
});

$leftButtons.forEach((x) => {
    x.onclick = () => {
        x.classList.add('hidden');
        $leftButtons.forEach((left) => {
            left.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                left.getAttribute('data-id') === 'main') {
                left.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                left.getAttribute('data-id') === 'main-seat') {
                left.classList.remove('hidden');
            }
        })
        $rightButtons.forEach((right) => {
            right.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                right.getAttribute('data-id') === 'main') {
                right.classList.remove('hidden');
                table.innerHTML = ""
                adults.forEach(radio => {
                    radio.checked = false;
                });
                $seatCommon.textContent = '';
                $seatPriceCommon.textContent = '';
                $seatPricePay.textContent = '';
                $seatPrice.textContent = '';
                $seatPriceAdd.textContent = '';
                selectedSeats = [];
                selectedHuman = [];
                $seatHuman.textContent = `${selectedHuman.join(', ')}`;
                $seatNumber.textContent = '';
                t = 0;
                m = 0;
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                right.getAttribute('data-id') === 'main-seat') {
                right.classList.remove('hidden');
            }
        })
        $mains.forEach((main) => {
            main.classList.add('hidden');
            if (x.getAttribute('data-id') === 'main-seat' &&
                main.getAttribute('data-id') === 'main') {
                $seatContainer.classList.remove('hidden');
                $paymentContainer.classList.remove('hidden');
                main.classList.remove('hidden');
            }
            if (x.getAttribute('data-id') === 'main-payment' &&
                main.getAttribute('data-id') === 'main-seat') {
                main.classList.remove('hidden');
            }
        })
    }
})
$realCancel.onclick = () =>{
    $paymentSection.classList.add('hidden');
    $mainPayment.classList.remove('hidden');
    $third.classList.remove('hidden');
}




$paymentCheck.forEach((radio) => {
    radio.addEventListener('change', () => {
            pay = String(`${(radio.value)}`);

            $payContainers.forEach(container => {
                container.style.display = 'none';
            });

            if (pay === "card") {
                $method.innerText = "신용카드"
                $cardContainer.style.display = 'block';
            } else if (pay === "cellPhone") {
                $method.innerText = "휴대폰 결제"
                $cellphoneContainer.style.display = 'block';
            } else if (pay === "simple-pay") {
                $method.innerText = "간편결제"
                $simplePayContainer.style.display = 'block';
                $simplePayCheck.forEach((radio) => {
                    radio.addEventListener('change', () => {
                        pay2 = String(`${(radio.value)}`);
                        $simplePayTextContainers.forEach(container => {
                            container.style.display = 'none';
                            if (pay2 === "NAVERPAY") {
                                $naverPayContainer.style.display = 'flex';
                            } else if (pay2 === "SMILEPAY") {
                                $smilePaynaverContainer.style.display = 'flex';
                            } else if (pay2 === "SSGPAY") {
                                $ssgPayContainer.style.display = 'flex';
                            } else if (pay2 === "KAKAOPAY") {
                                $kakaoPayContainer.style.display = 'flex';
                            } else if (pay2 === "PAYCO") {
                                $paycoContainer.style.display = 'flex';
                            }
                        });
                    })
                })
            } else if (pay === "credit") {
                $method.innerText = "내통장결제"

                $creditContainer.style.display = 'block';
            } else if (pay === "toss") {
                $method.innerText = "토스"
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




$checkboxAgreeAll.addEventListener('change', function () {
    $checkboxAgrees.forEach(checkbox => {
        checkbox.checked = $checkboxAgreeAll.checked;
    });
});
$checkboxAgrees.forEach(checkbox => {
    checkbox.addEventListener('change', function () {
        // 모든 체크박스가 체크되었는지 확인
        const allChecked = Array.from($checkboxAgrees).every(cb => cb.checked);
        $checkboxAgreeAll.checked = allChecked;
    });
});

function ReservationRefundRegulation() {
    window.open(
        "http://localhost:8080/ticket/ReservationRefundRegulations", // 팝업에서 열릴 URL
        "ReservationRefundRegulation",         // 팝업창 이름
        "width=600,height=800,left=200,top=200" // 크기와 위치
    );
}

function TCUElectronicFinancialTransactions() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/PGApply.htm", // 팝업에서 열릴 URL
        "TCUElectronicFinancialTransactions",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

function TCCollectingPersonalInformation() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/IDCollectApply.htm", // 팝업에서 열릴 URL
        "TCCollectingPersonalInformation",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

function TCPPIC() {
    window.open(
        "https://pay.cjsystems.co.kr/NewPayment/Apply/IDProvideApply.htm", // 팝업에서 열릴 URL
        "TCPPIC",         // 팝업창 이름
        "width=400,height=600,left=800,top=200" // 크기와 위치
    );
}

// $payForm.onsubmit = (e) => {
//     e.preventDefault();
//
//     if ($checkboxAgreeAll.checked && $checkboxAgreeSolo) {
//         const xhr = new XMLHttpRequest();
//         const formData = new FormData();
//         formData.append("paPrice", $payForm['pay-price-won5'].innerText);
//         formData.append("meNum", $payForm['method'].innerText);
//         formData.append("usNum", $payForm['user'].innerText);
//
//
//         xhr.onreadystatechange = () => {
//             if (xhr.readyState !== XMLHttpRequest.DONE) {
//                 return;
//             }
//             if (xhr.status < 200 || xhr.status >= 300) {
//                 alert("오류");
//                 return;
//             }
//
//         };
//         xhr.open('POST', location.href);
//         xhr.send(formData);
//     } else {
//         alert("약관을 모두 동의해주세요")
//
//     }
// };
//
// const seats = $payForm['pay-seat'];
// seats.forEach((seat, index) => {
//     formData.append(`seName${index}`, seat.innerText);
// });