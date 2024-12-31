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
                console.log(data)
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

        //영화 이미지
        renderMovieImage(data.mimgUrl);
        //영화 케릭터 정보 및 이미지
        renderCharacters(data.actors || [], data.actorImages || []);
    }


    // 영화 캐릭터 정보를 렌더링하는 함수
    function renderMovieImage(imageUrl) {
        const container = document.getElementById("movie-image");
        container.innerHTML = `
            <img src="${imageUrl}" alt="현재 영화 이미지" style="width: 150px; height: auto; display: block; margin-bottom: 10px;">
            <label>
                포스터 수정:
                <input type="file" id="movie-image-input" name="movieImage">
            </label>
        `;

        const input = document.getElementById("movie-image-input");
        input.addEventListener("change", (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    container.querySelector("img").src = event.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // 영화 캐릭터 정보를 렌더링하는 함수
    function renderCharacters(characters, actorImages) {
        const container = document.getElementById("movie-characters");

        // 기존 "인물 추가" 버튼을 유지한 채로 나머지 내용 초기화
        const existingAddButton = document.getElementById("add-character-button");
        container.innerHTML = ""; // 기존 내용 제거

        characters.forEach((character, index) => {
            addCharacterEntry(character, actorImages[index] || "");
        });

        const addButton = document.getElementById("add-character-button");
        addButton.onclick = () => addCharacterEntry();
    }


    function addCharacterEntry(characterName = "", imageUrl = "") {
        const container = document.getElementById("movie-characters");
        const addButton = document.getElementById("add-character-button");

        const charDiv = document.createElement("div");
        charDiv.classList.add("character-entry");

        charDiv.innerHTML = `
        <label>
            인물 이름 수정:
            <input type="text" value="${characterName}" name="characterName">
        </label>
        <img src="${imageUrl}" alt="현재 인물 이미지" style="width: 100px; height: auto; display: block; margin-bottom: 10px;">
        <label>
            인물 이미지 수정:
            <input type="file" class="character-image-input" name="characterImage">
        </label>
        <button type="button" class="delete-character-button">삭제</button>
    `;

        // "인물 추가" 버튼 앞에 새로운 캐릭터 입력 폼 추가
        container.insertBefore(charDiv, addButton);

        // 이미지 파일 변경 시 미리보기 업데이트
        const input = charDiv.querySelector(".character-image-input");
        input.addEventListener("change", (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    charDiv.querySelector("img").src = event.target.result;
                };
                reader.readAsDataURL(file);
            }
        });

        // "삭제" 버튼 동작 추가
        const deleteButton = charDiv.querySelector(".delete-character-button");
        deleteButton.addEventListener("click", () => charDiv.remove());
    }


    // 폼 제출을 처리하는 함수
    function handleFormSubmit(e, movieNum) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData();

        formData.append("movieRuntime", document.getElementById("movie-runtime").value);
        formData.append("movieDescription", document.getElementById("movie-description").value);
        formData.append("movieRating", document.getElementById("movie-rating").value);

        // 장르와 제작국가는 쉼표로 구분된 문자열을 배열로 변환하여 추가
        const genres = document.getElementById("movie-genre").value.split(",").map(item => item.trim());
        const countries = document.getElementById("movie-country").value.split(",").map(item => item.trim());
        formData.append("movieGenres", JSON.stringify(genres));
        formData.append("movieCountries", JSON.stringify(countries));

        // 영화 포스터 추가 (선택된 경우만)
        const posterInput = document.querySelector("#movie-image input[type='file']");
        if (posterInput.files.length > 0) {
            formData.append("moviePoster", posterInput.files[0]);
        }

        // 캐릭터 데이터 추가
        const characterContainers = document.querySelectorAll("#movie-characters .character-entry");
        characterContainers.forEach((container, index) => {
            const name = container.querySelector("input[name='characterName']").value;
            const imageInput = container.querySelector("input[name='characterImage']");

            formData.append(`movieCharacters[${index}][name]`, name);

            if (imageInput.files.length > 0) {
                formData.append(`movieCharacters[${index}][image]`, imageInput.files[0]);
            }
        });

        // 데이터 전송
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if(xhr.status < 200 || xhr.status >= 300) {
            
                return;
            }
        };
        xhr.open('PUT', `/admin/api/${movieNum}`);
        xhr.send(formData);
    }
});
