const $tabsContainer = document.querySelector('.tabs')
const $sections = document.querySelectorAll('section')
const $writeReviewButton = document.querySelector('.write-review-btn');
const $myReviewButton = document.querySelector('.my-review-btn');
const $modal = document.querySelector('.reserve-modal');
const $closeModal = document.getElementById('closeModal');
const $submitReview = document.getElementById('submitReview');


document.querySelector('.tab-item a[href = "#overview"]').classList.add('active')
document.getElementById('overview').classList.add('active')
$modal.style.display = 'none';

// 쓰로틀링 적용
let isThrottling = false;

$tabsContainer.addEventListener('click', function (e) {
    if (e.target.tagName === 'A') {
        e.preventDefault();

        isThrottling = true;

        // 모든 탭 비활성화
        document.querySelectorAll('.tabs .tab-item a').forEach(tab => tab.classList.remove('active'));
        $sections.forEach(section => section.classList.remove('active'));

        // 클릭된 탭 활성화
        e.target.classList.add('active');
        const targetId = e.target.getAttribute('href').substring(1); // # 제거
        document.getElementById(targetId).classList.add('active');
        setTimeout(() => {
            isThrottling = false
        }, 300)
    }
})


const $bottomBtn = document.querySelector('.moveBottom');
const $topBtn = document.querySelector('.moveTop');

$topBtn.onclick = () => {
    window.scrollTo(0, 0);
}
$bottomBtn.onclick = () => {
    window.scrollTo({
        top : document.body.scrollHeight,
        behavior: "smooth"
    })
}

$writeReviewButton.addEventListener('click', () => {
    console.log(sessionUser)
    if(sessionUser !== 'null') {
        $modal.style.display = 'flex';
    }else {
        const result = confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?');
        if(result) {
            window.location.href = '/user/login'
        } else {
            return;
        }
    }
})

$myReviewButton.addEventListener('click', () => {
    if(sessionUser) {
        window.location.href = '/user/my-review'
    } else {
        window.location.href = '/user/login'
    }
})

$closeModal.addEventListener('click', () => {
    $modal.style.display = 'none';
})



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

textarea.addEventListener('input', () => {
    const currentByteLength = getByteLength(textarea.value);
    charCountDisplay.textContent = `${currentByteLength} / 280 (byte)`;

    // 입력 제한 처리
    if (currentByteLength > 280) {
        textarea.value = textarea.value.slice(0, -1); // 초과 시 마지막 문자 삭제
        charCountDisplay.textContent = `${getByteLength(textarea.value)} / 280 (byte)`;
    }
});

// 리뷰 정렬(최신순, 좋아요 순)
function sortReviews(sort) {
    const $reviewContainer = document.querySelector('.reviews');
    const $reviews = Array.from($reviewContainer.getElementsByClassName('review-item'));

    if(sort === 'latest') {
        $reviews.sort((a, b) => b.dataset.timestamp - a.dataset.timestamp);
    }else if(sort === 'recommend') {
        $reviews.sort((a, b) => b.dataset.likes - a.dataset.likes);
    }
    $reviewContainer.innerHTML = '';
    $reviews.forEach(review => $reviewContainer.appendChild(review));

    document.querySelectorAll('.review-sort-button').forEach(button => button.classList.remove('active'));
    document.querySelector(`.review-sort-button[onclick="sortReviews('${sort}')"]`).classList.add('active');
}

// 평점 작성 및 평점 수정
$submitReview.addEventListener('click', () => {
    const $reviewText = document.getElementById('reviewText').value.trim();
    const movieId = window.location.pathname.split('/').pop();
    if($reviewText === '') {
        alert('평점 내용을 입력해 주세요');
        return
    }
    handleSubmitReview(movieId, $reviewText);
})
// 평점 작성
function handleSubmitReview(movieId, reviewText) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('리뷰 등록에 실패하였습니다. 다시 시도해 주세요')
            return;
        }

        const response = JSON.parse(xhr.responseText);
        console.log(response.result);
        if(response.result === 'SUCCESS') {
            alert('관람평이 등록 되었습니다. \n 임직원의 경우 실관람평 작성 포인트는 지급되지 않습니다.');
            document.getElementById('reviewText').value = ''
            $modal.style.display = 'none'
            window.location.reload();
        } else if(response.result === 'NOT_LOGGED_IN') {
            alert('리뷰 작성에 실패했습니다. 세션이 만료되었거나 로그인이 해제되었을 수 있습니다. 로그인 상태를 확인한 후 다시 시도해 주세요.');
            document.getElementById('reviewText').value = ''
            $modal.style.display = 'none'
        } else if(response.result === 'ALREADY_WRITTEN') {
            alert('이미 이 영화에 대한 평점을 작성하셨습니다.')
        } else {
            alert('평점 작성에 실패하였습니다. 다시 시도해 주세요')
        }
    };
    xhr.open('POST', '/reviews/write');
    xhr.setRequestHeader('Content-Type', 'application/json');
    const reviewData = {
        moNum: parseInt(movieId),
        reContent: reviewText
    };
    xhr.send(JSON.stringify(reviewData));
}


