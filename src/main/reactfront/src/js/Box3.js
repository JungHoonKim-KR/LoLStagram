import DonutChart from "./DonutChart";
import React, {useEffect, useState} from "react";
import '../css/Box3.css';
import axios from "axios";
import { BeatLoader } from 'react-spinners';
import Compare from './Compare';
import { useNavigate } from 'react-router-dom';
import addImg from "../images/더보기.png";

const Box3 = (searchResult)=>{
    const navigate = useNavigate();
    const [isUpdateLoading,setIsUpdateLoading] = useState(false)
    const [summonerInfo, setSummonerInfo] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")))
    const [matchList, setMatchList] = useState(summonerInfo.matchList)
    const [type, setType] = useState(searchResult.type)
    const [token,setToken] = useState(localStorage.getItem('accessToken'));
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [page,setPage] = useState(1)
    const [callType, setCallType] = useState(null);
    const [isLast,setIsLast]=useState(true)
    useEffect(() => {
        if (page === 0 && matchList.length === 0 && callType) {
            callMatchInfo(callType);
        }
    }, [page, matchList, callType]);

    const updateMatchInfo = async (callType)=>{
        setPage(0)
        setMatchList([])
        await callMatchInfo((callType))
    }
    const callMatchInfo =async(callType)=>{
        try{
            const promise = await axios.post('/matchUpdate',{
                summonerId : summonerInfo.summonerId,
                type : callType,
                page:page
                },{
                    headers: {'Authorization': `Bearer ${token}`},
                    withCredentials: true, // 쿠키를 포함하여 요청을 보냄
                }
            )
            if(promise.headers.access){
                localStorage.setItem('accessToken', promise.headers.access);
                setToken(promise.headers.access)
            }
            if(promise.data.matchInfoDtoList.length ==0){
                console.log("결과가 없습니다.")
            }
            else {
                setPage(page + 1)
                setMatchList(prevState => [...prevState, ...promise.data.matchInfoDtoList])
                setIsLast(promise.isLast)
            }
        }catch (error){
            console.log(error)
            if(error)console.log("dwdwda")
            else if(error.response.data.errorCode == "TOKEN_EXPIRED"){
                alert("토큰 만료. 로그인 화면으로 이동합니다.")
                navigate("/")
            }
            else{
                alert(error.response.data.errorMessage)
            }

        }

    }
    useEffect(() => {
        setSummonerInfo(prevState => ({
            ...prevState,
            matchList: matchList,
        }));
    }, [matchList]);
    useEffect(() => {
        let storedSummonerInfo
        if(type == "search"){
             storedSummonerInfo = JSON.parse(localStorage.getItem("searchedSummonerInfo"));
        }
        if (storedSummonerInfo) {
            setSummonerInfo(storedSummonerInfo);
        }
    }, []);
    const updateHandler = async () => {
        setIsUpdateLoading(true)
        try {
            const promise = await axios.post('/update',     {
                    summonerName: summonerInfo.summonerName,
                    summonerTag: summonerInfo.summonerTag
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
            localStorage.setItem("summonerInfo", JSON.stringify(promise.data))
        }catch (error){
            if(error.response.data.errorCode == "TOKEN_EXPIRED"){
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
    const handleCloseModal = () => {
        setIsModalOpen(false);
    };
    return(
        <div className="box3">
            <div className="myInfo">
                <div className="title">랭크 정보</div>

                <div className="contentContainer">
                    <div className="tier-image">
                        {summonerInfo.tier?<img src={require(`../images/tierImage/${summonerInfo.tier}.png`)}/>:'undefined' }

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
                        {type == "search" &&
                            <button  onClick={handleClick} >비교하기
                                {isModalOpen && <Compare isOpen={isModalOpen} onClose={setIsModalOpen} />}</button>}
                    </div>
                </div>
            </div>
            <div className="recentMatch">
                <div className="title">최근전적 (20 게임)</div>
                <div>
                    <button onClick={()=>{
                        updateMatchInfo("솔랭")
                    }}>버튼1</button>
                    <button onClick={()=>{
                        setPage(0)
                        setMatchList([])
                        setCallType("자유 랭크")
                    }}>버튼2</button>
                    <button onClick={()=>{
                        setPage(0)
                        setMatchList([])
                        setCallType("무작위 총력전")
                    }}>버튼3</button>
                    <button onClick={()=>{
                        setPage(0)
                        setMatchList([])
                        setCallType("URF")
                    }}>버튼4</button>
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
                                <li key={mostChampion.kda} className={index === 0 ? 'first champion' : 'champion'}>
                                    <img src={require(`../images/champion/${mostChampion.championName}.png`)} />
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
                                        <div className="matchInfo">
                                            <div className="summonerImg">
                                                <div className="runeImg">
                                                    <img id="mainRune" src={require(`../images/rune/${match.mainRune}.png`)} alt="mainRune"/>
                                                    <img id="subRune" src={require(`../images/rune/${match.subRune}.png`)} alt="subRune"/>
                                                </div>
                                                <div className="spellImg">
                                                    {match.summonerSpellList.map((spell, spellIndex) => (
                                                        <img key={spellIndex} src={require(`../images/spell/${spell}.png`)} alt={spell}/>
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
                    <button  id="addPostBtn" onClick={callMatchInfo}>
                        <img src={addImg} alt="Add post" />
                    </button>

                )}

            </div>

        </div>

    )
}
export default Box3