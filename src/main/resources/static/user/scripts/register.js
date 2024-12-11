const $registerForm = document.getElementById('register-form');
const $id = $registerForm.querySelector(':scope > .id');


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
            document.getElementById('detailAddress').value = data.jibunAddress; // 지번 주소
            document.getElementById('extraAddress').value = extraRoadAddr; // 추가 주소 정보

        }
    }).open();
}
// endregion

// region ID 중복 체크
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
        } else {
            alert('사용 가능한 아이디 입니다.');
        }
    };
    xhr.open('GET', '/user/check-duplicate-id?user=' + encodeURIComponent($usId));
    xhr.send();
}
// endregion

// region nickname 중복체크
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
        } else {
            alert('사용 가능한 닉네임 입니다.');
        }
    };
    xhr.open('GET', '/user/check-duplicate-nickname?nickname=' + encodeURIComponent($nickname));
    xhr.send();
}
// endregion

// region 회원가입
{

    $registerForm.onsubmit = (e) => {
        e.preventDefault();

        if ($registerForm['password'].value.length <= 6 && $registerForm['password'].value.length >= 50) {
            alert('올바른 비밀번호를 입력해 주세요.');
        }
        if ($registerForm['password'].value !== $registerForm['passwordCheck'].value) {
            alert('비밀번호가 서로 일치하지 않습니다.');
        }
        if ($registerForm['contact'].value.length <= 10 && $registerForm['contact'].value.length >= 13) {
            alert('올바른 연락처를 입력해주세요.');
        }
        if ($registerForm['email'].value.length <= 8 && $registerForm['email'].value.length >= 50) {
            alert('올바른 이메일을 입력해주세요.');
        }
        if (!$registerForm['agree'].checked) {
            alert('서비스 이용약관 및 개인정보 처리방침에 동의하지 않으면 회원가입을 하실 수 없습니다.');
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
        formData.append('usEmail', $registerForm['email'].value);

        const zipcode = $registerForm['postcode'].value;
        const address = $registerForm['address'].value;
        const detailAddress = $registerForm['detailAddress'].value;
        const extraAddress = $registerForm['extraAddress'].value;
        const fullAddress = `${zipcode} ${address} ${detailAddress} ${extraAddress ? extraAddress : ''}`.trim();
        formData.append('usAddr', fullAddress);


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

                location.href = `/user/login`;
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        };
        xhr.open('POST', '/user/register');
        xhr.send(formData);


    }
}

// endregion