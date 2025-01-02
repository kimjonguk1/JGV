const $findId = document.getElementById('find-id-form');
const $findResult = document.getElementById('find-result');

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

$findId.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('usName', $findId['name'].value);
    formData.append('usEmail', $findId['email'].value);
    formData.append('usContact', $findId['contact'].value);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE ) {

        return;
        }
        if (xhr.status < 200 || xhr.status >= 300 ) {
            alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
        return;
        }
        try {
            const response = JSON.parse(xhr.responseText);

            switch (response['result']) {
                case 'failure_deleted':
                    alert('입력하신 회원 정보는 삭제 처리된 회원 입니다. 고객센터에 문의해주세요.');
                    break;
                case 'failure_suspended':
                    alert('정지된 회원 정보입니다. 고객센터에 문의해주세요.');
                    break;
                case 'failure_verified':
                    alert('이메일 인증이 완료되지 않은 회원입니다. 인증 후 다시 시도해주세요.');
                    break;
                case 'failure':
                    alert('입력하신 정보가 올바르지 않습니다. 다시 입력해 주세요.');
                    break;
                case 'success':
                    alert('아이디 찾기에 성공했습니다. 아이디를 확인해주세요.');

                    $findId.style.display = 'none';

                    $findResult.innerHTML =
                        `<span style="color: #ea9b00; font-weight: 400;">${response.name} 
                         </span> 님의 아이디는 
                         <span style="color: #ea9b00;font-weight: 400;">${response.id}
                         </span> 
                         입니다.`;
                    break;
                default:
                    alert('알 수 없는 이유로 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                    break;
            }
        } catch (error) {
            alert('서버 응답 처리 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    };

    xhr.open('POST', location.href);
    xhr.send(formData);
};






