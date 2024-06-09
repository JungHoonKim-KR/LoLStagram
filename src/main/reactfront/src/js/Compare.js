import Modal from "react-modal";
import "../css/Compare.css";
import "../css/Box3.css";
import upperImg from "../images/upper.png";
import React, { useState } from "react";
Modal.setAppElement('#root');

const Compare = ({ isOpen, onClose }) => {
    const Tier = {
        IRON: 1,
        BRONZE: 2,
        SILVER: 3,
        GOLD: 4,
        PLATINUM: 5,
        EMERALD: 6,
        DIAMOND: 7,
        MASTER: 8,
        GRANDMASTER: 9,
        CHALLENGER: 10
    };
    const [summoner1] = useState(JSON.parse(localStorage.getItem("searchedSummonerInfo")));
    const [summoner2] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")));
    const tier1 = Tier[summoner1.tier];
    const tier2 = Tier[summoner2.tier];

    const comparedTier = parseFloat((tier1 - tier2).toFixed(1));
    const comparedPoint = parseFloat((summoner1.leaguePoints - summoner2.leaguePoints).toFixed(1));
    const comparedKda = parseFloat((summoner1.totalKda - summoner2.totalKda).toFixed(1));
    const comparedWin = parseFloat((summoner1.totalAvgOfWin - summoner2.totalAvgOfWin).toFixed(1));

    const compareItems = [
        { name: '티어', value: comparedTier },
        { name: '승점', value: comparedPoint },
        { name: 'KDA', value: comparedKda },
        { name: '승률', value: comparedWin }
    ];

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={() => onClose(false)}
        >
            <div className="modal-overlay">
                <div className="modal-content">
                    <div className="summonerInfo">
                        <div id="summoner1">
                            <div className="tier-image">
                                <img src={require(`../images/tierImage/${summoner1.tier}.png`)} alt={summoner1.tier} />
                            </div>
                            <div className="summoner">
                                <p>{summoner1.summonerName} #{summoner1.summonerTag}</p>
                                <div className="tier">
                                    <p>{summoner1.tier}</p>
                                    <p>{summoner1.rank}</p> &nbsp;
                                    <p>{summoner1.leaguePoints}P</p>
                                </div>
                            </div>
                        </div>
                        <div id="summoner2">
                            <div className="tier-image">
                                <img src={require(`../images/tierImage/${summoner2.tier}.png`)} alt={summoner2.tier} />
                            </div>
                            <div className="summoner">
                                <p>{summoner2.summonerName} #{summoner2.summonerTag}</p>
                                <div className="tier">
                                    <p>{summoner2.tier}</p>
                                    <p>{summoner2.rank}</p> &nbsp;
                                    <p>{summoner2.leaguePoints}P</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <hr></hr>
                    <div className="compareList">
                        {compareItems.map(item => (
                            <div className="compareValue">
                                <div className="compareItem" >{item.name}
                                    {item.value > 0 &&
                                        <div className="leftValue">
                                            <div className="value">{Math.abs(item.value)}</div>
                                            <img className="upperImg" src={upperImg} alt="Increase" />
                                        </div>
                                    }

                                    {item.value < 0 &&
                                        <div className="rightValue">
                                            <img className="upperImg" src={upperImg} alt="Decrease" />
                                            <div className="value">{Math.abs(item.value)}</div>
                                        </div>
                                    }
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </Modal>
    );
};

export default Compare;
