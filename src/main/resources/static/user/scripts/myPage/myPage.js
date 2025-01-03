{
    const $advertisementArray = ['https://adimg.cgv.co.kr/images/202412/Moana2/1218_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1224_980x80.png', 'https://adimg.cgv.co.kr/images/202411/jjanggu/1209_980x80.png']
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.getElementById('advertisement');
        const $advertisementRandom = $advertisementArray[Math.floor(Math.random() * $advertisementArray.length)];
        const $img = $advertisement.querySelector(':scope > a > img');
        if ($advertisementRandom === $advertisementArray[0]) {
            $advertisement.style.backgroundColor = '#2B53AB'
        } else if ($advertisementRandom === $advertisementArray[1]) {
            $advertisement.style.backgroundColor = '#4184D2'
        } else if ($advertisementRandom === $advertisementArray[2]) {
            $advertisement.style.backgroundColor = '#191413'
        } else {
            $advertisement.style.backgroundColor = '#2B82DD'
        }
        $img.setAttribute('src', $advertisementRandom);
    });
}

function ticketCancel(index) {
    // 선택된 항목의 데이터를 가져옵니다.
    const paNum = document.getElementById(`paNum-${index}`).innerText;
    const moTitle = document.getElementById(`moTitle-${index}`).innerText;
    const paPrice = document.getElementById(`paPrice-${index}`).innerText;
    const thName = document.getElementById(`thName-${index}`).innerText;
    const seHuman = document.getElementById(`seHuman-${index}`).innerText;
    const scStartDate = document.getElementById(`scStartDate-${index}`).innerText;
    const meName = document.getElementById(`meName-${index}`).innerText.slice(2); // '메뉴:' 제거
    const paCreatedAt = document.getElementById(`paCreatedAt-${index}`).innerText;

    // 팝업 데이터를 객체로 준비
    const popupData = {
        paNum,
        moTitle,
        paPrice,
        thName,
        seHuman,
        scStartDate,
        meName,
        paCreatedAt,
    };

    // 팝업 창 열기
    const popupWindow = window.open(
        "./reservationCancel",
        "예매취소",
        "width=600,height=800,left=200,top=200"
    );

    // 팝업이 로드된 후 데이터를 전달
    const checkPopupLoaded = setInterval(() => {
        if (popupWindow && popupWindow.document.readyState === "complete") {
            popupWindow.postMessage(popupData, "*");
            clearInterval(checkPopupLoaded); // 타이머 종료
        }
    }, 100);
}

const $modal = document.querySelector('.reserve-modal');
const $textarea = document.getElementById('reviewText');
const $modalMovieTitle = document.getElementById('modalMovieTitle');
const $modalUserName = document.getElementById('modalUserName');
const $submitReview = document.getElementById('submitReview');
const $closeModal = document.getElementById('closeModal');
if($modal) {
    $modal.style.display = 'none';
}


// UTF-8 바이트 계산 함수
function getByteLength(str) {
    let byteLength = 0;
    for (let i = 0; i < str.length; i++) {
        const charCode = str.charCodeAt(i);
        if (charCode <= 0x7f) {
            byteLength += 1; // ASCII 문자: 1바이트
        } else if (charCode <= 0x7ff) {
            byteLength += 2; // 2바이트 문자
        } else if (charCode <= 0xffff) {
            byteLength += 3; // 3바이트 문자
        } else {
            byteLength += 4; // 4바이트 문자 (이모지 등)
        }
    }
    return byteLength;
}

// 입력 이벤트 처리
const textarea = document.getElementById('reviewText');
const charCountDisplay = document.getElementById('charCount');

function updateCharCount() {
    const currentText = textarea.value;
    const currentByteLength = getByteLength(currentText);
    charCountDisplay.textContent = `${currentByteLength} / 280 (byte)`;
}

