import React from "react";

const CommentSection = ({ postIndex, post, handleInputChange, handleInputClick, uploadReview, activePostIndex }) => (
    <div>
        <div className="feed__comment-field">
            <input
                type="text"
                placeholder="댓글달기..."
                className="feed__comment-input"
                onChange={(e) => handleInputChange(e, postIndex)}
                value={post.newComment}
                onClick={() => handleInputClick(postIndex)}
            />
            <div className="feed__comment-submit" onClick={() => uploadReview(postIndex)} style={{ color: post.postReviewBtnColor }}>
                게시
            </div>
        </div>
        {activePostIndex === postIndex && (
            <div className="comment-list">
                {post.commentList.map((comment, index) => (
                    <div key={index} className="comment">
                        <p>{comment.writerName}</p> &nbsp;
                        <p>{comment.comment}</p>
                    </div>
                ))}
            </div>
        )}
        <ul>
            {post.comments.map((review, commentIndex) => (
                <li key={commentIndex} className="comment-text">
                    <strong>{review.author} </strong>
                    {review.text}
                </li>
            ))}
        </ul>
    </div>
);

export default CommentSection;
