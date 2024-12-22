const $withdraw = document.querySelector('.withdraw-button');


$withdraw.onclick = () => {
    window.open(
        "http://localhost:8080/user/myPage/userWithdraw",
        "회원탈퇴",
        "width=600,height=800,left=200,top=200"
    );
};

