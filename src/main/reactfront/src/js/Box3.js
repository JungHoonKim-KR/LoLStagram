import DonutChart from "./DonutChart";
import React, {useEffect, useState} from "react";
import '../css/Box3.css';
import axios from "axios";
import { BeatLoader } from 'react-spinners';
import Compare from './Compare';
const Box3 = (searchResult)=>{
    const [isUpdateLoading,setIsUpdateLoading] = useState(false)
    const [isCompareLoading, setIsCompareLoading] = useState(false)
    const [summonerInfo, setSummonerInfo] = useState(searchResult.summerInfo);
    console.log(searchResult.summerInfo)
    const [type, setType] = useState(searchResult.type)
    const token = localStorage.getItem('accessToken');
    const [isModalOpen, setIsModalOpen] = useState(false);
    useEffect(() => {
        let storedSummonerInfo
        if(type == "search"){
             storedSummonerInfo = JSON.parse(localStorage.getItem("searchedSummonerInfo"));
        }else {
             storedSummonerInfo = JSON.parse(localStorage.getItem("mySummonerInfo"));
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

            setSummonerInfo(promise.data)
            localStorage.setItem("summonerInfo", JSON.stringify(promise.data))
        }catch (error){
            console.log(error)
        }finally {
            setIsUpdateLoading(false)
        }
    }


    const handleClick = () => {
        setIsModalOpen(!isModalOpen);
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
                        <img src={require(`../images/tierImage/${summonerInfo.tier}.png`)} />

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
                <div className="title">최근전적 (10 게임)</div>
                <p>Most 3</p>
                <div className="contentContainer">
                    <div className="donut-chart">
                        <DonutChart percentage={summonerInfo.recentWins*10}
                                    text={summonerInfo.recentWins + "승 "+ summonerInfo.recentLosses +"패"} />
                    </div>

                    <div className="mostChampionList">
                        <ul >
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
                <ul>
                    {summonerInfo.matchList.map((match,index) =>(
                        <li id="matchList" key={index+100} className={match.result}>
                            <div className="summonerList">
                                <img className="championImg" src={require(`../images/champion/${match.championName}.png`)} />
                                <div className="matchInfo">
                                    <div className="summonerImg">
                                        <div className="runeImg">
                                            <img id="mainRune" src={require(`../images/rune/${match.mainRune}.png`)}/>
                                            <img id="subRune" src={require(`../images/rune/${match.subRune}.png`)}/>
                                        </div>
                                        <div className="spellImg">
                                            {match.summonerSpellList.map(spell =>(
                                                <img src={require(`../images/spell/${spell}.png`)}/>
                                            ))}
                                        </div>
                                    </div>
                                    <div className="kda">
                                        <span id="kills">{match.kills}</span>/
                                        <span id="deaths">{match.deaths}</span>/
                                        <span id="assists">{match.assists}</span>
                                        <span id="kda"> ( KDA: {match.kda} )</span>
                                        <span id={match.result}>{match.result=="true" ? "승리" : "패배"}</span>
                                    </div>
                                </div>
                            </div>
                            <div className="itemList">
                                <ul>
                                    {match.itemList.map(item =>(
                                        <li key={item}>
                                            <img src={require(`../images/item/${item}.png`)}></img>
                                        </li>
                                    ))}
                                </ul>

                            </div>
                        </li>
                    ))}
                </ul>
            </div>

        </div>

    )
}
export default Box3