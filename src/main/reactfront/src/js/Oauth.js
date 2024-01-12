import React, { useEffect } from 'react';
import axios from "axios";
import {useNavigate} from 'react-router-dom';
function Oauth(){
    const urlParams = new URLSearchParams(window.location.search);
    const authenticationCode = urlParams.get('authenticationCode');
    const navigate = useNavigate(); // 추가
    useEffect( ()=> {
        const fetchData =async ()=>{

        try {
            const response = await axios.post("/oauthLogin", {
                authenticationCode: authenticationCode
            })
            const accessToken = response.headers['access']
            localStorage.setItem('accessToken', accessToken);
            navigate('/main',{state:{response:response.data}})
        } catch (error) {
            console.log(error)
        }

    }
        fetchData()


    },[])

}

export default Oauth;