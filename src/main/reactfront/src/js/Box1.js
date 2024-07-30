import React, {useEffect, useRef, useState} from "react";
import "../css/Box1.css"
import axios from "axios";
import Box3 from "./Box3";
import {BeatLoader} from 'react-spinners';
import {useNavigate} from 'react-router-dom';

const Box1 = () => {
    const navigate = useNavigate();
    const [searchResult, setSearchResult] = useState(null);
    const [isSearchVisible, setIsSearchVisible] = useState(false);
    const [isProfileVisible, setIsProfileVisible] = useState(false);
    const [isSearchLoading, setIsSearchLoading] = useState(false)
    const [isWriteVisible, setIsWriteVisible] = useState(false)
    const [showBox3, setShowBox3] = useState(false);
    const [token, setToken] = useState(localStorage.getItem('accessToken'));
    const [summonerInfo] = useState(JSON.parse(localStorage.getItem("mySummonerInfo")));
    const [memberInfo] = useState(JSON.parse(localStorage.getItem("member")))
    let summonerNameSearch = useRef()
    let summonerTagSearch = useRef()
    const [summonerNameProfile, setSummonerNameProfile] = useState(null)
    const [summonerTagProfile, setSummonerTagProfile] = useState(null)
    const [title, setTitle] = useState(null)
    const [content, setContent] = useState(null)
    const [img, setImg] = useState(null)
    useEffect(() => {
        if (summonerInfo) {
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
    const writeVisibility = () => {
        setIsWriteVisible(!isWriteVisible)
        if (!isWriteVisible) {
        }
    }

    const handleSearch = async () => {
        const name = summonerNameSearch.current.value.trim()
        const tag = summonerTagSearch.current.value.trim()
        if (!name || !tag) {
            alert('모든 필드를 입력해주세요.');
        } else {
            try {
                setIsSearchLoading(true)
                const result = await axios.post('/summoner/search', {
                    summonerName: name,
                    summonerTag: tag,
                }, {
                    headers: {"Authorization": `Bearer ${token}`},
                    withCredentials: true
                })
                if (result.headers.access) {
                    localStorage.setItem('accessToken', result.headers.access);
                    setToken(result.headers.access)
                }
                setSearchResult(result.data)
                localStorage.setItem("searchedSummonerInfo", JSON.stringify(result.data))
            } catch (error) {
                alert(error.response.data.errorMessage)

            } finally {
                setIsSearchLoading(false)
            }
        }
    }

    const handleProfile = async () => {
        if (!summonerNameProfile.trim() || !summonerTagProfile.trim()) {
            alert('모든 필드를 입력해주세요.');
            return;
        } else {
            try {
                setIsSearchLoading(true)
                await axios.put('/profile/update',
                    {
                        id: memberInfo.id,
                        summonerName: summonerNameProfile.trim(),
                        summonerTag: summonerTagProfile.trim()
                    }, {
                        headers: {'Authorization': `Bearer ${token}`},
                        withCredentials: true
                    })
                    .then((res) => {
                        alert("변경이 완료되었습니다. 로그인 페이지로 이동합니다.")
                        navigate("/")
                    })
            } catch (error) {
                alert(error.response.data.errorMessage)
                navigate("/")
            }
        }
    }

    const handleWrite = async () => {
        if (!title.trim() || !content.trim()) {
            alert("모든 필드를 입력해주세요.")
            return;
        } else {
            try {
                setIsSearchLoading(true)
                const formData = new FormData()
                formData.append("postDto", new Blob([JSON.stringify({
                    title: title.trim(),
                    content: content.trim(),
                    memberId: memberInfo.id,
                    memberName: memberInfo.username
                })], {
                    type: 'application/json'
                }))
                formData.append("image", img)
                await axios.post('/post/write',
                    formData,
                    {
                        headers: {'Authorization': `Bearer ${token}`},
                        withCredentials: true,
                    }).then(response => {
                        if (response.headers.access) {
                            localStorage.setItem('accessToken', response.headers.access);
                            setToken(response.headers.access)
                        }
                    }
                )

                alert("작성 완료")
                window.location.reload()
            } catch (error) {
                alert(error.response.data.errorMessage)
                navigate("/")
            } finally {
                setImg(null)
            }
        }
    }

    const handleLogout = async () => {
        if (window.confirm("로그아웃 하시겠습니까?")) {
            try {
                await axios.post('/logout',
                    {}, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'emailId': summonerInfo.emailId
                        }
                    })
                localStorage.clear()
                alert("로그아웃 되었습니다.")
                navigate("/")
            } catch (error) {
                alert(error.response.data.errorMessage)
                navigate("/")
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
                                    {isSearchLoading ?
                                        <BeatLoader size={10} color={"#123abc"} loading={isSearchLoading}/> : '검색'}
                                </button>
                            </div>
                            {showBox3 && (
                                <Box3 key={searchResult.leagueId} summerInfo={searchResult} type="search"
                                      label="searchResult"/>
                            )}
                        </div>
                    )}
                    <li onClick={profileVisibility}>소환사 변경</li>
                    {isProfileVisible && (
                        <div>
                            <div id="searchArea">
                                <input type="text" placeholder="소환사명"
                                       onChange={(e) => setSummonerNameProfile(e.target.value)}/>
                                <input type="text" placeholder="태그"
                                       onChange={(e) => setSummonerTagProfile(e.target.value)}/>
                                <button type="submit" id="updateBtn" onClick={handleProfile}>확인</button>
                            </div>
                        </div>
                    )}
                    <li onClick={writeVisibility}>글쓰기</li>
                    {isWriteVisible && (
                        <div>
                            <div id="writeArea">
                                <input type="text" placeholder="제목" onChange={(e) => setTitle(e.target.value)}/>
                                <textarea onChange={(e) => setContent(e.target.value)}/>
                                <input type="file" onChange={(e) => setImg(e.target.files[0])}/>
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