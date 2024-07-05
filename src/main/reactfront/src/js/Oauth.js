import axios from "axios";
import { useEffect } from 'react';
import { useNavigate} from 'react-router-dom';
import {useCookies} from 'react-cookie'
function Oauth() {
    const [cookie, getCookie] = useCookies(['accessToken']);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.post("/auth/oauthLogin", {
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
    }, [ navigate]); // Added navigate to the dependency array

    return null;
}

export default Oauth;