// 좋아요 버튼 클릭시 이벤트 처리 (동적이므로 이벤트 위임 처리)
document.addEventListener('click', (e) => {
    if(e.target.closest('#like-btn')) {
        const $likeButton = e.target.closest('#like-btn');
        const $reviewItem = $likeButton.closest('.review-item');
        const reviewId = $reviewItem.dataset.reviewId;

        if(reviewId) {
            handleLike(reviewId);
        }
    }

    if (e.target.closest('.edit-review')) {
        const reviewItem = e.target.closest('.review-item');
        const reviewId = reviewItem.dataset.reviewId;
        const reviewText = reviewItem.querySelector('.review-text').innerText;

        // 모달 열기
        $modal.style.display = 'flex';

        // 텍스트 영역에 기존 리뷰 내용 삽입
        document.getElementById('reviewText').value = reviewText;

        // 수정 모드로 전환
        $submitReview.dataset.mode = 'edit';
        $submitReview.dataset.reviewId = reviewId;
    }

})

// 좋아요 버튼 클릭시 이벤트
function handleLike(reviewId) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('좋아요 반영에 실패했습니다. 다시 시도해 주세요')
            console.log('xhr 스탯')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response === "NOT_LOGGED_IN") {
            alert("로그인 후 좋아요를 눌러주세요");
        } else if(response.trim() === "SUCCESS") {
            const reviewItem = document.querySelector(`.review-item[data-review-id="${reviewId}"]`);
            const likeCountSpan = reviewItem.querySelector('.like-count');
            const currentCount = parseInt(likeCountSpan.innerText, 10);
            likeCountSpan.innerText = currentCount + 1; // 카운트 증가
        } else if(response.trim() === "ALREADY_LIKED") {
            alert("이미 좋아요를 누르셨습니다")
        }
        else if(response === "FAILURE"){
            alert("좋아요 반영에 실패했습니다. 다시 시도해 주세요")
        }

    };
    xhr.open('POST', `/reviews/like/${reviewId}`);
    xhr.send();
}


