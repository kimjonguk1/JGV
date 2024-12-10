const tabs = document.querySelectorAll('.tabs .tab-item a');
const sections = document.querySelectorAll('section');

tabs.forEach(tab => {
    tab.addEventListener('click', function (e) {
        e.preventDefault(); // 기본 링크 동작 방지

        // 모든 탭에서 활성화 클래스 제거
        tabs.forEach(t => t.classList.remove('active'));

        // 클릭된 탭에 활성화 클래스 추가
        tab.classList.add('active');

        // 모든 섹션 숨기기
        sections.forEach(section => section.classList.remove('active'));

        // 클릭된 탭과 연결된 섹션 표시
        const targetId = tab.getAttribute('href').substring(1); // # 제거
        document.getElementById(targetId).classList.add('active');
    });
});