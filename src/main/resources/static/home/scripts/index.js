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
        button.addEventListener("click", (e)=> {
            const movieTitle = button.getAttribute("data-moTitle");

            const ticket = { moTitle : movieTitle};
            sessionStorage.setItem('ticketParams', JSON.stringify(ticket));

            window.location.href = `/ticket/?moTitle=${encodeURIComponent(movieTitle)}`
        })
    })
});

