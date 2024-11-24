import axios from "axios";
import {useState} from "react";
import {useNavigate} from 'react-router-dom';
import "../../css/Login.css"
import google_btn from "../../images/btn_login_google.png"
//서버로 인증을 요청할 uri (서버의 webSecurityConfig의 base uri와 일치해야 한다)
export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

//서버에서 인증을 완료한 후에 프론트엔드로 돌아올 redirect uri (app.oauth2.authorized-redirect-uri와 일치해야 한다)
export const OAUTH2_REDIRECT_URI = process.env.REACT_APP_OAUTH2_REDIRECT_URI;
export const GOOGLE_AUTH_URL = API_BASE_URL + '/oauth2/authorization/google?redirect_uri=' + OAUTH2_REDIRECT_URI;
export const FACEBOOK_AUTH_URL = API_BASE_URL + '/oauth2/authorization/facebook?redirect_uri=' + OAUTH2_REDIRECT_URI;
export const NAVER_AUTH_URL = API_BASE_URL + '/oauth2/authorization/naver?redirect_uri=' + OAUTH2_REDIRECT_URI;
export const KAKAO_AUTH_URL = API_BASE_URL + '/oauth2/authorization/kakao?redirect_uri=' + OAUTH2_REDIRECT_URI;

function Login() {
    const [emailId, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate(); // 추가
    const joinHandler = ()=>{
        navigate('/join')
    }
    const loginHandler = async (e) => {
        e.preventDefault()
        try {
            const response = await axios.post('/login/normal', {
                emailId: emailId,
                password: password,
                type:"ALL",
                page:0
            },
                );

            localStorage.setItem('accessToken', response.data.accessToken);
            localStorage.setItem('userName',response.data.username)
            localStorage.setItem('mySummonerInfo', JSON.stringify(response.data.summonerInfoDto));
            localStorage.setItem("member", JSON.stringify(response.data.memberDto))


            // 로그인 성공 후, main 페이지로 이동하면서 response를 전달
            navigate('/main', {state:{response:response.data}})

        } catch (error) {

            const errorMessage = error.response.data.errorMessage;
            alert(errorMessage)
        }
    }
    return (
        <div className={'login'}>
            <h1>Login</h1>
            <form className={'form'}>
                <input
                    type="text"
                    className="input"
                    value={emailId}
                    onChange={e => setEmail(e.target.value)}
                    placeholder="이메일"
                />
                <input
                    type="password"
                    className="input"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder="비밀번호"
                />
                <button type="submit" className="button" onClick={loginHandler}>로그인</button>
            </form>
            <button type="button" onClick={joinHandler}>회원가입</button>
            <a href={GOOGLE_AUTH_URL} className="btn btn-sm btn-success active" role="button">
                <button className="google-btn">
                    <img src={google_btn} alt="Google logo" target="_self" className="google-logo" />
                </button>
            </a>

        </div>
    )
}

export default Login;