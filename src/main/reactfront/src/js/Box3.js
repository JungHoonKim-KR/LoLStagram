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
    const defaultImage = "default";
    const image = require.context("../images/",true);
    const navigate = useNavigate();
    const [isUpdateLoading, setIsUpdateLoading] = useState(false);
    const [summonerInfo, setSummonerInfo] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")));
    const [matchList, setMatchList] = useState(summonerInfo.matchList);
    const [type] = useState(searchResult.type);
    const [token, setToken] = useState(localStorage.getItem('accessToken'));
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [page, setPage] = useState(1);
    const [callType,setCallType] = useState(null);
    const [isLast, setIsLast] = useState(true);
    const [mouseOverId, setMouseOverId] = useState(null);
    const [objectId, setObjectId] = useState(null);
    const championList = ["0","Jax","Sona","Tristana","Yuumi","Seraphine","Varus","Kaisa","Fiora","Aurora","Singed","Samira","TahmKench","Leblanc","Thresh","Naafiri","Karma","Lillia","Jhin","Rumble","Udyr","LeeSin","Yorick","Ornn","Kayn","Neeko","Kassadin","Sivir","MissFortune","Senna","Smolder","Draven","Hwei","Yasuo","Kayle","Rell","Milio","Shaco","Briar","Renekton","Hecarim","Fizz","KogMaw","Yone","Maokai","Lissandra","Jinx","Urgot","Fiddlesticks","Galio","Pantheon","Talon","Gangplank","Sett","Ezreal","Gnar","Teemo","Annie","Mordekaiser","Azir","Kennen","Riven","Chogath","Aatrox","Poppy","Taliyah","Illaoi","Pyke","Heimerdinger","Alistar","XinZhao","Lucian","Volibear","Sejuani","Nidalee","Garen","Leona","Zed","Blitzcrank","Rammus","Velkoz","KSante","Caitlyn","Trundle","Kindred","Renata","Akshan","Vex","Quinn","Ekko","Nami","Swain","Aphelios","Belveth","Sylas","Taric","Syndra","Rakan","Skarner","Gwen","Braum","Veigar","Xerath","Corki","Nautilus","Ahri","Jayce","Nilah","Darius","Tryndamere","Janna","Elise","Vayne","Brand","Zoe","Graves","Soraka","Xayah","Viego","Karthus","Vladimir","Zilean","Katarina","Shyvana","Warwick","Ziggs","Kled","Khazix","Olaf","TwistedFate","Nunu","Qiyana","Rengar","Bard","Irelia","Ivern","MonkeyKing","Ashe","Kalista","Akali","Vi","Amumu","Lulu","Morgana","Nocturne","Diana","AurelionSol","Zyra","Viktor","Cassiopeia","Nasus","Twitch","DrMundo","Zeri","Orianna","Evelynn","RekSai","Lux","Sion","Camille","MasterYi","Ryze","Malphite","Anivia","Shen","JarvanIV","Malzahar","Zac","Gragas"]
    const itemList = [0,9188,9189,9183,9180,9181,226699,9187,9184,9185,223074,223075,223072,223193,223073,3513,223078,2422,2421,2420,3876,223071,3877,223190,9193,9192,446632,223184,226695,223185,226333,226696,226697,1104,226698,1103,223067,226691,1102,223068,226692,9190,1101,223065,226693,3400,226694,223181,4630,4628,4629,223094,223095,446656,223091,4641,1011,3430,223085,1006,223084,1004,223089,3302,223087,1001,4632,4633,4635,4636,4637,4638,1018,4642,6700,4401,4643,4402,4644,4403,4645,6701,4646,1033,3211,1031,3330,1029,1028,1027,444644,1026,6610,3222,1043,3100,1042,2010,3340,1040,6609,1039,1038,1037,1036,1035,2003,6621,6620,1055,2144,3112,1054,2022,2143,3111,1053,2021,2142,3110,1052,2020,2141,2140,226035,3109,2019,3108,2139,3107,3349,2138,3348,3105,2015,3102,6616,6617,6630,6632,6631,126697,3002,3123,2033,3001,3364,3121,3363,2031,2152,2151,2150,228005,228004,3119,3118,228006,3117,228001,3116,1058,3115,3599,1057,228003,3114,1056,2145,228002,3113,3013,3134,3012,3133,3011,3010,3131,3009,3128,3006,3005,3004,3003,3124,6653,2056,3024,3145,2055,3023,3144,3143,3142,2052,3020,1083,2051,3140,1082,2050,3139,2049,444636,3137,3135,444637,228020,6660,6662,6665,4003,6664,3035,3156,3155,2065,3033,3032,3153,3031,3152,228008,3026,3147,3146,6656,6655,6657,6670,6672,4010,6671,4011,4012,6673,4013,6676,4014,6675,3046,3044,3165,3042,3041,3040,3161,3039,3158,3036,3157,4004,6667,4005,223814,3057,3177,3053,3051,3172,3050,3047,4015,4016,6677,4017,3181,6692,6691,6694,6693,6333,6696,6695,6698,6697,3068,3067,3066,3065,3184,6690,3179,3071,3070,3190,3078,3077,3076,222503,3075,222504,3074,3073,222502,3072,3193,6699,223508,3082,223748,223742,3089,3087,223504,3086,3085,3084,3083,3091,3095,3094,9400,9403,9404,9401,9402,220001,220000,220003,220002,220005,220004,220007,220006,9407,9408,9405,9406,9300,9301,9304,6035,9305,9302,9303,223302,224633,224637,224636,221011,6029,224629,224628,9308,9306,9307,221026,221031,8001,224403,224645,224644,224401,226701,224646,3902,3903,3901,223105,226616,223102,226617,223109,223107,226620,226621,3916,221053,222022,223111,223112,222141,223110,226609,8020,221038,7050,1503,3803,1502,1501,1500,226610,223100,223222,221043,1509,1508,1507,1506,3801,1504,3802,223005,447111,223006,447112,223003,223124,223004,447110,223009,447113,3814,447108,447109,1512,447106,1511,447107,1510,223011,1519,1518,1517,1516,1515,221057,223115,447100,223116,447101,221058,223119,447104,447105,447102,223118,447103,226630,226631,1522,226632,1521,223001,1520,223002,223121,223146,223026,226655,443054,226656,443055,226657,443056,226662,2504,223031,223152,2503,226664,2502,226665,2501,223156,223032,223153,222065,223033,2508,9278,9279,9276,9277,223137,446671,9271,223135,9274,9275,223139,9272,9273,222051,223020,226653,446667,3600,223142,223143,9168,9289,9287,9288,3860,443193,9281,223047,9280,9285,9283,443079,9284,3858,443069,223053,3859,223050,2403,223172,223177,223057,3850,3851,3853,3854,3855,3857,9179,9177,9178,3870,3871,443060,443061,446693,9171,9292,223039,443062,9172,9293,223036,223157,224005,443063,446691,9290,223158,224004,443064,9175,226667,9176,9173,9174,226673,3748,3869,443058,223042,3504,443059,226675,223040,223161,226676,223046,226671,223165,226672,3862,3742,3863,3864,3865,3508,3866,3867]
    const runeList = [0,8100,8112,8128,9923,8126,8139,8143,8136,8120,8138,8135,8105,8106,8300,8351,8360,8369,8306,8304,8321,8313,8352,8345,8347,8410,8316,8000,8005,8021,8010,9101,9111,8009,9104,9105,9103,8014,8017,8299,8400,8437,8439,8465,8446,8463,8401,8429,8444,8473,8451,8453,8242,8200,8214,8229,8230,8224,8226,8275,8210,8234,8233,8237,8232,8236]
    const spellList = [0,21,30,39,1,12,4,32,7,13,31,11,2202,2201,55,3,54,14,6]
    const tierList = ["IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM", "EMERALD", "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER"]

    const callMatch = useCallback(async () => {
        try {
            const promise = await axios.put(`/match/update?page=${page}`, {
                summonerId: summonerInfo.summonerId,
                type: callType
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
            alert(error.response.data.errorMessage);
            navigate("/");
        }
    }, [summonerInfo.summonerId, token, page, callType,navigate()]);

    const updateMatch = useCallback((type) => {
        setPage(0);
        setMatchList([]);
        setCallType(type);
    }, []);

    useEffect(() => {
        if (callType) {
            console.log(`callType has been updated to: ${callType}`);
            callMatch();
        }
    }, [callType], callMatch());  // callType 변경 감지

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
            alert(error.response.data.errorMessage)
            navigate("/")
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

    const imageCheck = (type, name)=>{
        if(type === "champion"){
            if(championList.includes(name))
                return image(`./champion/${name}.png`);
            else return image(`./champion/${defaultImage}.png`);
        }
        else if(type === "item"){
            if(itemList.includes(name))
                return image(`./item/${name}.png`);
            else return image(`./item/${defaultImage}.png`);
        }
        else if(type === "rune"){
            if(runeList.includes(name))
                return image(`./rune/${name}.png`);
            else return image(`./rune/${defaultImage}.png`);
        }
        else if(type === "spell"){
            if(spellList.includes(name))
                return image(`./spell/${name}.png`);
            else return image(`./spell/${defaultImage}.png`);
        }
        else if(type === "tier"){
            if(tierList.includes(name))
                return image(`./tier/${name}.png`);
            else return image(`./tier/${defaultImage}.png`);
        }
    }

    return(
        <div className="box3">
            <div className="myInfo">
                <div className="title">랭크 정보</div>

                <div className="contentContainer">
                    <div className="tier-image">
                        <img src={imageCheck("tier",summonerInfo.tier)} alt = "tier"/>

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
                                    <img src={imageCheck("champion",mostChampion.championName)} alt ="mostChampion"/>
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
                                        <img
                                            className="championImg"
                                            src={imageCheck("champion",match.championName)}
                                            alt={match.championName}
                                        />
                                        <div className="Match">
                                            <div className="summonerImg">
                                                <div className="runeImg">
                                                    <div className="mouseOn"
                                                         onMouseEnter={()=> {setMouseOverId(match.matchId); setObjectId(match.mainRune)}}
                                                         onMouseLeave={()=> setMouseOverId(null)}>
                                                        <img
                                                            id="mainRune"
                                                            key={match.matchId}
                                                            src={imageCheck("rune",match.mainRune)}
                                                            alt="mainRune"
                                                        />
                                                        {mouseOverId ===match.matchId && objectId === match.mainRune &&<Info type="rune" id ={match.mainRune}></Info>}
                                                    </div>
                                                    <img
                                                        id="subRune"
                                                        src={imageCheck("rune",match.subRune)}
                                                        alt="subRune"/>
                                                </div>
                                                <div className="spellImg">
                                                    {match.summonerSpellList.map((spell, spellIndex) => (
                                                        <span  onMouseEnter={()=> {setMouseOverId(match.matchId); setObjectId(spell)}}
                                                               onMouseLeave={()=> setMouseOverId(null)}>
                                                            <img key={spellIndex} src={imageCheck("spell",spell)} alt={spell}/>
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
                                                    <img
                                                        src={imageCheck("item",item)}
                                                        alt={item}
                                                    />
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