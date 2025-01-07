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
        alert('올바른 비밀번호를 입력해 주세요.');
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
            // URLSearchParams를 통해 현재 URL의 파라미터를 확인
            const urlParams = new URLSearchParams(window.location.search);
            const forwardUrl = urlParams.get('forward');

            // forward 파라미터가 있으면 해당 URL로 리디렉션, 없으면 기본 '/'로 리디렉션
            if (forwardUrl) {
                window.location.href = forwardUrl;
            } else {
                window.location.href = '/';
            }
        }
        else if (response['result'] === 'failure_suspended') {
            alert('해당 계정은 이용이 정지된 상태입니다. 관리자에게 문의해 주세요.');

        }
        else if (response['result'] === 'failure_not_verified') {
            // 이미 다이얼로그가 존재하는지 확인
            if (!document.getElementById("missing-email")) {
                // 커스텀 다이얼로그 HTML 생성
                const customDialog = `
        <div id="missing-email" class="frame" style="padding: 20px; background-color: #f9f9f9; border: 1px solid #ccc; border-radius: 5px; width: 350px; text-align: center; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 97;">
            <span style="font-weight: 500; color: #222222; text-decoration: underline;">${response['userEmail']}</span>
            <span style="font-weight: 500; color: #6e6c6d;"">은 이메일 인증이 완료되지 않은 계정입니다.</span>
            <br><br>
            <span style="font-weight: 500; white-space: nowrap; color: #6e6c6d;">혹시 회원가입한 이메일로 인증메일이 도착하지 않았나요?</span>
            <br><br>
            <a style="color: #FB4357; font-weight: 700;" href="/user/resend-register-email-token?emEmail=${response['userEmail']}" id="resendEmailLink">&gt;&gt; 인증 메일 다시 받기</a>
        </div>
    `;

                // 다이얼로그 HTML을 body에 추가
                const dialogDiv = document.createElement('div');
                dialogDiv.innerHTML = customDialog;
                document.body.appendChild(dialogDiv);

                // "인증 메일 다시 받기" 링크 클릭 시 이메일 재전송
                document.getElementById("resendEmailLink").addEventListener("click", function(event) {
                    Loading.show(0);
                    event.preventDefault(); // 기본 링크 동작 막기

                    // 이메일 재전송 로직 (서버와 연동 필요)
                    fetch(`/user/resend-register-email-token?emEmail=${response['userEmail']}`)
                        .then(response => response.json()) // 서버 응답 처리
                        .then(data => {
                            Loading.hide();
                            if (data.result === 'success') {
                                alert("인증 메일이 다시 전송되었습니다. 회원가입시 입력하신 메일을 확인해주세요.");
                                window.location.reload();
                            } else {
                                alert("메일 전송에 실패했습니다. 다시 시도해주세요.");
                                window.location.reload();
                            }
                        })
                        .catch(error => {
                            alert("서버에 문제가 발생했습니다. 다시 시도해주세요.");
                            window.location.reload();
                        });
                });
            }
        }
        else if (response['result'] === 'failure') {
            alert('올바른 아이디, 비밀번호를 입력해 주세요.');
        }
        else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    };
    xhr.open('POST', '/user/login');
    xhr.send(formData);
    Loading.show(0);
}
// endregion


