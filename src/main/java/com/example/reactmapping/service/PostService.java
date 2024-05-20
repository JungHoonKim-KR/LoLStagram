package com.example.reactmapping.service;

import com.example.reactmapping.dto.PostCommentDto;
import com.example.reactmapping.dto.PostDto;
import com.example.reactmapping.dto.PostResultDto;
import com.example.reactmapping.entity.*;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.norm.ImageType;
import com.example.reactmapping.repository.MemberRepository;
import com.example.reactmapping.repository.PostCommentRepository;
import com.example.reactmapping.repository.PostRepository;
import com.example.reactmapping.repository.SummonerInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImgService imgService;
    private final SummonerInfoRepository summonerInfoRepository;
    private final PostCommentRepository postCommentRepository;

    public void savePost(PostDto postDto) throws IOException {
        Member findMember = memberRepository.findMemberById(postDto.getMemberId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND,"회원을 찾지 못했습니다."));
        Image img ;
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .member(findMember)
                .createTime(LocalDateTime.now())
                .build();
        if (postDto.getServerImg() != null) {
            img = imgService.createImg(postDto.getServerImg(),null,postDto.getMemberId(), String.valueOf(ImageType.PostType));
            post = post.toBuilder().image(img).build();
        }
        postRepository.save(post);
    }
    public PostResultDto getPostList(Pageable pageable){
        // log start
        log.info(String.valueOf(pageable.getPageNumber()));
        log.info(String.valueOf(pageable.getPageSize()));
        // log end
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
        SummonerInfo writer = summonerInfoRepository.findBySummonerId(postCommentDto.writerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND, "회원을 찾지 못했습니다."));

        PostComment postComment = new PostComment().toBuilder()
                .writerId(writer.getId())
                .comment(postCommentDto.comment)
                .build();
        post.addComment(postComment);
        postRepository.save(post);
    }


}
