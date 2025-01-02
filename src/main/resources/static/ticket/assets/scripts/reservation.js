const $body = document.querySelector('body');

window.onload = () => {
    if (sessionStorage.getItem('paymentComplete') !== 'true') {
        window.location.href = '../../';
        return;
    }

    $body.style.display = 'flex';

    const keys = [
        'meName', 'moTitle', 'ciName', 'thName',
        'seName', 'scStartDate', 'paPrice',
        'human', 'poster', 'paymentNumber'
    ];

    keys.forEach(key => {
        const element = document.getElementById(key);
        if (element) {
            if (key === 'poster') element.src = sessionStorage.getItem(key);
            else element.innerText = sessionStorage.getItem(key);
        }
    });

    sessionStorage.removeItem('paymentComplete');
    keys.forEach(key => sessionStorage.removeItem(key));
};
