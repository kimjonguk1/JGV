document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const movieNum = urlParams.get("movieNum");

    // 영화 정보 가져오기
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('영화 정보를 불러오는데 실패하였습니다. 다시 시도해 주세요')
            return;
        }
        const data = JSON.parse(xhr.responseText);
        document.getElementById("movie-title").value = data.title;
        document.getElementById("movie-runtime").value = data.runtime;
        document.getElementById("movie-description").value = data.description;
        document.getElementById("movie-rating").value = data.rating;
        document.getElementById("movie-genre").value = data.genre;
        document.getElementById("movie-country").value = data.country;
        renderCharacters(data.characters);
    };
    xhr.open("GET", `/admin/modify/${movieNum}`);
    xhr.send();

    function renderCharacters(characters) {
        const container = document.getElementById("movie-characters");
        characters.forEach(character => {
            const charDiv = document.createElement("div");
            charDiv.innerHTML = `
                <label>
                    인물 이름:
                    <input type="text" value="${character.name}" name="characterName">
                </label>
                <label>
                    인물 이미지:
                    <input type="file" name="characterImage">
                </label>
            `;
            container.appendChild(charDiv);
        });
    }

    // 저장 버튼
    const form = document.getElementById("movie-edit-form");
    form.onsubmit = (e) => {
        e.preventDefault();
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
    };

    // 취소 버튼
    document.getElementById("cancel-button").onclick = () => {
        window.location.href = "/admin";
    };
});
