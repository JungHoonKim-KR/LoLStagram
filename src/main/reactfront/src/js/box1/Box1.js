import React, { useEffect, useState } from "react";
import "../../css/Box1.css";
import Box3 from "../box3/Box3";
import { useNavigate } from "react-router-dom";
import SearchComponent from "./SearchComponent";
import ProfileComponent from "./ProfileComponent";
import WriteComponent from "./WriteComponent";
import LogoutComponent from "./LogoutComponent";

const Box1 = () => {
    const navigate = useNavigate();
    const [searchResult, setSearchResult] = useState(null);
    const [isSearchLoading, setIsSearchLoading] = useState(false);
    const [showBox3, setShowBox3] = useState(false);
    const [token, setToken] = useState(localStorage.getItem("accessToken"));
    const [summonerInfo] = useState(
        JSON.parse(localStorage.getItem("mySummonerInfo"))
    );
    const [memberInfo] = useState(JSON.parse(localStorage.getItem("member")));

    useEffect(() => {
        if (searchResult) {
            setShowBox3(true);
        }
    }, [searchResult]);

    const handleSearchVisibility = () => {
        setSearchResult(null);  // 검색 결과 초기화
        setShowBox3(false);  // Box3 숨기기
    };

    return (
        <div className="box1">
            <div className="memberInfo">
                <span>{memberInfo.username}님 안녕하세요</span>
            </div>
            <div className="menu">
                <span>Menu</span>
            </div>
            <div className="menuLine">
                <ul>
                    <SearchComponent
                        token={token}
                        setToken={setToken}
                        setSearchResult={setSearchResult}
                        isSearchLoading={isSearchLoading}
                        setIsSearchLoading={setIsSearchLoading}
                        onSearchVisibility={handleSearchVisibility}  // 검색창 상태 제어
                        navigate={navigate}
                    />
                    {showBox3 && searchResult && (
                        <Box3
                            key={searchResult.leagueId}
                            summerInfo={searchResult}
                            type="search"
                            label="searchResult"
                        />
                    )}
                    <ProfileComponent
                        token={token}
                        memberInfo={memberInfo}
                        navigate={navigate}
                    />
                    <WriteComponent
                        token={token}
                        setToken={setToken}
                        memberInfo={memberInfo}
                        navigate={navigate}
                    />
                    <LogoutComponent
                        token={token}
                        summonerInfo={summonerInfo}
                        navigate={navigate}
                    />
                </ul>
            </div>
        </div>
    );
};

export default Box1;
