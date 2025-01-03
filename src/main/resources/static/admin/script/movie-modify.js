document.addEventListener("DOMContentLoaded", function () {
    const urlPath = window.location.pathname; // "/admin/modify/3604"
    const pathSegments = urlPath.split('/'); // ["", "admin", "modify", "3604"]
    const movieNum = pathSegments[pathSegments.length - 1];

    // 영화 정보를 서버에서 가져오기
    fetchMovieData(movieNum);

    // 저장 버튼 이벤트 핸들러
    const form = document.getElementById("movie-edit-form");
    form.onsubmit = (e) => handleFormSubmit(e, movieNum);

    // 취소 버튼 이벤트 핸들러
    document.getElementById("cancel-button").onclick = () => {
        window.location.href = "/admin/is_admin";
    };

    // 영화 정보를 가져오는 함수
    function fetchMovieData(movieNum) {
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) return;

            if (xhr.status >= 200 && xhr.status < 300) {
                const data = JSON.parse(xhr.responseText);
                populateMovieForm(data);
            } else {
                alert("영화 정보를 불러오는데 실패하였습니다. 다시 시도해 주세요.");
            }
        };
        xhr.open("GET", `/admin/api/${movieNum}`);
        xhr.send();
    }

    // 영화 정보를 폼에 채우는 함수
    function populateMovieForm(data) {
        document.getElementById("movie-title").value = data.moTitle || "";
        document.getElementById("movie-runtime").value = data.moTile || "";
        document.getElementById("movie-description").value = data.moPlot || "";
        document.getElementById("movie-rating").value = data.raGrade || "";
        document.getElementById("movie-genre").value = data.genres || "";
        document.getElementById("movie-country").value = data.countries || "";

        // 영화 포스터 표시
        renderMoviePoster(data.mimgUrl);

        // 영화 캐릭터 정보 표시
        renderCharacters(data.movieCharacters || [], data.actorImages || []);
    }

    // 영화 포스터를 표시하는 함수
    function renderMoviePoster(imageUrl) {
        const container = document.getElementById("movie-image");
        container.innerHTML = `
            <img src="${imageUrl}" alt="영화 포스터">
        `;
    }

    // 영화 캐릭터 정보를 표시하는 함수
    function renderCharacters(characters, actorImages) {
        const container = document.getElementById("movie-characters");

        // 기존 캐릭터 폼 초기화
        container.innerHTML = "";

        // 캐릭터 추가
        characters.forEach((character, index) => {
            addCharacterEntry(character.chName, character.delete, character.chNum, actorImages[index] || "");
        });
    }

    function addCharacterEntry(characterName = "", deleteFlag = false, chNum, imageUrl = "") {
        const container = document.getElementById("movie-characters");

        const charDiv = document.createElement("div");
        charDiv.classList.add("character-entry");

        charDiv.innerHTML = `
            <label>
                인물 이름 수정:
                <input type="text" value="${characterName}" name="characterName">
            </label>
            <img src="${imageUrl}" alt="인물 이미지">
            <label class="delete-container">
                <label>삭제:</label>
                <input type="checkbox" name="deleteCharacter">
            </label>
            <input type="hidden" name="characterNum" value="${chNum}">
        `;

        container.appendChild(charDiv);
    }

    // 폼 제출을 처리하는 함수
    function handleFormSubmit(e, movieNum) {
        e.preventDefault();

        const formData = {
            moTile: document.getElementById("movie-runtime").value,
            moPlot: document.getElementById("movie-description").value,
            raGrade: document.getElementById("movie-rating").value,
            genres: document.getElementById("movie-genre").value.split(",").map(item => item.trim()),
            countries: document.getElementById("movie-country").value.split(",").map(item => item.trim()),
            actors: []
        };

        // 캐릭터 데이터 추가
        const characterContainers = document.querySelectorAll("#movie-characters .character-entry");
        characterContainers.forEach(container => {
            const name = container.querySelector("input[name='characterName']").value;
            const deleteFlag = container.querySelector("input[name='deleteCharacter']").checked;
            const chNum = parseInt(container.querySelector("input[name='characterNum']").value, 10);

            formData.actors.push({
                chNum: chNum,
                chName: name,
                delete: deleteFlag
            });
        });

        // 데이터 전송
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                alert('영화 정보 수정에 실패하였습니다. 다시 시도해 주세요');
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if(response === 'SUCCESS') {
                alert('영화 정보가 성공적으로 수정되었습니다.')
                window.location.href = '/admin/is_admin'
            } else if(response === 'FAILURE') {
                alert('영화 정보 수정에 실패하였습니다.')
                return;
            } else if(response === 'NOT_LOGGED_IN') {
                alert('로그인 세션이 만료되었습니다.')
                window.location.href = '/user/login'
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다')
                return;
            }
        };
        Loading.show();
        xhr.open('PUT', `/admin/api/${movieNum}`);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send(JSON.stringify(formData));
    }
});
