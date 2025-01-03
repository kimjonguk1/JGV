const $loginForm = document.getElementById('login-form');

// region nav 광고
{
    const $sideAdvertisementArray = ['https://adimg.cgv.co.kr/images/202411/Firefighters/1121_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_980x90.jpg'];
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.querySelector('.advertisement-info');
        const $advertisementRandom = $sideAdvertisementArray[Math.floor(Math.random() * $sideAdvertisementArray.length)];
        const $img = $advertisement.querySelector(':scope > img');
        const $a = $advertisement.querySelector(':scope > a')
        if ($advertisementRandom === $sideAdvertisementArray[0]) {
            $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3651')
        } else if ($advertisementRandom === $sideAdvertisementArray[1]) {
            $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3628')
        } else {
            $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3611')
        }
        $img.setAttribute('src', $advertisementRandom);
    });
}
// endregion

const savedId = localStorage.getItem('rememberId');
if (savedId) {
    $loginForm['id'].value = savedId;
    $loginForm['rememberId'].checked = true;
    $loginForm['password'].focus();
}
$loginForm.addEventListener('submit', () => {
    const isRememberChecked = $loginForm['rememberId'].checked;
    const enteredId = $loginForm['id'].value.trim();

    if (isRememberChecked) {

        localStorage.setItem('rememberId', enteredId);
    } else {

        localStorage.removeItem('rememberId');
    }
});


// region 로그인
$loginForm.onsubmit = (e) => {
    e.preventDefault();

    if ($loginForm['id'].value.length <= 6 && $loginForm['id'].value.length >= 20) {
        alert('올바른 아이디를 입력해 주세요.');
    }
    if ($loginForm['password'].value.length <= 6 && $loginForm['password'].value.length >= 20) {
        alert('올바른 아이디를 입력해 주세요.');
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usId', $loginForm['id'].value);
    formData.append('usPw', $loginForm['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        Loading.hide();
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');

            return;
        }
        const response = JSON.parse(xhr.responseText);

        if (response['logout']) {
            alert(response['message']);  // 강제 로그아웃 메시지
            window.location.href = '/logout';  // 로그아웃 페이지로 이동
            return;  // 이후 코드 실행을 방지
        }

        if (response['result'] === 'success') {
            sessionStorage.setItem('user', response['MemberNum']);
            // URLSearchParams를 통해 현재 URL의 파라미터를 확인
            const urlParams = new URLSearchParams(window.location.search);
            const forwardUrl = urlParams.get('forward');

            // forward 파라미터가 있으면 해당 URL로 리디렉션, 없으면 기본 '/'로 리디렉션
            if (forwardUrl) {
                window.location.href = forwardUrl;
            } else {
                window.location.href = '/';
            }

        } else if (response['result'] === 'failure_suspended') {
            alert('해당 계정은 이용이 정지된 상태입니다. 관리자에게 문의해 주세요.');
        } else if (response['result'] === 'failure_not_verified') {
            alert(`해당 계정의 이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.<br><br>혹시 이메일이 오지 않았다면 인증 링크가 포함된 이메일을 <a href="/user/resend-register-email-token?email=${$loginForm['email'].value}" target="_blank">다시 전송</a>할 수 있습니다.`)
        } else if (response['result'] === 'failure') {
            alert('올바른 아이디, 비밀번호를 입력해 주세요.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    };
    xhr.open('POST', '/user/login');
    xhr.send(formData);
    Loading.show(0);
}
// endregion


