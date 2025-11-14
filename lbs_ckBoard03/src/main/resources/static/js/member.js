function validateForm() {
    const memId = document.getElementById("memId").value.toLowerCase();
    const forbiddenWords = ["admin", "master", "system"];

    // "admin", "master", "system" 문자열 포함 여부 검사
    for (const word of forbiddenWords) {
        if (memId.includes(word)) {
            alert(`'${word}'가 포함된 아이디는 사용할 수 없습니다.`);
            return false; // 폼 전송 중단
        }
    }
    return true; // 통과 시 전송
}