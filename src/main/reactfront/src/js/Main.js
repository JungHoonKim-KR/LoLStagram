import React, { useState, useRef, useEffect, useCallback } from 'react';
import axios from 'axios';
import Box1 from './Box1';
import Box3 from './Box3';
import '../css/Main.css';
import { useNavigate } from 'react-router-dom';
import defaultImg from '../images/tier/CHALLENGER.png';
import addImg from '../images/더보기.png';

const Main = () => {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [isLast, setIsLast] = useState(true);
    const [postList, setPostList] = useState([]);
    const [token, setToken] = useState(localStorage.getItem('accessToken'));
    const [member] = useState(JSON.parse(localStorage.getItem('member')));
    const [activePostIndex, setActivePostIndex] = useState(null);
    const containerRef = useRef(null);

    // Use useCallback to memorize fetchData function
    const fetchData = useCallback(async (page) => {
        try {
            const res = await axios.get("/post/postList", {
                params: { page: page },
                headers: { 'Authorization': `Bearer ${token}` },
                withCredentials: true,
            });
            if (res.headers.access) {
                localStorage.setItem('accessToken', res.headers.access);
                setToken(res.headers.access);
            }

            const newPosts = res.data.postDtoList.map(post => ({
                ...post,
                comments: [],
                newComment: '',
                postReviewBtnColor: "rgb(199, 235, 245)",
            }));

            setPostList(prevState => [...prevState, ...newPosts]);
            setIsLast(res.data.isLast);
        } catch (error) {
            alert(error.response.data.errorMessage)
            navigate("/")
        }
    }, [token, navigate]);

    useEffect(() => {
        fetchData(0);
    }, [token, fetchData]);

    const handleInputChange = (e, index) => {
        const newPostList = [...postList];
        newPostList[index].newComment = e.target.value;
        newPostList[index].postReviewBtnColor = e.target.value.length > 0 ? "rgb(11, 159, 228)" : "rgb(199, 235, 245)";
        setPostList(newPostList);
    };
    const handleInputClick = (postIndex) => {
        setActivePostIndex(postIndex);
    };

    const handleClickOutside = (event) => {
        if (containerRef.current && !containerRef.current.contains(event.target)) {
            setActivePostIndex(null);
        }
    };
    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const uploadReview = async (index) => {
        const newPostList = [...postList];
        if (newPostList[index].newComment.length > 0) {
            try {
                const res = await axios.post('/post/write/comment', {
                    postId: newPostList[index].postId,
                    writeId: member.id,
                    writerName: member.username,
                    comment: newPostList[index].newComment
                }, {
                    headers: { "Authorization": `Bearer ${token}` },
                    withCredentials: true
                });
                if (res.headers.access) {
                    localStorage.setItem('accessToken', res.headers.access);
                    setToken(res.headers.access)
                }
                newPostList[index].comments.push({
                    id: newPostList[index].comments.length,
                    author: member.username,
                    text: newPostList[index].newComment,
                });
                newPostList[index].newComment = '';
                newPostList[index].postReviewBtnColor = "rgb(199, 235, 245)";
                setPostList(newPostList);

            } catch (error) {
                alert(error.response.data.errorMessage);
                navigate("/");
            }
        } else {
            alert('댓글을 입력해 주세요');
        }
    };

    const removeComm = (postIndex, commentIndex) => {
        const newPostList = [...postList];
        newPostList[postIndex].comments = newPostList[postIndex].comments.filter((_, idx) => idx !== commentIndex);
        setPostList(newPostList);
    };

    const addPost = async () => {
        try {
            fetchData(page + 1);
            setPage(page + 1);
        } catch (error) {
            if (error.response.data.errorCode === "TOKEN_EXPIRED") {
                alert("토큰 만료. 로그인 화면으로 이동합니다.");
                navigate("/");
            } else {
                alert(error.response.data.errorMessage);
            }
        }
    };

    return (
        <div className="container">
            <div className="box1">
                <Box1 />
            </div>
            <div className="box2">
                {postList.map((post, postIndex) => (
                    <div className="feed" key={postIndex}>
                        <header className="feed__header">
                            <div className="feed__user-info">
                                <img src={member.profileImg || defaultImg} alt="Profile" className="feed__user-img" />
                                <div className="feed__user-details">
                                    <div className="feed__user-name">{post.memberName}</div>
                                    <div className="feed__user-location">Seoul, South Korea</div>
                                </div>
                            </div>
                            <div className="feed__more-icon"></div>
                        </header>
                        <div className="feed__contents">
                            <p>{post.title}</p>
                            {post.frontImage ? (
                                <img src={post.frontImage} alt="Front of the post" />
                            ) : (
                                <span></span>
                            )}
                            <p>{post.content}</p>
                        </div>
                        <div className="feed__comment-field">
                            <input type="text" placeholder="댓글달기..." className="feed__comment-input"
                                   onChange={(e) => handleInputChange(e, postIndex)} value={post.newComment}
                                   onClick={() => handleInputClick(postIndex)}
                            />
                            <div className="feed__comment-submit" onClick={() => uploadReview(postIndex)}
                                 style={{ color: post.postReviewBtnColor }}>게시
                            </div>
                        </div>
                        {activePostIndex === postIndex && (
                            <div className="comment-list">
                                {post.commentList.map((comment, index) => (
                                    <div key={index} className="comment">
                                        <p>{comment.writerName}</p> &nbsp;
                                        <p>{comment.comment}</p>
                                    </div>
                                ))}
                            </div>
                        )}
                        <ul>
                            {post.comments.map((review, commentIndex) => (
                                <li key={commentIndex} className="comment-text">
                                    <strong>{review.author} </strong>{review.text}
                                    <button onClick={() => removeComm(postIndex, commentIndex)}>삭제</button>
                                </li>
                            ))}
                        </ul>
                    </div>
                ))}
                {!isLast && (
                    <button id="addPostBtn" onClick={addPost}>
                        <img src={addImg} alt="Add more posts" />
                    </button>
                )}
            </div>
            <div className="box3">
                <Box3 type="main" />
            </div>
        </div>
    );
}

export default Main;
