import React, { useState } from "react";
import axios from "axios";

const WriteComponent = ({ token, setToken, memberInfo, navigate }) => {
    const [isWriteVisible, setIsWriteVisible] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [img, setImg] = useState(null);

    const writeVisibility = () => {
        setIsWriteVisible(!isWriteVisible);
    };
    const handleWrite = async () => {
        if (!title || !content || !title.trim() || !content.trim()) {
            alert("모든 필드를 입력해주세요.");
            return;
        }
    
        try {
            const response = await axios.post(
                "/post/write/post",
                {
                    title: title.trim(),
                    content: content.trim(),
                    memberId: memberInfo.id,
                    memberName: memberInfo.username,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                    withCredentials: true,
                }
            );
    
            // 액세스 토큰 갱신 처리
            const newAccessToken = response.headers.access;
            if (newAccessToken) {
                localStorage.setItem("accessToken", newAccessToken);
                setToken(newAccessToken);
            }
    
            alert("작성 완료");
            window.location.reload();
        } catch (error) {
            // 오류 메시지 처리
            const errorMessage = error.response?.data?.errorMessage || "알 수 없는 오류 발생";
            alert(errorMessage);
    
            // 토큰 만료 시 처리
            if (errorMessage === "토큰 만료") {
                localStorage.clear();
                navigate("/");
            }
        }
    };

    return (
        <li>
            <span onClick={writeVisibility}>글쓰기</span>
            {isWriteVisible && (
                <div>
                    <div id="writeArea">
                        <input type="text" placeholder="제목" onChange={(e) => setTitle(e.target.value)} />
                        <textarea onChange={(e) => setContent(e.target.value)} />
                        <button type="submit" id="writeBtn" onClick={handleWrite}>
                            작성
                        </button>
                    </div>
                </div>
            )}
        </li>
    );
};

export default WriteComponent;
