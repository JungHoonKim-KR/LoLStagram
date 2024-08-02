import React, {useRef, useState,useEffect} from "react";
import axios from "axios";
import {BeatLoader} from "react-spinners";

const SearchComponent = ({
                             token,
                             setToken,
                             setSearchResult,
                             isSearchLoading,
                             setIsSearchLoading,
                             onSearchVisibility,
                             navigate
                         }) => {
    const [isSearchVisible, setIsSearchVisible] = useState(false);
    let summonerNameSearch = useRef();
    let summonerTagSearch = useRef();

    const searchVisibility = () => {
        setIsSearchVisible((prevState) => !prevState);
    };

    useEffect(()=>{
        if(!isSearchVisible){
            onSearchVisibility();
        }
    },[isSearchVisible,onSearchVisibility])

    const handleSearch = async () => {

        const name = summonerNameSearch.current.value.trim();
        const tag = summonerTagSearch.current.value.trim();
        if (!name || !tag) {
            alert("모든 필드를 입력해주세요.");
        } else {
            try {
                setIsSearchLoading(true);
                const result = await axios.post(
                    "/summoner/search",
                    {
                        summonerName: name,
                        summonerTag: tag,
                    },
                    {
                        headers: {Authorization: `Bearer ${token}`},
                        withCredentials: true,
                    }
                );
                if (result.headers.access) {
                    localStorage.setItem("accessToken", result.headers.access);
                    setToken(result.headers.access);
                }
                setSearchResult(result.data);
                localStorage.setItem("searchedSummonerInfo", JSON.stringify(result.data));
            } catch (error) {
                const errorMessage =
                    error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            } finally {
                setIsSearchLoading(false);
            }
        }
    };

    return (
        <li>
            <span onClick={searchVisibility}>소환사 검색</span>
            {isSearchVisible && (
                <div>
                    <div id="searchArea">
                        <input
                            ref={summonerNameSearch}
                            type="text"
                            id="nameSearch"
                            placeholder="소환사명"
                        />
                        <input
                            ref={summonerTagSearch}
                            type="text"
                            id="tagSearch"
                            placeholder="태그"
                        />
                        <button
                            type="submit"
                            id="searchBtn"
                            onClick={handleSearch}
                            disabled={isSearchLoading}
                        >
                            {isSearchLoading ? (
                                <BeatLoader size={10} color={"#123abc"} loading={isSearchLoading}/>
                            ) : (
                                "검색"
                            )}
                        </button>
                    </div>
                </div>
            )}
        </li>
    );
};

export default SearchComponent;
