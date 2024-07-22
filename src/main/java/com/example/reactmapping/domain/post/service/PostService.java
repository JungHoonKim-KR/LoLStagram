package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.Image.service.ImageCreateService;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.post.domain.Post;
import com.example.reactmapping.domain.post.domain.PostComment;
import com.example.reactmapping.domain.post.repository.PostRepository;
import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.domain.post.dto.PostResultDto;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.domain.post.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImageCreateService imageCreateService;
    private final PostCommentRepository postCommentRepository;

    public void savePost(PostDto postDto) throws IOException {
        Member findMember = memberRepository.findMemberById(postDto.getMemberId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND,"회원을 찾지 못했습니다."));
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .member(findMember)
                .createTime(LocalDateTime.now())
                .build();
        if (postDto.getServerImage() != null) {
            String imageUrl = imageCreateService.createImage(postDto.getServerImage());
            post = post.toBuilder().imageUrl(imageUrl).build();
        }
        postRepository.save(post);
    }
    public PostResultDto getPostList(Pageable pageable){
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> findPostObject = postRepository.findAll(pageRequest);
        List<Post> postList = findPostObject.getContent();
        List<Long> postIds = postList.stream().map(Post::getId).collect(Collectors.toList());
        List<PostComment> allComments = postCommentRepository.findAllComments(postIds);
        Map<Long, List<PostComment>> commentsByPostId = allComments.stream().collect(Collectors.groupingBy(comment -> comment.getPost().getId()));

        List<PostDto> postDtoList = postList.stream().map(post -> {
            List<PostComment> commentList = commentsByPostId.get(post.getId());
            Post updatedPost = post.toBuilder().commentList(commentList).build();
            return PostDto.entityToDto(updatedPost);
        }).collect(Collectors.toList());

        return new PostResultDto(postDtoList,findPostObject.isLast());
    }

    public void saveComment(PostCommentDto postCommentDto){
        Post post = postRepository.findById(postCommentDto.postId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND, "게시글을 찾지 못했습니다."));
        memberRepository.findAll();
        Member writer = memberRepository.findMemberById(postCommentDto.writeId)
                .orElseThrow(()-> new AppException(ErrorCode.NOTFOUND,"회원을 찾지 못했습니다."));
        PostComment postComment = new PostComment().toBuilder()
                .writer(writer)
                .comment(postCommentDto.comment)
                .build();
        postComment.setPost(post);
        postRepository.save(post);
    }


}
