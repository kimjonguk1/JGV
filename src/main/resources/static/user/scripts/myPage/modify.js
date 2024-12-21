const $modifyNickname = document.getElementById('changeNickname');
const $modifyPassword = document.getElementById('change-password');

$modifyNickname.onclick = () => {
        window.open(
            "http://localhost:8080/user/myPage/modifyNickname",
            "회원 닉네임 수정",
            "width=600,height=800,left=200,top=200"
        );
}

$modifyPassword.onclick = () => {
        window.open(
            "http://localhost:8080/user/myPage/modifyPassword",
            "회원 비밀번호 재설정",
            "width=600,height=800,left=200,top=200"
        );

}
