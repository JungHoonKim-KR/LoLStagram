import '../css/App.css';
import {HashRouter  as Router, Route, Routes} from 'react-router-dom';
import Login from "./Login"
import Main from "./Main"
import Join from "./Join"
import Oauth from "./Oauth";
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
