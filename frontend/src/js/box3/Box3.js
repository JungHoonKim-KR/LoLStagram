import React, {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import "../../css/Box3.css";
import SummonerInfo from "./SummonerInfoComponent";
import RecentMatches from "./RecentMatchesComponent";

const Box3 = (searchResult) => {
    const image = require.context("../../images/", true);
    const navigate = useNavigate();
    const [isUpdateLoading, setIsUpdateLoading] = useState(false);
    const [summonerInfo, setSummonerInfo] = useState(
        JSON.parse(localStorage.getItem("mySummonerInfo"))
    );
    const [matchList, setMatchList] = useState(summonerInfo.matchList || []);
    const [type] = useState(searchResult.type);
    const [token, setToken] = useState(localStorage.getItem("accessToken"));
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [page, setPage] = useState(1);
    const [callType, setCallType] = useState(null);
    const [isLast, setIsLast] = useState(true);
    
    const tierList = ["IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM", "EMERALD", "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER"]

    const imageCheck = (type,name) => {
        if(type === "item")
            return image(`./${type}/0.png`);
        else if(type === "tier")
            return image(`./${type}/${name}.png`);
        return image(`./${type}/default.png`);
    };

    const callMatch = useCallback(async () => {
        try {
            const promise = await axios.put(
                `/match/update?page=${page}`,
                {
                    summonerId: summonerInfo.summonerId,
                    type: callType,
                },
                {
                    headers: {Authorization: `Bearer ${token}`},
                    withCredentials: true,
                }
            );
            if (promise.headers.access) {
                localStorage.setItem("accessToken", promise.headers.access);
                setToken(promise.headers.access);
            }
            if (promise.data.matchList.length === 0) {
                setIsLast(true);
            } else {
                setPage((prevPage) => prevPage + 1);
                setMatchList((prevState) => [...prevState, ...promise.data.matchList]);
                setIsLast(promise.data.isLast);
            }
        } catch (error) {
            const errorMessage =
                error.response?.data?.errorMessage || "알 수 없는 오류 발생";
            alert(errorMessage);
            if (errorMessage === "토큰 만료") {
                localStorage.clear();
                navigate("/");
            }
        }
    }, [summonerInfo.summonerId, token, callType, page, navigate]);

    const updateMatch = useCallback((type) => {
        setPage(0);
        setMatchList([]);
        setCallType(type);
    }, []);

    useEffect(() => {
        if(callType !== null)
            callMatch();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [callType]);

    useEffect(() => {
        setSummonerInfo((prevState) => ({
            ...prevState,
            matchList: matchList,
        }));
    }, [matchList]);

    useEffect(() => {
        if (type === "search") {
            let storedSummonerInfo = JSON.parse(localStorage.getItem("searchedSummonerInfo"));
            setSummonerInfo(storedSummonerInfo);
        }
    }, [type]);

    const updateHandler = async () => {
        setIsUpdateLoading(true);
        try {
            const promise = await axios.put(
                "/summoner/update",
                {
                    summonerId: summonerInfo.summonerId,
                },
                {
                    headers: {Authorization: `Bearer ${token}`},
                    withCredentials: true,
                }
            );
            if (promise.headers.access) {
                localStorage.setItem("accessToken", promise.headers.access);
                setToken(promise.headers.access);
            }
            // Update the summonerInfo and matchList directly from the updated data
            setSummonerInfo(promise.data);
            setMatchList(promise.data.matchList || []); // Ensure matchList is updated
            alert("업데이트 완료.");
            localStorage.setItem("mySummonerInfo", JSON.stringify(promise.data));
        } catch (error) {
            const errorMessage =
                error.response?.data?.errorMessage || "알 수 없는 오류 발생";
            alert(errorMessage);
            if (errorMessage === "토큰 만료") {
                localStorage.clear();
                navigate("/");
            }
        } finally {
            setIsUpdateLoading(false);
        }
    };

    const handleClick = () => {
        if (summonerInfo.tier == null) {
            alert("랭크 정보가 없는 소환사와 비교할 수 없습니다.");
        } else {
            setIsModalOpen(!isModalOpen);
        }
    };

    return (
        <div className="box3">
            <SummonerInfo
                summonerInfo={summonerInfo}
                type={type}
                updateHandler={updateHandler}
                isUpdateLoading={isUpdateLoading}
                imageCheck={imageCheck}
                handleClick={handleClick}
                isModalOpen={isModalOpen}
                setIsModalOpen={setIsModalOpen}
            />
            <RecentMatches
                summonerInfo={summonerInfo}
                updateMatch={updateMatch}
                matchList={matchList}
                imageCheck={imageCheck}
                callMatch={callMatch}
                isLast={isLast}
            />
        </div>
    );
};

export default Box3;
