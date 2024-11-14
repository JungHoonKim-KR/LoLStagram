import React from "react";
import PostItem from "./PostItemComponent";

const PostList = ({ postList, setPostList, token, setToken, member, navigate }) => {
    return (
        <>
            {postList.map((post, postIndex) => (
                <PostItem
                    key={postIndex}
                    post={post}
                    postIndex={postIndex}
                    postList={postList}
                    setPostList={setPostList}
                    token={token}
                    setToken={setToken}
                    member={member}
                    navigate={navigate}
                />
            ))}
        </>
    );
};

export default PostList;
