const $tabsContainer = document.querySelector('.tabs')
const $sections = document.querySelectorAll('section')
const $writeReviewButton = document.querySelector('.write-review-btn');
const $myReviewButton = document.querySelector('.my-review-btn');
const $modal = document.querySelector('.reserve-modal');
const $closeModal = document.getElementById('closeModal');
const $submitReview = document.getElementById('submitReview');

document.querySelector('.tab-item a[href = "#overview"]').classList.add('active')
document.getElementById('overview').classList.add('active')
$modal.style.display = 'flex';

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

$submitReview.addEventListener('click', () => {
    const $reviewText = document.getElementById('reviewText').value.trim();

    if($reviewText === '') {
        alert('평점 내용을 입력해 주세요');
        return
    }
    if($reviewText.length < 15) {
        alert('평점 내용을 15자 이상 입력해 주세요')
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('리뷰 등록에 실패하였습니다. 다시 시도해 주세요')
            return;
        }
        alert('관람평이 등록 되었습니다. \n 임직원의 경우 실관람평 작성 포인트는 지급되지 않습니다.');
        document.getElementById('reviewText').value = ''
        $modal.style.display = 'none'
    };
    xhr.open('POST', '/review');
    xhr.setRequestHeader('Content-Type', 'application/json');
    const reviewData = {
        content: $reviewText
    };
    xhr.send(JSON.stringify(reviewData));
})