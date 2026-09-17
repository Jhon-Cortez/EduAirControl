import { useState, useEffect } from "react";
import "./ScrollToTop.css";
import { useTranslation } from 'react-i18next';

function ScrollToTop() {
    const { t } = useTranslation();
    const [visible, setVisible] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setVisible(window.scrollY > 300);
        };

        window.addEventListener("scroll", handleScroll);

        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    };

    return (
        <button
            className={`scroll-to-top ${visible ? "show" : ""}`}
            onClick={scrollToTop}
            aria-label={t('landing.scroll.backToTop')}
            title={t('landing.scroll.backToTop')}
        >
            <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="scroll-to-top-icon"
                aria-hidden="true"
            >
                <polyline points="18 15 12 9 6 15" />
            </svg>
        </button>
    );
}

export default ScrollToTop;