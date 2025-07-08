// Explain Page JavaScript Effects

// 스크롤 시 카드들에 추가 애니메이션 효과
window.addEventListener('scroll', function () {
    const cards = document.querySelectorAll('.feature-card');
    cards.forEach(card => {
        const cardTop = card.getBoundingClientRect().top;
        const cardVisible = 150;

        if (cardTop < window.innerHeight - cardVisible) {
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }
    });
});

// 페이지 로드 시 부드러운 스크롤
document.addEventListener('DOMContentLoaded', function () {
    window.scrollTo({ top: 0, behavior: 'smooth' });
});
