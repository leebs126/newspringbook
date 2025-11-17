function validateForm(isAdminPage = false) {
    const memId = document.getElementById("memId").value.toLowerCase();
    const forbiddenWords = ["admin", "master", "system"];

    // 일반 회원가입 페이지일 경우만 검사
    if (!isAdminPage) {
        for (const word of forbiddenWords) {
            if (memId.includes(word)) {
                alert(`'${word}'가 포함된 아이디는 사용할 수 없습니다.`);
                return false;
            }
        }
    }
    return true;
}
