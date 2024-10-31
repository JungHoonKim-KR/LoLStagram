import React from "react";
import {BeatLoader} from "react-spinners";
import Compare from "../util/Compare";

const SummonerInfo = ({
                          summonerInfo,
                          type,
                          updateHandler,
                          isUpdateLoading,
                          imageCheck,
                          handleClick,
                          isModalOpen,
                          setIsModalOpen,
                      }) => {
    return (
        <div className="myInfo">
            <div className="title">랭크 정보</div>
            <div className="contentContainer">
                <div className="tier-image">
                    <img
                        src={imageCheck("tier", summonerInfo.tier)}
                        alt="tier"
                    />
                </div>
                <div className="summoner">
                    <p>
                        {summonerInfo.summonerName} #{summonerInfo.summonerTag}
                    </p>
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
                        {isUpdateLoading ? <BeatLoader size={10} color={"#123abc"} loading={isUpdateLoading}/> : "갱신하기"}
                    </button>
                    {type === "search" && (
                        <button onClick={handleClick}>
                            비교하기
                            {isModalOpen && <Compare isOpen={isModalOpen} onClose={setIsModalOpen}/>}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SummonerInfo;
