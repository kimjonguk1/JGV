const $movieCrawl = document.getElementById('movie-crawl-button');

$movieCrawl.onclick = () => {
    window.open(
        "../../admin/movie-crawl",
        "영화 정보 크롤링",
        "width=600,height=800,left=200,top=200"
    );
};

const $theaterCrawl = document.getElementById('theater-crawl-button');

$theaterCrawl.onclick = () => {
    window.open(
        "../../admin/theater-crawl",
        "상영 정보 크롤링",
        "width=600,height=800,left=200,top=200"
    );
};

{
    const $titleChange = document.getElementById('admin-title-container');
    const $buttonWrapper = $titleChange.querySelector(':scope > .admin-button-wrapper');
    const $movieInfo = $titleChange.querySelector(':scope > .movie-information');
    const $theaterInfo = $titleChange.querySelector(':scope > .theater-information');
    const $userInfo = $titleChange.querySelector(':scope > .user-information');
    const $movieButton = document.getElementById('movie-crawl-button');
    const $theaterButton = document.getElementById('theater-crawl-button');
    const $movieMain = document.getElementById('movie-admin-page');
    const $theaterMain = document.getElementById('theater-admin-page');
    const $userMain = document.getElementById('user-admin-page')
    const $movieSearchForm = document.getElementById('movie-search-form');
    const $theaterSearchForm = document.getElementById('theater-search-form');
    const $userSearchForm = document.getElementById('user-search-form');
    const $moviePage = document.getElementById('movie-page-container');
    const $theaterPage = document.getElementById('theater-page-container');
    const $userPage = document.getElementById('user-page-container');
    const $userButton = document.getElementById('user-history-button');


    $movieInfo.onclick = () => {
        $movieButton.style.display = 'block';
        $movieInfo.style.color = '#000000';
        $movieMain.style.display = 'block';
        $movieSearchForm.style.display = 'block';
        $theaterButton.style.display = 'none';
        $theaterInfo.style.color = '#666666';
        $theaterMain.style.display = 'none';
        $theaterSearchForm.style.display = 'none';
        $userSearchForm.style.display = 'none';
        $buttonWrapper.style.border = '2px solid #fb4357';
        $moviePage.style.display = 'block';
        $theaterPage.style.display = 'none';
        $userInfo.style.color = '#666666';
        $userMain.style.display = 'none';
        $userPage.style.display = 'none';
        $userButton.style.display = 'none';

        const url = new URL(location.href);
        url.searchParams.set('mode', 'movie');
        history.pushState(undefined, undefined, url.toString());
    }

    $theaterInfo.onclick = () => {
        $movieButton.style.display = 'none';
        $theaterButton.style.display = 'block';
        $movieInfo.style.color = '#666666';
        $movieMain.style.display = 'none';
        $movieSearchForm.style.display = 'none';
        $theaterInfo.style.color = '#000000';
        $theaterMain.style.display = 'block';
        $theaterButton.style.backgroundColor = '#2275a4';
        $theaterButton.style.border = '1px solid #2275a4';
        $theaterSearchForm.style.display = 'block';
        $userSearchForm.style.display = 'none';
        $buttonWrapper.style.border = '2px solid #2275a4'
        $moviePage.style.display = 'none';
        $theaterPage.style.display = 'block';
        $userInfo.style.color = '#666666';
        $userMain.style.display = 'none';
        $userPage.style.display = 'none';
        $userButton.style.display = 'none';

        const url = new URL(location.href);
        url.searchParams.set('mode', 'theater');
        history.pushState(undefined, undefined, url.toString());
    }

    $userInfo.onclick = () => {
        $userInfo.style.color = '#000000';
        $movieInfo.style.color = '#666666';
        $theaterInfo.style.color = '#666666';
        $movieButton.style.display = 'none';
        $theaterButton.style.display = 'none';
        $movieSearchForm.style.display = 'none';
        $movieMain.style.display = 'none';
        $moviePage.style.display = 'none';
        $theaterMain.style.display = 'none';
        $theaterPage.style.display = 'none';
        $theaterSearchForm.style.display = 'none';
        $userSearchForm.style.display = 'block';
        $userMain.style.display = 'block';
        $userPage.style.display = 'block';
        $userButton.style.display = 'block';
        $userButton.style.backgroundColor = '#aa3ebc';
        $userButton.style.border = '1px solid #aa3ebc';
        $buttonWrapper.style.border = '2px solid #aa3ebc';


        const url = new URL(location.href);
        url.searchParams.set('mode', 'user');
        history.pushState(undefined, undefined, url.toString());

    }


    const url = new URL(location.href);
    const mode = url.searchParams.get('mode') ?? 'movie'
    if (mode === 'movie') {
        $movieInfo.dispatchEvent(new Event('click'));
    } else if (mode === 'theater') {
        $theaterInfo.dispatchEvent(new Event('click'));
    } else {
        $userInfo.dispatchEvent(new Event('click'));
    }
}

