import React from "react";
import DonutChart from "../util/DonutChart";
import addImg from "../../images/더보기.png";

const RecentMatches = ({ summonerInfo, updateMatch, matchList, imageCheck, callMatch, isLast }) => {
    return (
        <div className="recentMatch">
            <div className="title">최근 전적 (20 게임)</div>
            {/* <div>
                <button onClick={() => updateMatch("ALL")}>전체</button>
                <button onClick={() => updateMatch("솔랭")}>솔랭</button>
                <button onClick={() => updateMatch("자유 랭크")}>자랭</button>
                <button onClick={() => updateMatch("무작위 총력전")}>칼바람</button>
                <button onClick={() => updateMatch("URF")}>URF</button>
            </div> */}
            <p>Most 3</p>
            <div className="contentContainer">
                <div className="donut-chart">
                    <DonutChart
                        percentage={(summonerInfo.recentWins / (summonerInfo.recentWins + summonerInfo.recentLosses)) * 100}
                        text={`${summonerInfo.recentWins}승 ${summonerInfo.recentLosses}패`}
                    />
                </div>
                <div className="mostChampionList">
                    <ul>
                        {summonerInfo.mostChampionList.map((mostChampion) => (
                            <li key={mostChampion.kda} className="mostChampion">
                                <img
                                    src={mostChampion.championURL}
                                    alt="mostChampion"
                                />
                                <div className="championInfo">
                                    <span>{mostChampion.championName}</span> {mostChampion.count}판
                                    <br/>
                                    (승률: {mostChampion.avgOfWin}%)
                                    <br/>
                                    {mostChampion.kills}/{mostChampion.deaths}/{mostChampion.assists}
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
            <div className="recentMatchList">
                {matchList.length === 0 ? (
                    <div>결과가 없습니다.</div>
                ) : (
                    <ul>
                        {summonerInfo.matchList.map((match, index) => (
                            <li className={`matchList ${match.result}`} key={index + 100}>
                                <div className={`${match.result} gameType`}>{match.gameType}</div>
                                <div className="summonerList">
                                    <img
                                        className="championImg"
                                        src={match.championURL}
                                    />
                                    <div className="Match">
                                        <div className="summonerImg">
                                            <div className="runeImg">
                                                <div className="mouseOn">
                                                    <img
                                                        id="mainRune"
                                                        key={match.matchId}
                                                        src={match.mainRuneURL}
                                                        alt="mainRune"
                                                    />
                                                </div>
                                                <img
                                                    id="subRune"
                                                    src={match.subRuneURL}
                                                    alt="subRune"
                                                />
                                            </div>
                                            <div className="spellImg">
                                                {match.summonerSpellURLList.map((spellURL, spellIndex) => (
                                                    <span key={spellIndex}>
                            <img src={spellURL} alt={spellURL} />
                          </span>
                                                ))}
                                            </div>
                                        </div>
                                        <div className="kda">
                                            <span id="kills">{match.kills}</span>/
                                            <span id="deaths">{match.deaths}</span>/
                                            <span id="assists">{match.assists}</span>
                                            <span id="kda"> ( KDA: {match.kda} )</span>
                                            <span className={match.result}>
                        {match.result === "true" ? "승리" : "패배"}
                      </span>
                                        </div>
                                    </div>
                                </div>
                                <div className="itemList">
                                    <ul>
                                        {match.itemURLList.map((itemURL, itemIndex) => (
                                            <li key={itemIndex}>
                                                <img src={itemURL || imageCheck("item")} alt={itemURL} />
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
                {!isLast && (
                    <button id="addPostBtn" onClick={callMatch}>
                        <img src={addImg} alt="Add post" />
                    </button>
                )}
            </div>
        </div>
    );
};

export default RecentMatches;