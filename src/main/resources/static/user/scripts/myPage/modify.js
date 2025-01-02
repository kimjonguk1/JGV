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

{
        const $advertisementArray = ['https://adimg.cgv.co.kr/images/202412/Moana2/1218_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/PORORO/1231_980x80.jpg', 'https://adimg.cgv.co.kr/images/202412/HARBIN/1224_980x80.png', 'https://adimg.cgv.co.kr/images/202411/jjanggu/1209_980x80.png']
        document.addEventListener("DOMContentLoaded", () => {
                const $advertisement = document.getElementById('advertisement');
                const $advertisementRandom = $advertisementArray[Math.floor(Math.random() * $advertisementArray.length)];
                const $img = $advertisement.querySelector(':scope > a > img');
                const $a = $advertisement.querySelector(':scope > a')
                if ($advertisementRandom === $advertisementArray[0]) {
                        $advertisement.style.backgroundColor = '#2B53AB'
                        $a.setAttribute('href', '../movies/movieList/movieInfo/3669')
                } else if ($advertisementRandom === $advertisementArray[1]) {
                        $advertisement.style.backgroundColor = '#4184D2'
                        $a.setAttribute('href', '../movies/movieList/movieInfo/3628')
                } else if ($advertisementRandom === $advertisementArray[2]) {
                        $advertisement.style.backgroundColor = '#191413'
                        $a.setAttribute('href', '../movies/movieList/movieInfo/3611')
                } else {
                        $advertisement.style.backgroundColor = '#2B82DD'
                        $a.setAttribute('href', '../movies/movieList/movieInfo/3666')
                }
                $img.setAttribute('src', $advertisementRandom);
        });
}