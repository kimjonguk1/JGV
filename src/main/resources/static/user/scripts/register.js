const $registerForm = document.getElementById('register-form');

// region 주소 요청
$registerForm['addr-button'].onclick = () => {
    new daum.Postcode({
        oncomplete: function (data) {

            var roadAddr = data.roadAddress;
            var extraRoadAddr = '';

            if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                extraRoadAddr += data.bname;
            }
            if (data.buildingName !== '' && data.apartment === 'Y') {
                extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            if (extraRoadAddr !== '') {
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }

            document.getElementById('postcode').value = data.zonecode; // 우편번호
            document.getElementById('address').value = roadAddr; // 도로명 주소
            document.getElementById('extraAddress').value = extraRoadAddr; // 추가 주소 정보

        }
    }).open();
}
// endregion

// region ID 중복 체크
let isIdValid = false;
$registerForm['duplicate-id-button'].onclick = () => {
    const $usId = $registerForm['id'].value;

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {

            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        const result = response['result'];
        if (result === 'failure_duplicate_id') {
            alert('이미 사용중인 아이디 입니다.');
            return isIdValid = false;

        }
        if (result === 'failure') {
            alert('올바른 아이디를 입력해주세요. 아이디는 6~20자의 소문자 + 숫자 입니다.');

            return isNicknameValid = false;
        }

        alert('사용 가능한 아이디 입니다.');
        return isIdValid = true;

    };
    xhr.open('GET', '/user/check-duplicate-id?user=' + encodeURIComponent($usId));
    xhr.send();
}
// endregion

// region nickname 중복체크
let isNicknameValid = false;
$registerForm['duplicate-nickname-button'].onclick = () => {
    const $nickname = $registerForm['nickname'].value;

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {

            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        const result = response['result'];
        if (result === 'failure_duplicate_nickname') {
            alert('이미 사용중인 닉네임 입니다.');

            return isNicknameValid = false;
        }
        if (result === 'failure') {
            alert('올바른 닉네임을 입력해주세요. 닉네임은 2~20자 입니다.');

            return isNicknameValid = false;
        }

        alert('사용 가능한 닉네임 입니다.');
        return isNicknameValid = true;

    };
    xhr.open('GET', '/user/check-duplicate-nickname?nickname=' + encodeURIComponent($nickname));
    xhr.send();
}
// endregion

// region 이메일 버튼
const $valueButton = document.querySelector('.value-button');
const $modal = document.getElementById('domainModal');
const $domainSelf = document.querySelector('.domain-self');
const $domainButtons = $modal.querySelectorAll('.domain-button');
const $domainInput = document.querySelector('.domain');
const $body = document.querySelector('body');


$valueButton.addEventListener('click', function (e) {
    e.stopPropagation();
    $modal.style.display = 'block';
});

$domainButtons.forEach(button => {
    button.addEventListener('click', function () {
        $domainInput.value = button.value;
        $modal.style.display = 'none';
    });
});
$domainSelf.addEventListener('click', function () {
    $domainInput.value = '';
    $domainInput.placeholder = '직접입력';
    $domainInput.focus();
    $modal.style.display = 'none';
});
$body.addEventListener('click', function (e) {
    if (!e.target.closest('#domainModal') && !e.target.closest('.value-button')) {
        $modal.style.display = 'none';
    }
});
$modal.addEventListener('click', function (e) {
    e.stopPropagation();
});


// endregion

// TODO keyup EventListener 구현

// region 비밀번호 강도 설정
const $passwordInput = $registerForm[`password`];
const $strengthDisplay = document.getElementById('password-strength');

const $lowercaseRegex = /[a-z]/;
const $uppercaseRegex = /[A-Z]/;
const $numberRegex = /\d/;
const $specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;

