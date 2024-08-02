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
        } else {
            try {
                const formData = new FormData();
                formData.append(
                    "postDto",
                    new Blob(
                        [
                            JSON.stringify({
                                title: title.trim(),
                                content: content.trim(),
                                memberId: memberInfo.id,
                                memberName: memberInfo.username,
                            }),
                        ],
                        {
                            type: "application/json",
                        }
                    )
                );
                formData.append("image", img);
                await axios
                    .post("/post/write/post", formData, {
                        headers: { Authorization: `Bearer ${token}` },
                        withCredentials: true,
                    })
                    .then((response) => {
                        if (response.headers.access) {
                            localStorage.setItem("accessToken", response.headers.access);
                            setToken(response.headers.access);
                        }
                    });

                alert("작성 완료");
                window.location.reload();
            } catch (error) {
                const errorMessage =
                    error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            } finally {
                setImg(null);
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
                        <input type="file" onChange={(e) => setImg(e.target.files[0])} />
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
