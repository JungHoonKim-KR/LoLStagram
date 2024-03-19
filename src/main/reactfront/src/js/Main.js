import React, {useState, useEffect} from 'react';
import axios from 'axios';
import Box1 from './Box1';
import Box3 from './Box3'
import '../css/Main.css'; // 이 부분은 css 파일 경로에 따라 변경해주세요.
import { useNavigate } from 'react-router-dom';
import defaultImg from '../images/tierImage/CHALLENGER.png'
import addImg from '../images/더보기.png'
const Main = () => {
    const navigate = useNavigate();
    const[page,setPage]=useState(0)
    const [isLast,setIsLast]=useState(true)
    const [count, setCount] = useState(0);
    const [addReview, setAddReview] = useState(null);
    const [newReview, setNewReview] = useState([]);
    const [commenter] = useState(['uuu', 'pdds', 'hooe', 'ddwwe']);
    const [postReviewBtnColor, setPostReviewBtnColor] = useState("rgb(199, 235, 245)");
    const [postList, setPostList] = useState([])
    const [token,setToken] = useState(localStorage.getItem('accessToken'));
    const [member,setMember] = useState(JSON.parse(localStorage.getItem('member')))
    const fetchData = async (page) => {
        try {
            const res = await axios.get("/postList",{
                params:{
                    page:page
                }
            }, {
                headers: {'Authorization': `Bearer ${token}`},
                withCredentials: true, // 쿠키를 포함하여 요청을 보냄
            });
            if(res.headers.access){
                localStorage.setItem('accessToken', res.headers.access);
                setToken(res.headers.access)
            }

            setPostList(prevState => [...prevState,...res.data.postDtoList])
            setIsLast(res.data.isLast)
        } catch (error) {
            if(error.response.data.errorCode == "TOKEN_EXPIRED"){
                alert("토큰 만료. 로그인 화면으로 이동합니다.")
                navigate("/")
            }
            else{
                alert(error.response.data.errorMessage)
            }
        }
    };

    useEffect(() => {
        fetchData(0);
    }, [token]);
    const countPlus = () => {
        setCount(prevCount => prevCount + 1);
    }

    const uploadReview = () => {
        if (addReview.length > 0) {
            const commentervalue = Math.floor(Math.random() * commenter.length);
            const commenterPick = commenter[commentervalue];
            setNewReview(prevReviews => [...prevReviews, {
                id: prevReviews.length,
                text: commenterPick + " " + addReview,
                liked: false
            }]);
            setAddReview('');
            setPostReviewBtnColor("rgb(199, 235, 245)");
        } else {
            alert('댓글을 입력해 주세요');
        }
    }
    const removeComm = (id) => {
        setNewReview(prevReviews => prevReviews.filter(review => review.id !== id));
    }
    const pushHeart = (id) => {
        setNewReview(prevReviews => prevReviews.map((review, index) => index === id ? {
            ...review,
            liked: !review.liked
        } : review));
    }
    const handleInputChange = (e) => {
        setAddReview(e.target.value);
        setPostReviewBtnColor(e.target.value.length > 0 ? "rgb(11, 159, 228)" : "rgb(199, 235, 245)");
    }

    const addPost = async ()=>{
        try{
            fetchData(page+1)
            setPage(page+1)
        }
        catch (error){
            if(error.response.data.errorCode == "TOKEN_EXPIRED"){
                alert("토큰 만료. 로그인 화면으로 이동합니다.")
                navigate("/")
            }
            else{
                alert(error.response.data.errorMessage)
            }
        }
    }
    return (
        <div className="container">
            <div className="box1">
                <Box1></Box1>
            </div>
            <div className="box2">
                {postList.map((post,index)=>(
                <div className="feed">
                    <header className="feed__header">
                        <div className="feed__user-info">
                            <img src={member.profileImg || defaultImg} alt="프로필 이미지" className="feed__user-img"/>
                            <div className="feed__user-details">
                                <div className="feed__user-name">{post.memberName}</div>
                                <div className="feed__user-location">Seoul, South Korea</div>
                            </div>
                        </div>
                        <div className="feed__more-icon"></div>
                    </header>
                    <div className="feed__contents">
                        <img src={post.frontImage}></img>
                        <p>{post.content}</p>
                    </div>
                    <div className="feed__icons">
                        <div className="feed__icons-left">
                            <button onClick={countPlus} className="feed__icon-heart">좋아요</button>
                            <div className="feed__icon-comment"></div>
                        </div>
                        <div className="feed__icon-bookmark"></div>
                    </div>
                    <div className="feed__likes">
                        좋아요 <span className="feed__likes-count">{count}</span> 개
                    </div>
                    <div className="feed__comment-field">
                        <input type="text" placeholder="댓글달기..." className="feed__comment-input"
                               onChange={handleInputChange} value={addReview}/>
                        <div className="feed__comment-submit" onClick={uploadReview}
                             style={{color: postReviewBtnColor}}>게시
                        </div>
                    </div>
                    <ul>
                        {newReview.map((review, index) => (
                            <li key={index} className="comment-text">
                                {review.text}
                                <button onClick={() => pushHeart(index)}>{review.liked ? '<3' : '<>'}</button>
                                <button onClick={() => removeComm(index)}>삭제</button>
                            </li>
                        ))}
                    </ul>
                </div>
                ))}
                {!isLast &&(
                <button  id="addPostBtn" onClick={addPost}>
                    <img src={addImg} alt="Add post" />
                </button>

                )}
            </div>

            <div className="box3">
           <Box3 type="main" ></Box3>
            </div>
        </div>
    );
}

export default Main;
