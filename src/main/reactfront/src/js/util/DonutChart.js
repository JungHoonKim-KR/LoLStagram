import React from "react";

function DonutChart({ percentage, text = "" }) {
    const radius = 110; // 반지름
    const strokeWidth = 20; // 두께
    const circumference = 2 * Math.PI * radius;
    const strokeDashoffset = circumference - (percentage / 100) * circumference;

    return (
        <svg width="200px" viewBox="0 0 250 250">
            {/* 배경 원 */}
            <circle
                stroke="#f0f0f0"
                strokeWidth={strokeWidth}
                fill="transparent"
                r={radius}
                cx="125"
                cy="125"
            />
            {/* 퍼센트에 따른 원 */}
            <circle
                stroke="#3498db"
                strokeWidth={strokeWidth}
                fill="transparent"
                r={radius}
                cx="125"
                cy="125"
                strokeDasharray={circumference}
                strokeDashoffset={strokeDashoffset}
                transform="rotate(-90 125 125)"
                style={{ transition: "stroke-dashoffset 0.5s ease" }} // 애니메이션 추가
            />
            <text
                x="50%"
                y="50%"
                textAnchor="middle"
                fill="#000000"
                fontSize="20"
                dy=".3em" // 텍스트 수직 정렬 조정
            >
                {text}
            </text>
        </svg>
    );
}

export default DonutChart;
