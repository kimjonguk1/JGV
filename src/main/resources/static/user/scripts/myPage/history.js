// 현재 날짜 정보를 가져오기
const now = new Date();
const currentYear = now.getFullYear();
const currentMonth = now.getMonth() + 1; // 0-11, 1-12로 맞추기 위해 +1
const currentDate = now.getDate();

// startYear, startMonth, startDate 기본값 설정
document.getElementById('startYear').innerHTML = `
        <option value="${currentYear}" selected>${currentYear}</option>
    `;
document.getElementById('startMonth').innerHTML = `
        <option value="${currentMonth}" selected>${currentMonth}</option>
    `;
document.getElementById('startDate').innerHTML = `
        <option value="${currentDate}" selected>${currentDate}</option>
    `;

// endYear, endMonth, endDate 기본값 설정
document.getElementById('endYear').innerHTML = `
        <option value="${currentYear}" selected>${currentYear}</option>
        <option value="${currentYear + 1}">${currentYear + 1}</option>
    `;
document.getElementById('endMonth').innerHTML = createMonthOptions();
document.getElementById('endDate').innerHTML = createDateOptions();

// 1~12월을 위한 함수
function createMonthOptions() {
    let monthOptions = '';
    for (let i = 1; i <= 12; i++) {
        monthOptions += `<option value="${i}" ${i === currentMonth ? 'selected' : ''}>${i}</option>`;
    }
    return monthOptions;
}

// 1~31일까지의 날짜를 위한 함수
function createDateOptions() {
    let dateOptions = '';
    const daysInMonth = new Date(currentYear, currentMonth, 0).getDate();
    for (let i = 1; i <= daysInMonth; i++) {
        dateOptions += `<option value="${i}" ${i === currentDate ? 'selected' : ''}>${i}</option>`;
    }
    return dateOptions;
}