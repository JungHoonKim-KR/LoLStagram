package com.example.reactmapping.domain.post.repository;

import com.example.reactmapping.domain.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment,Long> {
    @Query("select pc from PostComment pc where pc.post.id in :postIds")
    List<PostComment>findAllComments(@Param("postIds") List<Long>postIds);
}