if(textarea && charCountDisplay) {
    textarea.addEventListener('input', () => {
        const currentByteLength = getByteLength(textarea.value);
        charCountDisplay.textContent = `${currentByteLength} / 280 (byte)`;

        // 입력 제한 처리
        if (currentByteLength > 280) {
            textarea.value = textarea.value.slice(0, -1); // 초과 시 마지막 문자 삭제
            charCountDisplay.textContent = `${getByteLength(textarea.value)} / 280 (byte)`;
        }
    });
}




document.addEventListener('click', (e) => {
    if (e.target.closest('.edit-review')) {
        const $reviewItem = e.target.closest('.review-item');
        const $reviewId = $reviewItem.dataset.reviewId;
        const $reviewText = $reviewItem.querySelector('.review-description').innerText;
        const $movieTitle = $reviewItem.querySelector('.review-title').innerText;
        const $userName = $reviewItem.querySelector('.nickname').innerText;

        $modal.style.display = 'flex';
        $textarea.value = $reviewText;
        $modalMovieTitle.textContent = $movieTitle;
        $modalUserName.textContent = $userName;

        updateCharCount();

        $submitReview.onclick = () => {
            handleEditReview($reviewId, $textarea.value.trim())
        }
    }
    if (e.target.closest('.delete-review')) {
        const $reviewItem = e.target.closest('.review-item');
        const $reviewId = $reviewItem.dataset.reviewId;
        const result = confirm('리뷰를 삭제하시겠습니까?')
        if (result) {
            handleDeleteReview($reviewId);
        } else {
            return
        }
    }
    if (e.target === $closeModal) {
        closeModal();
    }
});

//모달 닫기
function closeModal() {
    $modal.style.display = 'none';
    $textarea.value = '';
    $modalMovieTitle.textContent = '';
    $modalUserName.textContent = '';
}

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && $modal.style.display === 'flex') {
        closeModal();
    }
});

function handleEditReview(reviewId, updatedText) {
    if (!updatedText) {
        alert('수정할 내용을 입력해주세요')
        return
    }
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('리뷰 수정에 실패하였습니다. 새로고침 후 다시 시도해 주세요')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response === "SUCCESS") {
            alert('리뷰가 성공적으로 수정되었습니다')
            window.location.reload()
        } else if (response === "UNAUTHORIZED") {
            alert('수정할 권한이 없습니다')
            $modal.style.display = 'none';
        } else if (response === "FAILURE") {
            alert('리뷰 수정에 실패하였습니다. 잠시 후 다시 시도해 주세요')
        } else if (response === "NOT_LOGGED_IN") {
            alert('리뷰 수정에 실패하였습니다. 로그인 상태를 확인 후 다시 시도해 주세요')
            $modal.style.display = 'none';
            const redirectUrl = window.location.pathname;
            window.location.replace(`../../../user/login?forward=${encodeURIComponent(redirectUrl)}`);
        }
    };
    xhr.open('PUT', `/reviews/${reviewId}`);
    xhr.setRequestHeader("content-Type", "application/json");
    xhr.send(JSON.stringify({reContent: updatedText}));
}

function handleDeleteReview(reviewId) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('리뷰 삭제에 실패하였습니다. 새로고침 후 다시 시도해 주세요')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response === "SUCCESS") {
            alert('리뷰가 성공적으로 삭제되었습니다')
            window.location.reload()
        } else if (response === "UNAUTHORIZED") {
            alert('리뷰를 삭제할 권한이 없습니다')
        } else if (response === "FAILURE") {
            alert('리뷰 삭제에 실패하였습니다. 잠시 후 다시 시도해 주세요')
        } else if (response === "NOT_LOGGED_IN") {
            alert('리뷰 삭제에 실패하였습니다. 로그인 상태를 확인 후 다시 시도해 주세요')
            const redirectUrl = window.location.pathname;
            window.location.replace(`../../../user/login?forward=${encodeURIComponent(redirectUrl)}`);
        }
    };
    xhr.open('PATCH', `/reviews/${reviewId}`);
    xhr.send();
}



