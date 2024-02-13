package com.example.reactmapping.service;

import com.example.reactmapping.dto.PostDto;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.entity.Post;
import com.example.reactmapping.repository.MemberRepository;
import com.example.reactmapping.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
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
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    public void save(PostDto postDto) throws IOException {
        Member findMember = memberRepository.findMemberById(postDto.getMemberId()).get();
        String img = null;
//        if (postDto.getImg() != null) {
//            img = createImg(postDto.getImg());
//        }
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
//                .img(img)
                .member(findMember)
                .createTime(LocalDateTime.now())
                .build();
        postRepository.save(post);
    }
    public List<PostDto> getPostList(){
        List<Post> findPostList = postRepository.findAll();
        List<PostDto>postDtoList= new ArrayList<>();
        for(Post post : findPostList){
            PostDto postDto = PostDto.entityToDto(post);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    public String createImg(MultipartFile file) throws IOException {
        if(file.getOriginalFilename()==""){
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
        String uuid = String.valueOf(UUID.randomUUID());

        InputStream inputStream = file.getInputStream();
        File tempFile = File.createTempFile(uuid, fileExtension);
        FileUtils.copyInputStreamToFile(inputStream, tempFile);
        //// naverCloudServer 사용 예정
        return null;
    }
}
