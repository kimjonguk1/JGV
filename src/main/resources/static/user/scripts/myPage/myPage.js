const $reservationCancel = document.querySelector('.reservation-cancel-button');

// $reservationCancel.onclick = () => {
//     window.open(
//         "http://localhost:8080/user/myPage/reservationCancel",
//         "예매취소",
//         "width=600,height=800,left=200,top=200"
//     );
// };

const $modal = document.querySelector('.reserve-modal');
const $textarea = document.getElementById('reviewText');
const $modalMovieTitle = document.getElementById('modalMovieTitle');
const $modalUserName = document.getElementById('modalUserName');
const $submitReview = document.getElementById('submitReview');
const $closeModal = document.getElementById('closeModal');
console.log($modal)
$modal.style.display = 'none';
document.addEventListener('click', (e) => {
    if(e.target.closest('.edit-review')) {
        const $reviewItem = e.target.closest('.review-item');
        const $reviewId = $reviewItem.dataset.reviewId;
        const $reviewText = $reviewItem.querySelector('.review-description').innerText;
        const $movieTitle = $reviewItem.querySelector('.review-title').innerText;
        const $userName = $reviewItem.querySelector('.nickname').innerText;

        $modal.style.display = 'flex';
        $textarea.value = $reviewText;
        $modalMovieTitle.textContent = $movieTitle;
        $modalUserName.textContent = $userName;

        $submitReview.onclick = () => {
            handleEditReview($reviewId, $textarea.value.trim())
        }
    }
    if(e.target.closest('.delete-review')) {
        const $reviewItem = e.target.closest('.review-item');
        const $reviewId = $reviewItem.dataset.reviewId;
        const result = confirm('평점을 삭제하시겠습니까?')
        if(result) {
            handleDeleteReview($reviewId);
        } else {
            return
        }
    }
    if(e.target === $closeModal) {
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
    if(!updatedText) {
        alert('수정할 내용을 입력해주세요')
        return
    }
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('평점 수정에 실패하였습니다. 새로고침 후 다시 시도해 주세요')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response === "SUCCESS") {
            alert('평점이 성공적으로 수정되었습니다')
            window.location.reload()
        } else if(response === "UNAUTHORIZED") {
            alert('수정할 권한이 없습니다')
            $modal.style.display = 'none';
        } else if(response === "FAILURE") {
            alert('평점 수정에 실패하였습니다. 잠시 후 다시 시도해 주세요')
        } else if(response === "NOT_LOGGED_IN") {
            alert('평점 수정에 실패하였습니다. 로그인 상태를 확인 후 다시 시도해 주세요')
            $modal.style.display = 'none';
        }
    };
    xhr.open('PUT', `/reviews/${reviewId}`);
    xhr.setRequestHeader("content-Type", "application/json");
    xhr.send(JSON.stringify({ reContent : updatedText}));
}

function handleDeleteReview(reviewId) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('평점 삭제에 실패하였습니다. 새로고침 후 다시 시도해 주세요')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response === "SUCCESS") {
            alert('평점이 성공적으로 삭제되었습니다')
            window.location.reload()
        } else if(response === "UNAUTHORIZED") {
            alert('삭제할 권한이 없습니다')
        } else if(response === "FAILURE") {
            alert('평점 삭제에 실패하였습니다. 잠시 후 다시 시도해 주세요')
        } else if(response === "NOT_LOGGED_IN") {
            alert('평점 삭제에 실패하였습니다. 로그인 상태를 확인 후 다시 시도해 주세요')
        }
    };
    xhr.open('PATCH', `/reviews/${reviewId}`);
    xhr.send();
}