{
    const $screenItem = Array.from(document.querySelectorAll('.screen-item'));
    $screenItem.forEach((screen) => {
        const $theaterModify = Array.from(screen.querySelectorAll(':scope > .theater-modify'));
        $theaterModify.forEach((mod) => {
            mod.onclick = () => {
                const $modal = screen.querySelector(':scope > .modal');
                $modal.classList.remove('hidden');
                const $button = $modal.querySelector(':scope > .head > .h1 > .button');
                $button.onclick = () => {
                    $modal.classList.add('hidden');
                }
                $modal.onsubmit = (e) => {
                    e.preventDefault();
                    const scNum = screen.querySelector(':scope > .scNum');
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData();
                    formData.append('scStartDate', $modal['modify'].value);
                    formData.append('scNum', scNum.innerText.split(':')[1].trim());
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState !== XMLHttpRequest.DONE) {
                            return;
                        }
                        if (xhr.status < 200 || xhr.status >= 300) {
                            alert('오류 발생');
                            return;
                        }
                        const response = JSON.parse(xhr.responseText);
                        if (response['result'] === 'success') {
                            alert('상영정보를 성공적으로 수정했습니다.');
                            $button.click();
                            const $date = screen.querySelector(':scope > .date');
                            $date.innerText = '상영일: ' + $modal['modify'].value;
                        } else if (response['result'] === 'is_deleted') {
                            alert('이미 삭제된 상영정보입니다.');
                        } else {
                            alert('수정 처리 중 문제가 발생했습니다.');
                        }
                    }
                    xhr.open('PATCH', './is_admin');
                    xhr.send(formData);
                }
            }
        })
        const $theaterDelete = Array.from(screen.querySelectorAll(':scope > .theater-delete'));
        $theaterDelete.forEach((del) => {
            del.onclick = () => {
                const result = confirm("정말 삭제하시겠습니까?")
                if (result) {
                    $theaterDelete.forEach((item) => {
                        if (item === del) {
                            const scNum = screen.querySelector(':scope > .scNum');
                            const xhr = new XMLHttpRequest();
                            const formData = new FormData();
                            formData.append('scNum', scNum.innerText.split(':')[1].trim());
                            xhr.onreadystatechange = () => {
                                if (xhr.readyState !== XMLHttpRequest.DONE) {
                                    return;
                                }
                                if (xhr.status < 200 || xhr.status >= 300) {
                                    alert('오류 발생');
                                    return;
                                }
                                const response = JSON.parse(xhr.responseText);
                                if (response['result'] === 'success') {
                                    alert('상영정보를 성공적으로 삭제했습니다.');
                                    screen.remove();
                                } else if (response['result'] === 'is_deleted') {
                                    alert('이미 삭제된 상영정보입니다.');
                                } else {
                                    alert('삭제 처리 중 문제가 발생했습니다.');
                                }
                            };
                            xhr.open('DELETE', './is_admin');
                            xhr.send(formData);
                        }
                    })
                } else {
                    return;
                }
            }
        })
    })
}

document.addEventListener("DOMContentLoaded", function () {
    const $deleteButton = document.querySelectorAll('.movie-delete')
    const $modifyButton = document.querySelectorAll('.movie-modify')
    $deleteButton.forEach(button => {
        button.addEventListener("click", function () {
            const movieNum = this.getAttribute("data-mo-num")
            const result = confirm("정말 삭제하시겠습니까?")
            if (result) {
                const xhr = new XMLHttpRequest();
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    if (xhr.status < 200 || xhr.status >= 300) {
                        alert('삭제에 실패하였습니다. 다시 시도해 주세요')
                        return;
                    }
                    const response = JSON.parse(xhr.responseText);
                    if (response === 'SUCCESS') {
                        alert('삭제가 완료되었습니다.');
                        window.location.reload();
                    } else if (response === 'NOT_LOGGED_IN') {
                        alert('삭제에 실패했습니다. 세션이 만료되었거나 로그인이 해제되었을 수 있습니다. 로그인 상태를 확인한 후 다시 시도해 주세요.');
                    } else if (response === 'FAILURE') {
                        alert('삭제에 실패하였습니다. 다시 시도해 주세요')
                    } else {
                        alert('알 수 없는 응답입니다. 관리자에게 문의하세요')
                    }
                };
                xhr.open('POST', "/admin/delete");
                xhr.setRequestHeader('Content-Type', 'application/json');
                const Data = {
                    moNum: movieNum
                };
                xhr.send(JSON.stringify(Data));
            } else {
                return
            }
        })
    })
    $modifyButton.forEach(button => {
        button.addEventListener("click", function () {
            const movieNum = this.getAttribute("data-mo-num")
            window.location.href = `/admin/modify/${movieNum}`;
        })
    })
});

{
    const $userForms = document.querySelectorAll('.user-status-form');

    $userForms.forEach((form) => {
        form.onsubmit = (e) => {
            e.preventDefault();

            const userNum = form.closest('tr').querySelector('#user-number').innerText;
            const userStatus = form.querySelector('.user-status').value;

            if (userStatus === 'default') {
                alert("변경할 상태를 선택하세요.");
                return;
            }

            const xhr = new XMLHttpRequest();
            const formData = new FormData();

            formData.append('usNum', userNum);
            formData.append('usIsSuspended', userStatus === 'suspend' ? 'true' : 'false');

            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) return;

                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
                    return;
                }

                const response = JSON.parse(xhr.responseText);
                if (response['result'] === 'success') {
                    alert('회원 정보가 변경 되었습니다.');
                    location.reload(); // 변경 후 페이지 리로드
                } else if (response['result'] === 'failure') {
                    alert('회원 정보 변경에 실패하였습니다.');
                } else {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            };

            // 서버에 요청을 보냅니다.
            xhr.open('PATCH', '/admin/is_admin_user');
            xhr.send(formData);
        };
    });
}