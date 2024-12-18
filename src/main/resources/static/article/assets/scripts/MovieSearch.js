document.addEventListener('DOMContentLoaded', function () {
    const carouselTrack = document.querySelector('.carousel-track');
    const carouselItems = document.querySelectorAll('.carousel-item');
    const personInfoContainer = document.querySelector('.person-info');
    const prevButton = document.querySelector('.carousel-button.prev');
    const nextButton = document.querySelector('.carousel-button.next');
    const filmographyContainer = document.createElement('div'); // 필모그래피 컨테이너 추가
    filmographyContainer.id = 'filmography';
    personInfoContainer.insertAdjacentElement('afterend', filmographyContainer); // 필모그래피 위치 설정

    let currentIndex = 0;

    // 초기 상태: 아무도 선택되지 않음
    personInfoContainer.innerHTML = '';

    // 좌우 버튼 이벤트
    prevButton.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateCarouselPosition();
        }
    });

    nextButton.addEventListener('click', () => {
        if (currentIndex < carouselItems.length - 6) {
            currentIndex++;
            updateCarouselPosition();
        }
    });

    function updateCarouselPosition() {
        const itemWidth = carouselItems[0].offsetWidth + 20; // 아이템 너비 + 간격
        carouselTrack.style.transform = `translateX(-${currentIndex * itemWidth}px)`;
    }

    // 클릭 시 상세 정보 표시
    carouselItems.forEach(item => {
        item.addEventListener('click',function ()  {
            carouselItems.forEach(i => i.classList.remove('selected')); // 선택 해제
            item.classList.add('selected'); // 선택된 아이템에 클래스 추가

            const imageUrl = item.querySelector('.person-image').getAttribute('src');
            const name = item.querySelector('.name').innerText;
            const job = item.getAttribute('data-job') || '정보 없음';
            const birth = item.getAttribute('data-birth') || '정보 없음';

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

            // 필모그래피를 화면에 표시
            const filmographyContainer = document.querySelector('.person-info');
            filmographyContainer.innerHTML = relatedMovies.map(movie => `
                <div class="filmography-item">
                <a href="/movies/movieList/movieInfo/${movie.moNum}">
                    <img src="${movie.movieImage}" alt="영화 이미지" width="100">
                     </a>
                    <p>${movie.movieTitle}</p>
                </div>
            `).join('');

        });
    });
});