// 리뷰 삭제 및 수정
document.addEventListener('DOMContentLoaded', () => {
    document.addEventListener('click', (e) => {
        if (e.target.closest('.menu-btn')) {
            e.stopPropagation(); // 이벤트 전파 방지
            const menuDropdown = e.target.closest('.review-footer').querySelector('.menu-dropdown');
            document.querySelectorAll('.menu-dropdown').forEach(menu => menu.classList.add('hidden')); // 다른 메뉴 닫기
            menuDropdown.classList.toggle('hidden'); // 현재 메뉴 표시/숨김
        } else {
            // 메뉴 버튼 외부 클릭 시 모든 메뉴 닫기
            document.querySelectorAll('.menu-dropdown').forEach(menu => menu.classList.add('hidden'));
        }
    });

    // 수정 버튼 및 삭제 버튼 클릭 이벤트 처리
    document.addEventListener('click', (e) => {
        if(e.target.closest('.edit-review')) {
            const $reviewItem = e.target.closest('.review-item');
            const $reviewId = $reviewItem.dataset.reviewId;
            const $reviewText = $reviewItem.querySelector('.review-text').innerText;

            const $modal = document.querySelector('.reserve-modal');
            const $textarea = document.getElementById('reviewText');
            const $submitReview = document.getElementById('submitReview');

            $modal.style.display = 'flex';

            $textarea.value = $reviewText;

            $submitReview.onclick = () => {
                handleEditReview($reviewId, $textarea.value.trim())
            }
        }
    });

    function handleEditReview(reviewId, updatedText) {
        if(!updatedText) {
            alert('수정할 내용을 입력해주세요')
            return
        }

        const xhr = new XMLHttpRequest();
        console.log("리뷰 아이디 : " + reviewId);
        xhr.onreadystatechange = () => {
            if(xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if(xhr.status < 200 || xhr.status >= 300) {
                alert('평점 수정에 실패하였습니다. 새로고침 후 다시 시도해 주세요')
                return;
            }
            const response = JSON.parse(xhr.responseText);
            console.log(response);
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

    function handleDeleteReview(reviewId, reviewItem) {

    }


});


{
    const $schedule = document.getElementById('schedule');
    const $items = $schedule.querySelector(':scope > .main');
    const $theater = Array.from($items.querySelectorAll(':scope > .item'));
    const $screens = $schedule.querySelector(':scope > .cinema-info > .items');
    const $movie = document.querySelector('.movie-info > .title');

    {
        window.onload = () => {
            const $regions = document.querySelector('[rel="대구"]');
            $regions.click();
        }
    }

    $theater.forEach(($item) => {
        $item.onclick = () => {
            $theater.forEach((x) => x.classList.remove('select'));
            $item.classList.add('select'); // 선택한 지역에 관련해서 select class 부여
            const url = new URL(location.href);
            url.searchParams.set('region', $item.innerText);
            url.searchParams.set('movie', $movie.innerText);
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                Loading.hide();
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('오류 발생');
                    return;
                }
                const $dayContainer = $schedule.querySelector('.day-containers');
                $dayContainer.innerHTML = '';
                const $dayContainers = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelector('.day-containers');
                const $days = $dayContainers.querySelector(':scope > .day-container');
                const $items = Array.from($days.querySelectorAll(':scope > .item'));
                const $dayButtons = Array.from($dayContainers.querySelectorAll(':scope > .button'));
                const $beforeButton = $dayContainers.querySelector(':scope > .before');
                const $afterButton = $dayContainers.querySelector(':scope > .after');
                $dayContainer.append($beforeButton);
                $dayContainer.append($afterButton);
                $dayContainer.append($days);
                $items.forEach((item, index) => {
                    if (index >= 9) {
                        item.classList.add('hidden');
                    }
                    item.onclick = () => {
                        $items.forEach((it) => {
                            it.classList.remove('select');
                            if (item === it) {
                                it.classList.add('select');
                                const currentDate = new Date();
                                let year = currentDate.getFullYear();
                                const currentMonth = currentDate.getMonth() + 1;
                                const month = item.querySelector(':scope > .small-container > .day:nth-child(1)');
                                if (month.innerText.substring(0, 2) < currentMonth) {
                                    year += 1;
                                }
                                const day = item.querySelector(':scope > .day');
                                url.searchParams.set('date', year + '-' + month.innerText.substring(0, 2) + '-' + day.innerText);
                                const xhr = new XMLHttpRequest();
                                xhr.onreadystatechange = () => {
                                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                                        return;
                                    }
                                    Loading.hide();
                                    if (xhr.status < 200 || xhr.status >= 300) {
                                        alert('오류 발생');
                                        return;
                                    }
                                    $screens.innerHTML = '';
                                    const $screenContainer = new DOMParser().parseFromString(xhr.responseText, 'text/html').querySelectorAll('.items > .item');
                                    $screenContainer.forEach((screen) => {
                                        $screens.append(screen);
                                    })
                                }
                                xhr.open('GET', url.toString());
                                xhr.send();
                                Loading.show(0);
                            }
                        })
                    }
                })
                if ($items.length > 0) {
                    $items[0].click();
                }
                let Index = 0;
                $dayButtons.forEach((dayButton) => {
                    dayButton.onclick = () => {
                        if (dayButton.classList.contains('after')) {
                            Index += 9;
                        }
                        if (dayButton.classList.contains('before')) {
                            Index -= 9;
                        }
                        if (Index < 0) {
                            Index = 0;
                        }
                        if (Index >= $items.length) {
                            Index = Index - 9;
                        }
                        $items.forEach((iem, index) => {
                            if (index >= Index && index < Index + 9) {
                                iem.classList.remove('hidden');
                            } else {
                                iem.classList.add('hidden');
                            }
                        })
                    }
                })
            }
            xhr.open('GET', url.toString());
            xhr.send();
            Loading.show(0);
        }
    })
}