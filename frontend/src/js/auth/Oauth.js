import axios from "axios";
import { useEffect } from 'react';
import { useNavigate} from 'react-router-dom';
function Oauth() {
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.post("/login/oauthLogin", {
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
    }, [ navigate]);

    return null;
}
export default Oauth;
