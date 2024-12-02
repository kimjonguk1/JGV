document.addEventListener('DOMContentLoaded', function () {
    const tabs = document.querySelectorAll('.tabs a');
    const sections = document.querySelectorAll('main section');

    tabs.forEach(tab => {
        tab.addEventListener('click', function (e) {
            e.preventDefault();

            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            sections.forEach(section => section.style.display = 'none');
            const target = document.querySelector(this.getAttribute('href'));
            target.style.display = 'block';
        });
    });

    // 초기 활성화 상태
    document.querySelector('.tabs a.active').click();
});
