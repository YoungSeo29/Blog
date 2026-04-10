const couponBtn = document.getElementById('coupon-btn');

if (couponBtn) {
    couponBtn.addEventListener('click', event => {
        location.href = '/coupon';
    });
}

// 쿠폰 받기
const receiveCouponBtn = document.getElementById('receive-coupon-btn');

if (receiveCouponBtn) {
    receiveCouponBtn.addEventListener('click', async () => {
        const res = await fetch('/api/coupon', {
            method: 'POST'
        });

        if (res.ok) {
            alert('쿠폰이 발급되었습니다!');
            // 잔여 수량 갱신
            fetch('/api/coupon/count')
                .then(res => res.json())
                .then(data => {
                    remainingCountEl.textContent = data.remaining;
                });
        } else if (res.status === 409) {
            alert('이미 쿠폰을 받으셨습니다.');
        } else if (res.status === 400) {
            alert('쿠폰이 모두 소진되었습니다.');
        } else {
            alert('쿠폰 발급에 실패했습니다.');
        }
    });
}

// 잔여 쿠폰 수 조회
const remainingCountEl = document.getElementById('remaining-count');

if (remainingCountEl) {
    fetch('/api/coupon/count')
        .then(res => res.json())
        .then(data => {
            remainingCountEl.textContent = data.remaining;
        });
}