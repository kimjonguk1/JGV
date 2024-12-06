const $registerForm = document.getElementById('register-form');

$registerForm['duplicate-button'].onclick = () => {
    alert('사용가능한 아이디 입니다.');
}

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
    formData.append('name', $registerForm['name'].value);
    formData.append('id', $registerForm['id'].value);
    formData.append('password', $registerForm['password'].value);
    formData.append('birth', $registerForm['birth'].value);
    formData.append('contact', $registerForm['contact'].value);
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
            alert('이메일 보냈음 인증하셈');
        }
        else {
            alert('무슨무슨 오류로 고소함');
        }
    };
    xhr.open('POST', location.href);
    xhr.send(formData);


}
