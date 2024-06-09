import React, {useEffect, useState,useMemo} from "react";
import axios from "axios";
import {useNavigate,useLocation} from 'react-router-dom';
import "../css/Join.css"

function Join(){
    const location = useLocation();
    const urlParams = useMemo(() => new URLSearchParams(location.search), [location.search]);
    const [emailId, setEmailId] = useState('');
    const [password, setPassword] = useState('');
    const [username, setUsername] = useState('');
    const [summonerName,setSummonerName] = useState('');
    const [summonerTag,setSummonerTag] = useState('');
    const [img, setImg] = useState('')
    const navigate = useNavigate(); // 추가

    useEffect(()=> {

        if (urlParams.has('emailId') && urlParams.has('username')) {
            setEmailId(urlParams.get('emailId'))
            setUsername(urlParams.get('username'))
        }
    },[urlParams])

    const joinHandler  = async(e)=>{
        e.preventDefault();
        try {
            const formData = new FormData();
            formData.append("joinDto", new Blob([JSON.stringify({
                emailId: emailId.trim(),
                password: password.trim(),
                username: username.trim(),
                summonerName: summonerName.trim(),
                summonerTag: summonerTag.trim()
            })], {
                type: "application/json"
            }));
            formData.append("image", img);  // file 객체 추가
            await axios.post("/auth/join",
                formData,
                {

                }
            ).then((res)=>{
                alert("회원가입이 완료되었습니다.")
                navigate("/")
                }
            )

        }catch (error){
            alert(error.response.data.errorMessage)
        }
    }
    return(
        <div className="form-container">
            <form onSubmit={joinHandler}>
                <label>
                    이메일:
                    <input type="text" value={emailId} onChange={e => setEmailId(e.target.value)} required />
                </label>
                <label>
                    비밀번호:
                    <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                </label>
                <label>
                    사용자 이름:
                    <input type="text" value={username} onChange={e => setUsername(e.target.value)} required />
                </label>
                <label>
                    라이엇 이름:
                    <input type="text" value={summonerName} onChange={e => setSummonerName(e.target.value)} required />
                </label>
                <label>
                    라이엇 태그:
                    <input type="text" value={summonerTag} onChange={e => setSummonerTag(e.target.value)} required />
                </label>
                <label>
                    프로필 사진:
                    <input type="file" onChange={(e)=>setImg(e.target.files[0])}/>
                </label>



                <button type="submit">회원가입</button>
            </form>
        </div>
    )
}
export default Join;