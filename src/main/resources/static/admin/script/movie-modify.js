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
        window.location.href = "/admin";
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
        document.getElementById("movie-rating").value = data.moBookingRate || "";
        document.getElementById("movie-genre").value = data.genres || "";
        document.getElementById("movie-country").value = data.countries || "";
        renderCharacters(data.actors || []);
    }

    // 영화 캐릭터 정보를 렌더링하는 함수
    function renderCharacters(characters) {
        const container = document.getElementById("movie-characters");
        container.innerHTML = ""; // 기존 내용 제거
        characters.forEach((character) => {
            const charDiv = document.createElement("div");
            charDiv.classList.add("character-entry");
            charDiv.innerHTML = `
                <label>
                    인물 이름:
                    <input type="text" value="${character.name || ""}" name="characterName">
                </label>
                <label>
                    인물 이미지:
                    <input type="file" name="characterImage">
                </label>
            `;
            container.appendChild(charDiv);
        });
    }

    // 폼 제출을 처리하는 함수
    function handleFormSubmit(e, movieNum) {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    alert("영화 수정이 완료되었습니다.");
                    window.location.href = "/admin";
                } else {
                    alert("수정에 실패하였습니다. 다시 시도해 주세요.");
                }
            }
        };
        xhr.open("PUT", `/api/movies/${movieNum}`);
        xhr.send(formData);
    }
});
