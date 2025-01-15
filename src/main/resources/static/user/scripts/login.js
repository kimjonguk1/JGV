const $loginForm = document.getElementById('login-form');

// region nav 광고
{
    const $sideAdvertisementArray = ['https://adimg.cgv.co.kr/images/202411/Firefighters/1121_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x90.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1227_980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/RealPain/0114_980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/Panda/980x90.jpg', 'https://adimg.cgv.co.kr/images/202501/DarkNuns/980x90.jpg'];
    document.addEventListener("DOMContentLoaded", () => {
        const $advertisement = document.querySelector('.advertisement-info');
        const $advertisementRandom = $sideAdvertisementArray[Math.floor(Math.random() * $sideAdvertisementArray.length)];
        const $img = $advertisement.querySelector(':scope > img');
        switch ($advertisementRandom) {
            case ($sideAdvertisementArray[0]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3651');
                break;
            case ($sideAdvertisementArray[1]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3628');
                break;
            case ($sideAdvertisementArray[2]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3611');
                break;
            case ($sideAdvertisementArray[3]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3927')
                break;
            case ($sideAdvertisementArray[4]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/3974')
                break;
            case ($sideAdvertisementArray[5]):
                $advertisement.setAttribute('href', '../movies/movieList/movieInfo/4024')
                break;
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
        console.log(response['result']);

        if (response['result'] === 'failure_duplicate_user') {
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
        else if (response['result'] === 'failure_blocked_ip') {
            alert('해당 IP 주소에서의 로그인 시도가 차단되었습니다. 관리자가 검토 후 조치를 취할 수 있습니다."');
        }
        else if (response['result'] === 'failure_password_mismatch') {
            let customDialog = '';

            // 5회 미만 실패 시 기본 다이얼로그
            if (response["count"] < 5) {
                customDialog = `
            <div id="missing-email" class="frame" style="padding: 20px; background-color: #f9f9f9; border: 1px solid #ccc; border-radius: 5px; width: 350px; text-align: center; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 97; display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                <span style="font-weight: 500; color: #222222;">비밀번호가 일치하지 않습니다.</span>
                <div style="display: flex; flex-direction: row">
                    <span style="font-weight: 500; color: #6e6c6d;">(</span>
                    <span style="font-weight: 700; color: #FB4357;">${response['count']}회 실패</span>
                    <span style="font-weight: 500; color: #6e6c6d;">, 5회 실패시 계정 정지</span>
                    <span style="font-weight: 500; color: #6e6c6d;">)</span>
                </div>
                <div>
                    <span style="font-weight: 500; color: #6e6c6d;">비밀번호를 잊어버리셨나요? &gt; <a style="color: #FB4357; font-weight: 700;" href="./find-password">비밀번호 찾기</a></span>
                </div>
                <button style="color: #6e6c6d; cursor: pointer; font-weight: 700;" onclick="window.location.reload();">닫기</button>
            </div>
        `;
            }
            // 5회 실패 시 IP 차단 다이얼로그
            else if (response["count"] === 5) {
                customDialog = `
            <div id="missing-email" class="frame" style="padding: 20px; background-color: #f9f9f9; border: 1px solid #ccc; border-radius: 5px; width: 350px; text-align: center; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 97; display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                <span style="font-weight: 500; color: #222222;">비밀번호가 일치하지 않습니다.</span>
                <div style="display: flex; flex-direction: row">
                    <span style="font-weight: 500; color: #6e6c6d;">(</span>
                    <span style="font-weight: 700; color: #FB4357;">${response['count']}회 실패</span>
                    <span style="font-weight: 500; color: #6e6c6d;">, 계정 정지</span>
                    <span style="font-weight: 500; color: #6e6c6d;">)</span>
                </div>
                <div>
                    <span style="font-weight: 500; color: #6e6c6d;">해당 IP에서의 접근이 차단되었습니다.</span>
                </div>
                <button style="color: #6e6c6d; cursor: pointer; font-weight: 700;" onclick="window.location.reload();">닫기</button>
            </div>
        `;
            }

            // 다이얼로그 HTML을 body에 추가
            const dialogDiv = document.createElement('div');
            dialogDiv.innerHTML = customDialog;
            document.body.appendChild(dialogDiv);
            // 5회 실패 전까지는 다이얼로그와 함께 실패 횟수만 표시
        }
        else if (response['result'] === 'failure_not_verified') {
            if (!document.getElementById("missing-email")) {
                const customDialog = `
        <div id="missing-email" class="frame" style="padding: 20px; background-color: #f9f9f9; border: 1px solid #ccc; border-radius: 5px; width: 350px; text-align: center; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 97;">
            <span style="font-weight: 500; color: #222222; text-decoration: underline;">${response['userEmail']}</span>
            <span style="font-weight: 500; color: #6e6c6d;"">은 이메일 인증이 완료되지 않은 계정입니다.</span>
            <br><br>
            <span style="font-weight: 500; white-space: nowrap; color: #6e6c6d;">혹시 회원가입한 이메일로 인증메일이 도착하지 않았나요?</span>
            <br><br>
            <a style="color: #FB4357; font-weight: 700;" href="/user/resend-register-email-token?emEmail=${response['userEmail']}" id="resendEmailLink">&gt;&gt; 인증 메일 다시 받기</a>
            <br><br>
            <button style="color: #6e6c6d; cursor: pointer; font-weight: 700;" onclick="window.location.reload();">닫기</button>
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
        else if (response['result'] === 'failure_id_mismatch') {
            alert('올바른 아이디, 비밀번호를 입력해 주세요.');
        }
        else if (response['result'] === 'failure') {
            alert(`올바른 아이디, 비밀번호를 입력해주세요.`);
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


