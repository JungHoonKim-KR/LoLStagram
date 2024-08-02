import React, { useState } from "react";
import axios from "axios";

const ProfileComponent = ({ token, memberInfo, navigate }) => {
    const [isProfileVisible, setIsProfileVisible] = useState(false);
    const [summonerNameProfile, setSummonerNameProfile] = useState("");
    const [summonerTagProfile, setSummonerTagProfile] = useState("");

    const profileVisibility = () => {
        setIsProfileVisible(!isProfileVisible);
    };

    const handleProfile = async () => {
        if (!summonerNameProfile.trim() || !summonerTagProfile.trim()) {
            alert("모든 필드를 입력해주세요.");
            return;
        } else {
            try {
                await axios.put(
                    "/profile/update",
                    {
                        id: memberInfo.id,
                        summonerName: summonerNameProfile.trim(),
                        summonerTag: summonerTagProfile.trim(),
                    },
                    {
                        headers: { Authorization: `Bearer ${token}` },
                        withCredentials: true,
                    }
                ).then(() => {
                    alert("변경이 완료되었습니다. 로그인 페이지로 이동합니다.");
                    navigate("/");
                });
            } catch (error) {
                const errorMessage = error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            }
        }
    };

    return (
        <li>
            <span onClick={profileVisibility}>소환사 변경</span>
            {isProfileVisible && (
                <div>
                    <div id="searchArea">
                        <input
                            type="text"
                            placeholder="소환사명"
                            value={summonerNameProfile}
                            onChange={(e) => setSummonerNameProfile(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="태그"
                            value={summonerTagProfile}
                            onChange={(e) => setSummonerTagProfile(e.target.value)}
                        />
                        <button type="submit" id="updateBtn" onClick={handleProfile}>
                            확인
                        </button>
                    </div>
                </div>
            )}
        </li>
    );
};

export default ProfileComponent;
