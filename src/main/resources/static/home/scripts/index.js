const $special = Array.from(document.querySelectorAll('.special'));
const $items = Array.from(document.querySelectorAll('.item-container > .item > .text'));

$items.forEach((x) => {
    x.onmouseover = () => {
        $special.forEach((special) => {
            special.classList.add('hidden');
            if (special.getAttribute('rel') === x.getAttribute('rel')) {
                special.classList.remove('hidden');
            }
        })
    }
})

{
    const $videoArray = ['https://adimg.cgv.co.kr/images/202412/PORORO/pororo_1080x608_MA.mp4', 'https://adimg.cgv.co.kr/images/202412/HARBIN/HARBIN_pc_1080x608.mp4', 'https://adimg.cgv.co.kr/images/202501/Panda/Panda_1080x608_PC.mp4', 'https://adimg.cgv.co.kr/images/202501/RealPain/realpain_1080x608_pc.mp4', 'https://adimg.cgv.co.kr/images/202501/DarkNuns/DarkNuns_1080x608_PC.mp4'];
    window.onload = () => {
        const $video = document.querySelector('video');
        const $source = document.querySelector('source');
        const $random = $videoArray[Math.floor(Math.random() * $videoArray.length)];
        const $videoWrapper = document.querySelector('.video_wrapper');
        const $strong = $videoWrapper.querySelector(':scope > strong');
        const $span = $videoWrapper.querySelector(':scope > span');
        const $a = $videoWrapper.querySelector(':scope > div > a');
        $source.setAttribute('src', $random);
        switch ($random) {
            case ($videoArray[0]):
                $strong.innerText = '뽀로로 극장판 바닷속 대모험';
                $span.innerHTML = `최고 스케일 스펙터클 오션 어드벤처<br>1월 1일 대개봉`;
                $a.setAttribute('href', '/movies/movieList/movieInfo/3628');
                break;
            case ($videoArray[1]):
                $strong.innerText = '하얼빈';
                $span.innerHTML = `"지금 가장 의미있는 영화"<br>긴 여운 X 큰 울림! 예매 ▶`;
                $a.setAttribute('href', '/movies/movieList/movieInfo/3611');
                break;
            case ($videoArray[2]):
                $strong.innerText = '꼬마 판다 팡의 아프리카 대모험';
                $span.innerHTML = `귀여움으로 무장한 <br> 새로운 꼬마 판다의 등장!`;
                $a.setAttribute('href', '/movies/movieList/movieInfo/3974');
                break;
            case ($videoArray[3]):
                $strong.innerText = '리얼 페인';
                $span.innerHTML = `2025 골든 글로브 수상작 <br> 1월 15일 대개봉`;
                $a.setAttribute('href', '/movies/movieList/movieInfo/3927');
                break;
            case ($videoArray[4]):
                $strong.innerText = '검은 수녀들';
                $span.innerHTML = `송혜교, 이번 상대는 악마다! <br> 더러운 영들아, 당장 떠나거라`;
                $a.setAttribute('href', '/movies/movieList/movieInfo/4024');
                break;
        }
        $video.load();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // Tabs for switching sections
    const currentTab = document.querySelector(".current-tab");
    const upcomingTab = document.querySelector(".upcoming-tab");
    const currentMovies = document.getElementById("current-movies");
    const upcomingMovies = document.getElementById("upcoming-movies");
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabs = document.querySelectorAll(".carousel-container");

    const updateTab = (activeTab, inactiveTab, showSection, hideSection) => {
        activeTab.classList.add("active");
        inactiveTab.classList.remove("active");
        showSection.classList.add("active");
        hideSection.classList.remove("active");
    };

    currentTab.addEventListener("click", () => {
        updateTab(currentTab, upcomingTab, currentMovies, upcomingMovies);
    });

    upcomingTab.addEventListener("click", () => {
        updateTab(upcomingTab, currentTab, upcomingMovies, currentMovies);
    });

    // Carousel functionality
    const carousels = document.querySelectorAll(".carousel");
    carousels.forEach((carousel) => {
        const wrapper = carousel.querySelector(".movies-wrapper");
        const prevButton = carousel.querySelector(".carousel-button.prev");
        const nextButton = carousel.querySelector(".carousel-button.next");

        let cardWidth, cardCount, visibleCards, currentIndex;

        const initializeCarousel = () => {
            const cards = wrapper.children;
            cardWidth = cards[0].offsetWidth + 20; // 카드 너비 + 간격
            cardCount = cards.length;
            visibleCards = 5;
            currentIndex = 0;

            updateCarousel();
        };

        const updateCarousel = () => {
            const maxIndex = Math.ceil(cardCount / visibleCards) - 1;
            wrapper.style.transform = `translateX(-${currentIndex * visibleCards * cardWidth}px)`;

            // 버튼 활성화/비활성화
            prevButton.style.display = currentIndex === 0 ? "none" : "block";
            nextButton.style.display = currentIndex === maxIndex ? "none" : "block";
        };

        // 왼쪽 버튼 클릭
        prevButton.addEventListener("click", () => {
            if (currentIndex > 0) {
                currentIndex -= 1;
                updateCarousel();
            }
        });

        // 오른쪽 버튼 클릭
        nextButton.addEventListener("click", () => {
            const maxIndex = Math.ceil(cardCount / visibleCards) - 1;
            if (currentIndex < maxIndex) {
                currentIndex += 1;
                updateCarousel();
            }
        });

        // 탭 변경 시 초기화
        tabButtons.forEach((button, index) => {
            button.addEventListener("click", () => {
                tabs.forEach((tab, tabIndex) => {
                    if (tabIndex === index) {
                        tab.classList.add("active");
                        initializeCarousel();
                    } else {
                        tab.classList.remove("active");
                    }
                });
            });
        });

        // 초기화
        if (carousel.closest(".carousel-container").classList.contains("active")) {
            initializeCarousel();
        }
    });

    const reserveButtons = document.querySelectorAll('.reserve-button')

    reserveButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            const movieTitle = button.getAttribute("data-moTitle");

            const ticket = {moTitle: movieTitle};
            sessionStorage.setItem('ticketParams', JSON.stringify(ticket));

            window.location.href = `/ticket/?moTitle=${encodeURIComponent(movieTitle)}`
        })
    })
});
