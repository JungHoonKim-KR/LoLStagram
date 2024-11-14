import '../css/App.css';
import {HashRouter  as Router, Route, Routes} from 'react-router-dom';
import Login from "./auth/Login"
import Main from "./main/Main"
import Join from "./auth/Join"
import Oauth from "./auth/Oauth";
function App() {


    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login/>}></Route>
                <Route path="/join" element={<Join/>}></Route>
                <Route path="/main" element={<Main/>}></Route>
                <Route path="/oauth/callback" element={<Oauth/>}></Route>
            </Routes>
        </Router>
    );
}
export default App;
