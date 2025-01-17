// 현재 날짜 정보를 가져오기
const now = new Date();
const currentYear = now.getFullYear();
const currentMonth = now.getMonth() + 1; // 0-11, 1-12로 맞추기 위해 +1
const currentDate = now.getDate();
const $history = document.getElementById('history');

if(document.getElementById('startYear') !== null) {
    document.getElementById('startYear').innerHTML = createYearOptions(currentYear);
    document.getElementById('startMonth').innerHTML = createMonthOptions(currentMonth);
    document.getElementById('startDate').innerHTML = createDateOptions(currentYear, currentMonth, currentDate);

// endYear, endMonth, endDate 기본값 설정
    document.getElementById('endYear').innerHTML = createYearOptions(currentYear);
    document.getElementById('endMonth').innerHTML = createMonthOptions(currentMonth);
    document.getElementById('endDate').innerHTML = createDateOptions(currentYear, currentMonth, currentDate);
}
// startYear, startMonth, startDate 기본값 설정



// 1~12월을 위한 함수
function createMonthOptions(selectedMonth) {
    let monthOptions = '';
    for (let i = 1; i <= 12; i++) {
        monthOptions += `<option value="${i}" ${i === selectedMonth ? 'selected' : ''}>${i}</option>`;
    }
    return monthOptions;
}

// 1~31일까지의 날짜를 위한 함수
function createDateOptions(year, month, selectedDate) {
    let dateOptions = '';
    const daysInMonth = new Date(year, month, 0).getDate(); // 해당 월의 마지막 날짜 계산
    for (let i = 1; i <= daysInMonth; i++) {
        dateOptions += `<option value="${i}" ${i === selectedDate ? 'selected' : ''}>${i}</option>`;
    }
    return dateOptions;
}

// startYear, endYear를 위한 함수
function createYearOptions(selectedYear) {
    let yearOptions = '';
    for (let i = currentYear - 1; i <= currentYear + 1; i++) {  // 작년부터 내년까지 포함
        yearOptions += `<option value="${i}" ${i === selectedYear ? 'selected' : ''}>${i}</option>`;
    }
    return yearOptions;
}