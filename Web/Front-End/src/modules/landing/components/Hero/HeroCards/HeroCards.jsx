import "./HeroCards.css";
import { useTranslation } from "react-i18next";

import {
    FaLeaf,
    FaChartLine,
    FaBell,
    FaDatabase
} from "react-icons/fa";

// Subcomponente reutilizable: recibe sus datos por props 

function HeroCard({ icon, text, breakLines = false }) {
    return (
        <div className="hero-card">
            <div className="hero-card-icon">{icon}</div>
            <span>
                {breakLines
                    ? text.split(" ").map((word, i) => (
                          <span key={i}>{word}<br /></span>
                      ))
                    : text}
            </span>
        </div>
    );
}

function HeroCards() {
    const { t } = useTranslation();

    
    const cardsData = [
        { key: "healthy", icon: <FaLeaf />, textKey: "landing.hero.cards.healthy" },
        { key: "realtime", icon: <FaChartLine />, textKey: "landing.hero.cards.realtime", breakLines: true },
        { key: "alerts", icon: <FaBell />, textKey: "landing.hero.cards.alerts" },
        { key: "reports", icon: <FaDatabase />, textKey: "landing.hero.cards.reports" },
    ];

    // Ciclo forEach: recorre una por una las tarjetas 
    
    const cards = [];
    cardsData.forEach((card) => {
        cards.push(
            <HeroCard
                key={card.key}
                icon={card.icon}
                text={t(card.textKey)}
                breakLines={card.breakLines}
            />
        );
    });

    return (
        <div className="hero-cards">
            {cards}
        </div>
    );
}

export default HeroCards;
