{
  /** @param {PointerEvent} e */
  document.body.querySelector(':scope > .dialog > [rel="cancel"]').onclick = (e) => {
    e.preventDefault();
    if (!confirm('정말로 탈퇴할까요? 삭제된 정보는 복구할 수 없습니다.')) {
      return;
    }
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
      if (xhr.readyState !== XMLHttpRequest.DONE) return;
      if (xhr.status < 200 || xhr.status >= 300) {
        alert('탈퇴 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.');
        return;
      }
      const response = JSON.parse(xhr.responseText);
      const result = response['result'];
      if (result === 'failure') {
        alert('알 수 없는 이유로 탈퇴에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
      } else if (result === 'success') {
        alert('탈퇴에 성공하였습니다. 그 동안 이용해 주셔서 감사합니다.');
        location.href = './';
      } else {
        alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
      }
    };
    xhr.open('DELETE', './');
    xhr.send();
  };
}