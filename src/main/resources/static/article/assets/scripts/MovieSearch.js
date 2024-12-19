document.addEventListener('DOMContentLoaded', function () {
    const carouselTrack = document.querySelector('.carousel-track');
    const carouselItems = document.querySelectorAll('.carousel-item');
    const personInfoContainer = document.querySelector('.person-info');
    const prevButton = document.querySelector('.carousel-button.prev');
    const nextButton = document.querySelector('.carousel-button.next');
    const filmographyContainer = document.createElement('div'); // 필모그래피 컨테이너 추가
    filmographyContainer.id = 'filmography';
    personInfoContainer.insertAdjacentElement('afterend', filmographyContainer); // 필모그래피 위치 설정

    const itemsPerPage = 6; // 한 페이지에 표시할 아이템 수
    let currentPage = 0; // 현재 페이지 인덱스
    const totalPages = Math.ceil(carouselItems.length / itemsPerPage); // 전체 페이지 수

    // 초기 상태: 아무도 선택되지 않음
    personInfoContainer.innerHTML = '';

    function updateCarouselDisplay() {
        // 모든 아이템 숨기기
        carouselItems.forEach((item, index) => {
            if (index >= currentPage * itemsPerPage && index < (currentPage + 1) * itemsPerPage) {
                item.style.display = 'flex'; // 현재 페이지의 아이템만 표시
            } else {
                item.style.display = 'none'; // 나머지는 숨김
            }
        });

        prevButton.disabled = currentPage === 0;
        nextButton.disabled = currentPage === totalPages - 1;
    }

    // 초기 아이템 표시
    updateCarouselDisplay();

    // Next 버튼 클릭 시
    nextButton.addEventListener('click', () => {
        if (currentPage < totalPages - 1) {
            currentPage++;
            updateCarouselDisplay();
        }
    });

    // Prev 버튼 클릭 시
    prevButton.addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            updateCarouselDisplay();
        }
    });


    // 클릭 시 상세 정보 표시
    carouselItems.forEach(item => {
        item.addEventListener('click',  async function ()  {
            carouselItems.forEach(i => i.classList.remove('selected')); // 선택 해제
            item.classList.add('selected'); // 선택된 아이템에 클래스 추가

            const imageUrl = item.querySelector('.person-image').getAttribute('src');
            const name = item.querySelector('.name').innerText;
            const job = item.getAttribute('data-job') || '정보 없음';
            const birth = item.getAttribute('data-birth') || '정보 없음';
            const charId = this.getAttribute('data-char-id');

            filmographyContainer.innerHTML = '';

            personInfoContainer.innerHTML = `
                <div class="info-details">
                <p class="name">${name}</p>
                <p class="job">직업: ${job}</p>
                <p class="birth">출생: ${birth}</p> </div>
                <img src="${imageUrl}" alt="${name}" class="person-image">
            `;

            const relatedMoviesJson = this.getAttribute('data-related-movies');
            const relatedMovies = JSON.parse(relatedMoviesJson);

            console.log("선택된 인물의 필모그래피: ", relatedMovies);

            // AJAX로 해당 캐릭터의 필모그래피 가져오기
            try {
                const response = await fetch(`/movies/movieList/person/${charId}`);
                const result = await response.json();

                if (result.status === "SUCCESS") {
                    const relatedMovies = result.data;

                    // 필모그래피 업데이트
                    filmographyContainer.innerHTML = relatedMovies.map(movie => `
                        <div class="filmography-item">
                            <a href="/movies/movieList/movieInfo/${movie.moNum}">
                                <img src="${movie.movieImage}" alt="영화 이미지" width="100">
                            </a>
                            <p>${movie.movieTitle}</p>
                        </div>
                    `).join('');
                } else {
                    filmographyContainer.innerHTML = '';
                    console.error("영화 데이터를 불러오는 데 실패했습니다.");
                }
            } catch (error) {
                console.error("AJAX 요청 오류: ", error);
            }

        });
    });
});
