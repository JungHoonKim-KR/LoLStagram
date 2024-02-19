import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate} from 'react-router-dom';
import "../css/Join.css"

function Join(){
    const urlParams = new URLSearchParams(window.location.search);
    const [emailId, setEmailId] = useState('');
    const [password, setPassword] = useState('');
    const [username, setUsername] = useState('');
    const [summonerName,setSummonerName] = useState('');
    const [summonerTag,setSummonerTag] = useState('');
    const navigate = useNavigate(); // 추가

    useEffect(()=> {
        if (urlParams.has('emailId') && urlParams.has('username')) {
            setEmailId(urlParams.get('emailId'))
            setUsername(urlParams.get('username'))
        }
    },[])

    const joinHandler  = async(e)=>{
        e.preventDefault();
        try {
            await axios.post("/join",{
                emailId: emailId,
                password: password,
                username:username,
                summonerName:summonerName,
                summonerTag:summonerTag
            }).then((res)=>{
                alert("회원가입이 완료되었습니다.")
                navigate('/')
                }
            )

        }catch (error){
            alert(error.response.data)
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
                <button type="submit">회원가입</button>
            </form>
        </div>
    )
}
export default Join;