async function login() {
    const res = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        })
    });

    if (res.ok) {
        const data = await res.json();
        location.href = '/articles';
    } else {
        alert('로그인 실패. 이메일/비밀번호를 확인해주세요.');
    }
}