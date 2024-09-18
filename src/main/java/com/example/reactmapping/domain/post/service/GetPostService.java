package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.post.entity.Post;
import com.example.reactmapping.domain.post.entity.PostComment;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.domain.post.dto.PostResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GetPostService {
    private final PostService postService;

    public PostResultDto getPostList(Pageable pageable) {
        Page<Post> postsPage = postService.findAllPost(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createTime")));
        List<Post> posts = postsPage.getContent();
        Map<Long, List<PostComment>> commentsByPostId = getCommentsGroupedByPostId(posts);
        List<PostDto> postDtos = mappingComments(posts, commentsByPostId);
        return new PostResultDto(postDtos, postsPage.isLast());
    }

    private Map<Long, List<PostComment>> getCommentsGroupedByPostId(List<Post> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        List<PostComment> comments = postService.findAllComments(postIds);
        return comments.stream().collect(Collectors.groupingBy(comment -> comment.getPost().getId()));
    }
    private static List<PostDto> mappingComments(List<Post> posts, Map<Long, List<PostComment>> commentsByPostId) {
        List<PostDto> postDtos = posts.stream()
                .map(post -> {
                    List<PostComment> comments = commentsByPostId.getOrDefault(post.getId(), Collections.emptyList());
                    return PostDto.entityToDto(post.toBuilder().commentList(comments).build());
                })
                .collect(Collectors.toList());
        return postDtos;
    }
}