$passwordInput.addEventListener('keyup', (e) => {
    const $password = e.target.value;
    let strength = 0;
    if ($password.length > 6) {
        if ($lowercaseRegex.test($password)) strength++;
        if ($uppercaseRegex.test($password)) strength++;
        if ($numberRegex.test($password)) strength++;
        if ($specialCharRegex.test($password)) strength++;
    }

    let strengthText = '';
    let strengthColor = '';

    if (strength <= 1) {
        strengthText = '약함';
        strengthColor = '#FB4357';
    } else if (strength === 2) {
        strengthText = '보통';
        strengthColor = '#e6731f';
    } else if (strength > 2) {
        strengthText = '강함';
        strengthColor = '#45c645';
    }


    if ($password === '') {
        $strengthDisplay.style.display = 'none';
    } else {
        $strengthDisplay.style.display = 'block';
    }
    $strengthDisplay.textContent = `비밀번호 강도: ${strengthText}`;
    $strengthDisplay.style.color = strengthColor;
})


// endregion

// region 회원가입
{

    $registerForm.onsubmit = (e) => {
        e.preventDefault();
        const $id = $registerForm['id'].value;
        const $password = $registerForm['password'].value;
        const $idRegex = /^(?=.*[a-z])(?=.*\d).{6,20}$/;
        const $passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+{}\[\]:;"'<>,.?/\\|`~\-=_]).{8,100}$/;


        if ($registerForm['password'].value.length < 6 && $registerForm['password'].value.length > 50) {
            alert('올바른 비밀번호를 입력해 주세요.');
            return;
        }
        if ($registerForm['password'].value !== $registerForm['passwordCheck'].value) {
            alert('비밀번호가 서로 일치하지 않습니다.');
            return;
        }
        if ($registerForm['contact'].value.length < 10 && $registerForm['contact'].value.length > 13) {
            alert('올바른 연락처를 입력해주세요.');
            return;
        }
        if ($registerForm['email'].value.length < 6 && $registerForm['email'].value.length > 50) {
            alert('올바른 이메일을 입력해주세요.');
            return;
        }
        if (!$registerForm['agree'].checked) {
            alert('서비스 이용약관 및 개인정보 처리방침에 동의하지 않으면 회원가입을 하실 수 없습니다.');
            return;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('usId', $registerForm['id'].value);
        formData.append('usPw', $registerForm['password'].value);
        formData.append('usName', $registerForm['name'].value);
        formData.append('usNickName', $registerForm['nickname'].value);
        formData.append('usBirth', $registerForm['birth'].value);
        formData.append('usGender', $registerForm['gender'].value);
        formData.append('usContact', $registerForm['contact'].value);

        // 주소 결합
        const zipcode = $registerForm['postcode'].value;
        const address = $registerForm['address'].value;
        const extraAddress = $registerForm['extraAddress'].value;
        const fullAddress = `${zipcode} ${address} ${extraAddress ? extraAddress : ''}`.trim();
        formData.append('usAddr', fullAddress);

        // 이메일 결합
        const email = $registerForm['email'].value;
        const domain = $registerForm['domain'].value;
        const fullEmail = email + '@' + domain;
        formData.append('usEmail', fullEmail);


        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {

                return;
            }
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                return;
            }
            const response = JSON.parse(xhr.responseText);

            if (response['result'] === 'success') {
                alert('입력하신 이메일로 인증 링크를 전송하였습니다. 계정 인증 후 로그인이 가능하며, 해당 링크는 24시간 이후 만료 됩니다.');
                return location.href = `/user/login`;

            } else if (response['result'] === 'failure') {
                return location.href = `/user/register`;

            } else if (response['result'] === 'failure_invalid_id') {
                alert('올바른 아이디 형식이 아닙니다 다시 확인해주세요. 아이디는 소문자와 숫자만 포함되어야 하며, 8~20자여야 합니다.');
            } else if (response['result'] === 'failure_invalid_password') {
                alert('비밀번호는 8~100자 사이에 대소문자, 숫자, 특수문자를 포함해야 합니다.');
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        };
        xhr.open('POST', '/user/register');
        xhr.send(formData);

    }
}

// endregion

