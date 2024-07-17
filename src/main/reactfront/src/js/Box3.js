import React, { useCallback, useEffect, useState } from 'react';
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import DonutChart from "./DonutChart";
import Info from "./Info";
import Compare from './Compare';
import '../css/Box3.css';
import { BeatLoader } from 'react-spinners';
import addImg from "../images/더보기.png";

const Box3 = (searchResult) => {
    const navigate = useNavigate();
    const [isUpdateLoading, setIsUpdateLoading] = useState(false);
    const [summonerInfo, setSummonerInfo] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")));
    const [matchList, setMatchList] = useState(summonerInfo.matchList);
    const [type] = useState(searchResult.type);
    const [token, setToken] = useState(localStorage.getItem('accessToken'));
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [page, setPage] = useState(1);
    const [callType, setCallType] = useState(null);
    const [isLast, setIsLast] = useState(true);
    const [mouseOverId, setMouseOverId] = useState(null);
    const [objectId, setObjectId] = useState(null);

    const callMatch = useCallback(async (callType) => {
        try {
            const promise = await axios.put('/match/update', {
                summonerId: summonerInfo.summonerId,
                type: callType,
                page: page
            }, {
                headers: { 'Authorization': `Bearer ${token}` },
                withCredentials: true,
            });
            if (promise.headers.access) {
                localStorage.setItem('accessToken', promise.headers.access);
                setToken(promise.headers.access);
            }
            if (promise.data.MatchDtoList.length === 0) {
                console.log("No results found.");
            } else {
                setPage(prevPage => prevPage + 1);
                setMatchList(prevState => [...prevState, ...promise.data.MatchDtoList]);
                setIsLast(promise.data.isLast);
            }
        } catch (error) {
            console.error(error);
            if (error.response?.data?.errorCode === "TOKEN_EXPIRED") {
                alert("Token expired. Redirecting to login page.");
                navigate("/");
            } else {
                alert(error.response?.data?.errorMessage);
            }
        }
    }, [summonerInfo.summonerId, token, page, navigate]);

    useEffect(() => {
        if (page === 0 && matchList.length === 0 && callType) {
            callMatch(callType);
        }
    }, [page, matchList, callType, callMatch]);

    const updateMatch = async (callType) => {
        setPage(0);
        setMatchList([]);
        await callMatch(callType);
    }

    useEffect(() => {
        setSummonerInfo(prevState => ({
            ...prevState,
            matchList: matchList,
        }));
    }, [matchList]);
    useEffect(() => {
        if(type === "search"){
            let storedSummonerInfo = JSON.parse(localStorage.getItem("searchedSummonerInfo"));
            setSummonerInfo(storedSummonerInfo);
        }

    }, [type]);
    const updateHandler = async () => {
        setIsUpdateLoading(true)
        try {
            const promise = await axios.put('/summoner/update',     {
                    summonerId : summonerInfo.summonerId,
                },
                {
                    headers: {'Authorization': `Bearer ${token}`},
                    withCredentials: true, // 쿠키를 포함하여 요청을 보냄
                });
            if(promise.headers.access){
                localStorage.setItem('accessToken', promise.headers.access);
                setToken(promise.headers.access)
            }
            setSummonerInfo(promise.data)
            localStorage.setItem("mySummonerInfo", JSON.stringify(promise.data))
        }catch (error){
            if(error.response.data.errorCode === "TOKEN_EXPIRED"){
                alert("토큰 만료. 로그인 화면으로 이동합니다.")
                navigate("/")
            }
            else{
                alert(error.response.data.errorMessage)
            }
        }finally {
            setIsUpdateLoading(false)
        }
    }


    const handleClick = () => {
        if(summonerInfo.tier==null) {
            alert("랭크 정보가 없는 소환사와 비교할 수 없습니다.")
        }
        else{
            setIsModalOpen(!isModalOpen);
        }
    };
    return(
        <div className="box3">
            <div className="myInfo">
                <div className="title">랭크 정보</div>

                <div className="contentContainer">
                    <div className="tier-image">
                        {summonerInfo.tier?<img src={require(`../images/tierImage/${summonerInfo.tier}.png`)} alt = "tier"/>:'undefined' }

                    </div>
                    <div className="summoner">
                        <p>{summonerInfo.summonerName} #{summonerInfo.summonerTag}</p>
                        <div className="tier">
                            <p>{summonerInfo.tier}</p>
                            <p>{summonerInfo.rank}</p> &nbsp;
                            <p>{summonerInfo.leaguePoints}P</p>
                        </div>
                    </div>
                    <div className="matches">
                        <div className="winlose">
                            <p>{summonerInfo.totalWins}승</p>
                            <p>{summonerInfo.totalLosses}패</p>
                        </div>
                        <div>
                            <p>승률 {summonerInfo.totalAvgOfWin}%</p>
                        </div>
                    </div>
                    <div className="update">
                        <button onClick={updateHandler} disabled={isUpdateLoading}>
                            {isUpdateLoading ? <BeatLoader size={10} color={"#123abc"} loading={isUpdateLoading} /> : '갱신하기'}
                        </button >
                        {type === "search" &&
                            <button  onClick={handleClick} >비교하기
                                {isModalOpen && <Compare isOpen={isModalOpen} onClose={setIsModalOpen} />}</button>}
                    </div>
                </div>
            </div>
            <div className="recentMatch">
                <div className="title">최근전적 (20 게임)</div>
                <div>
                    <button onClick={() =>
                        updateMatch("ALL")}>전체
                    </button>
                    <button onClick={() =>
                        updateMatch("솔랭")}>솔랭
                    </button>
                    <button onClick={() =>
                        updateMatch("자유 랭크")}>자랭
                    </button>
                    <button onClick={() =>
                        updateMatch("무작위 총력전")}>칼바람
                    </button>
                    <button onClick={() =>
                        updateMatch("URF")}>URF
                    </button>
                </div>
                <p>Most 3</p>
                <div className="contentContainer">
                    <div className="donut-chart">
                        <DonutChart percentage={summonerInfo.recentWins*10}
                                    text={summonerInfo.recentWins + "승 "+ summonerInfo.recentLosses +"패"} />
                    </div>

                    <div className="mostChampionList">
                        <ul>
                            {summonerInfo.mostChampionList.map((mostChampion, index) => (
                                <li key={mostChampion.kda} className={'mostChampion'}>
                                    <img src={require(`../images/champion/${mostChampion.championName}.png`)} alt ="mostChampion"/>
                                    <div className="championInfo">
                                        {mostChampion.championName} {mostChampion.count}판<br/>
                                        (승률:{mostChampion.avgOfWin}%)<br/>
                                        {mostChampion.kills}/{mostChampion.deaths}/{mostChampion.assists}
                                    </div>
                                </li>
                            ))}
                        </ul>

                    </div>
                </div>
            </div>

            <div className="recentMatchList">
                {
                    summonerInfo.matchList.length === 0 ? (
                        <div>결과가 없습니다.</div>
                    ) : (
                        <ul>
                            {summonerInfo.matchList.map((match, index) => (
                                <li className={`matchList ${match.result}`} key={index + 100}>
                                    <div className={`${match.result} gameType`}>{match.gameType}</div>
                                    <div className="summonerList">
                                        <img className="championImg" src={require(`../images/champion/${match.championName}.png`)} alt={match.championName} />
                                        <div className="Match">
                                            <div className="summonerImg">
                                                <div className="runeImg">
                                                    <div className="mouseOn"
                                                         onMouseEnter={()=> {setMouseOverId(match.matchId); setObjectId(match.mainRune)}}
                                                         onMouseLeave={()=> setMouseOverId(null)}>
                                                        <img id="mainRune" key={match.matchId} src={require(`../images/rune/${match.mainRune}.png`)} alt="mainRune"
                                                        />
                                                        {mouseOverId ===match.matchId && objectId === match.mainRune &&<Info type="rune" id ={match.mainRune}></Info>}
                                                    </div>
                                                    <img id="subRune" src={require(`../images/rune/${match.subRune}.png`)} alt="subRune"/>
                                                </div>
                                                <div className="spellImg">
                                                    {match.summonerSpellList.map((spell, spellIndex) => (
                                                        <span  onMouseEnter={()=> {setMouseOverId(match.matchId); setObjectId(spell)}}
                                                               onMouseLeave={()=> setMouseOverId(null)}>
                                                            <img key={spellIndex} src={require(`../images/spell/${spell}.png`)} alt={spell}/>
                                                            {mouseOverId ===match.matchId && objectId === spell &&<Info type="spell" id ={spell}></Info>}
                                                        </span>
                                                    ))}
                                                </div>
                                            </div>
                                            <div className="kda">
                                                <span id="kills">{match.kills}</span>/
                                                <span id="deaths">{match.deaths}</span>/
                                                <span id="assists">{match.assists}</span>
                                                <span id="kda"> ( KDA: {match.kda} )</span>
                                                <span className={match.result}>{match.result === "true" ? "승리" : "패배"}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="itemList">
                                        <ul>
                                            {match.itemList.map((item, itemIndex) => (
                                                <li key={itemIndex}>
                                                    <img src={require(`../images/item/${item}.png`)} alt={item}></img>
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )
                }
                {!isLast &&(
                    <button  id="addPostBtn" onClick={callMatch}>
                        <img src={addImg} alt="Add post" />
                    </button>

                )}

            </div>

        </div>

    )
}
export default Box3