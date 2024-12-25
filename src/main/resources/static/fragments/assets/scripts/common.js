const $logout = document.getElementById('logout');

if ($logout) {
    $logout.addEventListener('click', (e) => {
        e.preventDefault();

        sessionStorage.clear();
        location.href  = $logout.href
    });
}