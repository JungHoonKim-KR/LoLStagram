package com.example.reactmapping.service;

import com.example.reactmapping.dto.PostDto;
import com.example.reactmapping.dto.PostResultDto;
import com.example.reactmapping.entity.Image;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.entity.Post;
import com.example.reactmapping.norm.ImageType;
import com.example.reactmapping.repository.MemberRepository;
import com.example.reactmapping.repository.PostRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImgService imgService;

    public void save(PostDto postDto) throws IOException {
        Member findMember = memberRepository.findMemberById(postDto.getMemberId()).get();
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
        log.info(String.valueOf(pageable.getPageNumber()));
        log.info(String.valueOf(pageable.getPageSize()));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> findPostObject = postRepository.findAll(pageRequest);
        List<Post> postList = findPostObject.getContent();
        List<PostDto>postDtoList= new ArrayList<>();
        for(Post post : postList){
            PostDto postDto = PostDto.entityToDto(post);
            postDtoList.add(postDto);
        }

        return new PostResultDto(postDtoList,findPostObject.isLast());
    }




}
