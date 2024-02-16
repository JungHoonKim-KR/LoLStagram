import React, {useEffect, useRef, useState} from "react";
import "../css/Box1.css"
import axios from "axios";
import Box3 from "./Box3";
import { BeatLoader } from 'react-spinners';
import { useNavigate } from 'react-router-dom';
    const Box1 = () => {
        const navigate = useNavigate();
        const [searchResult, setSearchResult] = useState(null);
        const [isSearchVisible, setIsSearchVisible] = useState(false);
        const [isProfileVisible, setIsProfileVisible] = useState(false);
        const [isSearchLoading, setIsSearchLoading] =useState(false)
        const [isWriteVisible,setIsWriteVisible] = useState(false)
        const [showBox3, setShowBox3] = useState(false);
        const token = localStorage.getItem('accessToken');
        const [summonerInfo, setSummonerInfo] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")));
        const [memberInfo, setMemberInfo] = useState(JSON.parse(localStorage.getItem("member")))
        let summonerNameSearch = useRef()
        let summonerTagSearch = useRef()
        const [summonerNameProfile,setSummonerNameProfile] = useState('')
        const [summonerTagProfile,setSummonerTagProfile] = useState('')
        const [title,setTitle] = useState('')
        const [content,setContent] = useState('')
        const [img,setImg] = useState('')
        useEffect(() => {
            if(summonerInfo) {
                setSummonerNameProfile(summonerInfo.summonerName)
                setSummonerTagProfile(summonerInfo.summonerTag)
            }
        }, [summonerInfo]);
        const searchVisibility = () => {
            setIsSearchVisible(!isSearchVisible);
            if (!isSearchVisible) {
                summonerNameSearch = null
                summonerTagSearch = null
                setShowBox3(false);
            }
        };
        const profileVisibility = () => {
            setIsProfileVisible(!isProfileVisible)
            if (!isProfileVisible) {
                setSummonerNameProfile(null)
                setSummonerTagProfile(null)
            }
        }
        const writeVisibility= ()=>{
            setIsWriteVisible(!isWriteVisible)
            if(!isWriteVisible){
            }
        }

        const handleSearch = async () => {
            setIsSearchLoading(true)
            const name = summonerNameSearch.current.value
            const tag = summonerTagSearch.current.value
            if (!name || !tag) {
                alert('모든 필드를 입력해주세요.');
                return;
            } else {
                try {
                    const result = await axios.post('/update', {
                        summonerName: name,
                        summonerTag: tag
                    }, {
                        headers: {"Authorization": `Bearer ${token}`},
                        withCredentials: true // 쿠키를 포함하여 요청을 보냄
                    })
                    setSearchResult(result.data)
                    localStorage.setItem("searchedSummonerInfo", JSON.stringify(result.data))
                } catch (error) {
                    alert(error.response.data);
                } finally {
                    setIsSearchLoading(false)
                }
            }
        }
        const handleProfile = async () => {
            if ( !summonerNameProfile || !summonerTagProfile) {
                alert('모든 필드를 입력해주세요.');
                return;
            } else {
                try {
                    await axios.post('/profileUpdate', {
                        id: memberInfo.id,
                        summonerName: summonerNameProfile,
                        summonerTag: summonerTagProfile
                    }, {
                        headers: {'Authorization': `Bearer ${token}`},
                        withCredentials:true
                    })
                        .then((res) => {
                            alert("변경이 완료되었습니다. 로그인 페이지로 이동합니다.")
                            navigate('/')

                        })
                } catch (error) {
                    console.log(error.response.data)
                }
            }
        }
        const handleWrite = async ()=>{
            if(!title || !content){
                alert("모든 필드를 입력해주세요.")
                return;
            }
            else{
                try{
                    await axios.post('/write',{
                        title:title,
                        content:content,
                        // img:img,
                        memberId:memberInfo.id
                    },{
                        headers: {'Authorization': `Bearer ${token}`},
                        withCredentials: true, // 쿠키를 포함하여 요청을 보냄
                    })

                    alert("작성 완료")
                    window.location.reload()
                }catch (error){
                    console.log(error.response.data)
                }
            }
        }
        const handleLogout=async ()=> {
            if (window.confirm("로그아웃 하시겠습니까?")) {
                try {
                    await axios.post('/logout',
                        {}, {
                            headers: {
                                'Authorization': `Bearer ${token}`,
                                'emailId':summonerInfo.emailId
                            }
                        })
                    localStorage.clear()
                    alert("로그아웃 되었습니다.")
                    navigate('/')
                } catch (error) {
                    alert(error.response.data)
                }
            }
        }

        useEffect(() => {
            if (searchResult) {
                setShowBox3(true);
            }
        }, [searchResult]);


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
                        <li onClick={searchVisibility}>
                            <span>소환사 검색</span>
                        </li>
                        {isSearchVisible && (
                            <div>
                                <div id="searchArea">
                                    <input ref={summonerNameSearch} type="text" id="nameSearch" placeholder="소환사명"/>
                                    <input ref={summonerTagSearch} type="text" id="tagSearch" placeholder="태그"/>
                                    <button type="submit" id="searchBtn" onClick={handleSearch} disabled={isSearchLoading}>
                                        {isSearchLoading?<BeatLoader size={10} color={"#123abc"} loading={isSearchLoading} />:'검색'}
                                    </button>
                                </div>
                                {showBox3 && (
                                    <Box3 summerInfo={searchResult} type="search" label="searchResult"/>
                                )}
                            </div>
                        )}
                        <li onClick={profileVisibility}>소환사 변경</li>
                        {isProfileVisible && (
                            <div>
                                <div id="searchArea">
                                <input type="text" placeholder="소환사명" onChange={(e) => setSummonerNameProfile(e.target.value)}/>
                                <input type="text"  placeholder="태그" onChange={(e) => setSummonerTagProfile(e.target.value)}/>
                                <button type="submit" id="updateBtn" onClick={handleProfile}>확인</button>
                                </div>
                            </div>
                        )}
                        <li onClick={writeVisibility}>글쓰기</li>
                        {isWriteVisible &&(
                            <div>
                                <div id="writeArea">
                                    <input type="text" placeholder="제목" onChange={(e)=>setTitle(e.target.value)}/>
                                    <textarea onChange={(e)=>setContent(e.target.value)} />
                                    {/*<input type="file" onChange={(e)=>setImg(e.target.files[0])}/>*/}
                                    <button type="submit" id="writeBtn" onClick={handleWrite}>작성</button>
                                </div>
                            </div>
                        )
                        }
                        <li onClick={handleLogout}>로그아웃</li>
                    </ul>
                </div>
            </div>
    )
}
export default Box1;