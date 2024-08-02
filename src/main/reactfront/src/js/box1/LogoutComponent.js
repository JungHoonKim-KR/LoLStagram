import React from "react";
import axios from "axios";

const LogoutComponent = ({ token, summonerInfo, navigate }) => {

    const handleLogout = async () => {
        if (window.confirm("로그아웃 하시겠습니까?")) {
            try {
                await axios.post(
                    "/logout",
                    {},
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            emailId: summonerInfo.emailId,
                        },
                    }
                );
                localStorage.clear();
                alert("로그아웃 되었습니다.");
                navigate("/");
            } catch (error) {
                const errorMessage = error.response?.data?.errorMessage || "알 수 없는 오류 발생";
                alert(errorMessage);
                if (errorMessage === "토큰 만료") {
                    localStorage.clear();
                    navigate("/");
                }
            }
        }
    };

    return <li onClick={handleLogout}>로그아웃</li>;
};

export default LogoutComponent;
