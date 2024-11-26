import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import Box1 from "../box1/Box1"
import Box3 from "../box3/Box3";
import "../../css/Main.css";
import { useNavigate } from "react-router-dom";
import addImg from "../../images/더보기.png";
import PostList from "./PostListComponent";

const Main = () => {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [isLast, setIsLast] = useState(true);
    const [postList, setPostList] = useState([]);
    const [token, setToken] = useState(localStorage.getItem("accessToken"));
    const [member] = useState(JSON.parse(localStorage.getItem("member")));

    const fetchData = useCallback(
        async (page) => {
            try {
                const res = await axios.get("/post/postList", {
                    params: { page: page },
                    headers: { Authorization: `Bearer ${token}` },
                    withCredentials: true,
                });
                if (res.headers.access) {
                    localStorage.setItem("accessToken", res.headers.access);
                    setToken(res.headers.access);
                }
                const newPosts = res.data.content.map((post) => ({
                    ...post,
                    comments: [],
                    newComment: "",
                    postReviewBtnColor: "rgb(199, 235, 245)",
                }));
                
                setPostList((prevState) => [...prevState, ...newPosts]);
                setIsLast(res.data.last);
            } catch (error) {
                const errorMessage = error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            }
        },
        [token, navigate]
    );

    useEffect(() => {
        fetchData(0);
    }, [fetchData]);

    const addPost = () => {
        fetchData(page + 1);
        setPage((prevPage) => prevPage + 1);
    };

    return (
        <div className="container">
            <div className="box1">
                <Box1/>
            </div>
            <div className="box2">
                <PostList
                    postList={postList}
                    setPostList={setPostList}
                    token={token}
                    setToken={setToken}
                    member={member}
                    navigate={navigate}
                />
                {!isLast && <LoadMoreButton onClick={addPost} />}
            </div>
            <div className="box3">
                <Box3 type="main" />
            </div>
        </div>
    );
};

const LoadMoreButton = ({ onClick }) => (
    <button id="addPostBtn" onClick={onClick}>
        <img src={addImg} alt="Add more posts" />
    </button>
);

export default Main;
