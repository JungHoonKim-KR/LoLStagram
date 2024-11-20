import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import CommentSection from "./CommentSectionComponent";

const PostItem = ({ post, postIndex, postList, setPostList, token, setToken, member, navigate }) => {
    const [activePostIndex, setActivePostIndex] = useState(null);
    const containerRef = useRef(null);
    const youtubeRegex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([\w-]{11})(?:[&?].*)?/g;

    // 유튜브 링크를 iframe으로 변환
    const replaceYoutubeLinks = (content) => {
        return content.replace(youtubeRegex, (match, videoId) => {
            return `<iframe width="460" height="280" src="https://www.youtube.com/embed/${videoId}" frameborder="0" allowfullscreen></iframe>`;
        });
    };

    // post.content에서 유튜브 링크를 변환된 iframe 포함한 HTML로 교체
    const formattedContent = replaceYoutubeLinks(post.content);

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
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const uploadReview = async (index) => {
        const newPostList = [...postList];
        if (newPostList[index].newComment.length > 0) {
            try {
                const res = await axios.post(
                    "/post/write/comment",
                    {
                        postId: newPostList[index].postId,
                        writeId: member.id,
                        writerName: member.username,
                        comment: newPostList[index].newComment,
                    },
                    {
                        headers: { Authorization: `Bearer ${token}` },
                        withCredentials: true,
                    }
                );
                if (res.headers.access) {
                    localStorage.setItem("accessToken", res.headers.access);
                    setToken(res.headers.access);
                }
                newPostList[index].comments.push({
                    id: newPostList[index].comments.length,
                    author: member.username,
                    text: newPostList[index].newComment,
                });
                newPostList[index].newComment = "";
                newPostList[index].postReviewBtnColor = "rgb(199, 235, 245)";
                setPostList(newPostList);
            } catch (error) {
                const errorMessage = error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            }
        } else {
            alert("댓글을 입력해 주세요");
        }

    };


    return (
        <div className="feed" ref={containerRef}>
            <header className="feed__header">
                <div className="feed__user-info">
                    <div className="feed__user-details">
                        <div className="feed__user-name">{post.memberName}</div>
                        <div className="feed__user-location">Seoul, South Korea</div>
                    </div>
                </div>
                <div className="feed__more-icon"></div>
            </header>
            <div className="feed__contents">
                <p>{post.title}</p>
                <p style={{ whiteSpace: 'pre-line' }} dangerouslySetInnerHTML={{ __html: formattedContent }} />
    
            </div>
            <CommentSection
                postIndex={postIndex}
                post={post}
                handleInputChange={handleInputChange}
                handleInputClick={handleInputClick}
                uploadReview={uploadReview}
                activePostIndex={activePostIndex}
            />
        </div>
    );
};

export default PostItem;
