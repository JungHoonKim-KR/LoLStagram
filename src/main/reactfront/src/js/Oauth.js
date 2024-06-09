import axios from "axios";
import { useEffect } from 'react';
import {useLocation, useNavigate} from 'react-router-dom';

function Oauth() {
    const location = useLocation();
    const urlParams = new URLSearchParams(location.search);
    const authenticationCode = urlParams.get('authenticationCode');
    const navigate = useNavigate(); // Hook for navigation

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.post("/auth/oauthLogin", {
                    authenticationCode: authenticationCode
                },{
                    withCredentials: true
                });
                localStorage.setItem('accessToken', response.data.accessToken);
                localStorage.setItem('userName', response.data.username);
                localStorage.setItem('mySummonerInfo', JSON.stringify(response.data.summonerInfoDto));
                localStorage.setItem("member", JSON.stringify(response.data.memberDto));
                navigate('/main', {state: {response: response.data}});
            } catch (error) {
                console.log(error.response.data);
            }
        };
        fetchData();
    }, [authenticationCode, navigate]); // Added navigate to the dependency array

    return null;
}

export default Oauth;
