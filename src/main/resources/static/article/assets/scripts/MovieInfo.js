const $tabsContainer = document.querySelector('.tabs')
const $sections = document.querySelectorAll('section')
const $writeReviewButton = document.querySelector('.write-review-btn');
const $myReviewButton = document.querySelector('.my-review-btn');
const $modal = document.querySelector('.reserve-modal');

document.querySelector('.tab-item a[href = "#overview"]').classList.add('active')
document.getElementById('overview').classList.add('active')


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
        $modal.style.display = 'block'
        console.log('세션 있음');
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