const $userContainer = document.getElementById('user-container');
const tabs = $userContainer.querySelectorAll('.title'); // 모든 탭을 한 번에 선택

// 클릭 이벤트 리스너를 각각 추가하여 스타일을 변경
function clearStyles() {
    tabs.forEach(tab => {
        tab.style.backgroundColor = ''; // 모든 탭의 배경색을 초기화
    });
}

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        clearStyles(); // 다른 탭의 스타일을 초기화
        tab.style.backgroundColor = 'red'; // 선택된 탭의 색을 변경
    });
});