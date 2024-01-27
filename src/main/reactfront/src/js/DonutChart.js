import React from 'react';

function DonutChart({ percentage,text =''   }) {
    const radius = 110; // 반지름을 늘립니다.
    const strokeWidth = 20; // 두께를 늘립니다.
    const circumference = 2 * Math.PI * radius;
    const strokeDashoffset = circumference - (percentage / 100) * circumference;

    return (
        <svg width="200" height="200" viewBox="0 0 250 250"> {/* SVG의 크기를 늘립니다. */}
            <circle
                stroke="#f0f0f0"
                strokeWidth={strokeWidth}
                fill="transparent"
                r={radius}
                cx="125"
                cy="125"
            />
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
            />
            <text
                x="50%"
                y="50%"
                textAnchor="middle"
                stroke="#000000"
                fontSize="20"
            >
                {text} {/* 텍스트를 출력합니다. */}
            </text>
        </svg>
    );
}

export default DonutChart;
