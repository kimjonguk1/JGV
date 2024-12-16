const $findPassword = document.getElementById('find-password-form');
const $findResult = document.getElementById('find-result');

$findPassword.onsubmit = (e) => {
    e.preventDefault()

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE ) {

        return;
        }
        if (xhr.status < 200 || xhr.status >= 300 ) {

        return;
        }
    };
    xhr.open('POST', location.href);
    xhr.send();


}