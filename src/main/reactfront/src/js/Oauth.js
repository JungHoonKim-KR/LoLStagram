import axios from "axios";
import React, { useEffect } from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
function Oauth(){
    const location = useLocation();
    const urlParams = new URLSearchParams(location.search)
    const authenticationCode = urlParams.get('authenticationCode');
    const navigate = useNavigate(); // 추가
    useEffect( ()=> {
        const fetchData =async ()=>{

        try {
            const response = await axios.post("/auth/oauthLogin", {
                authenticationCode: authenticationCode
            })
            localStorage.setItem('accessToken', response.data.accessToken);
            localStorage.setItem('userName',response.data.username)
            localStorage.setItem('mySummonerInfo', JSON.stringify(response.data.summonerInfoDto));
            localStorage.setItem("member", JSON.stringify(response.data.memberDto))
            navigate('/main',{state:{response:response.data}})
        } catch (error) {
            console.log(error.response.data)
        }

    }
        fetchData()


    },[])

}

export default Oauth;