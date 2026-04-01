// 닉네임 수정 토글
function toggleNicknameEdit() {
    const editRow = document.getElementById('nickname-edit-row');
    const isHidden = editRow.style.display === 'none' || editRow.style.display === '';
    editRow.style.display = isHidden ? 'flex' : 'none';
    if (isHidden) {
        document.getElementById('nickname-input').value = document.getElementById('nickname-text').textContent;
        document.getElementById('nickname-input').focus();
    }
}

// 닉네임 저장
async function saveNickname() {
    const nickname = document.getElementById('nickname-input').value.trim();
    if (!nickname) {
        alert('닉네임을 입력해주세요.');
        return;
    }

    const res = await fetch('/api/user/nickname', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nickname: nickname })
    });

    if (res.ok) {
        document.getElementById('nickname-text').textContent = nickname;
        document.getElementById('nickname-edit-row').style.display = 'none';
        alert('닉네임이 변경되었습니다.');
    } else {
        alert('닉네임 변경에 실패했습니다.');
    }
}