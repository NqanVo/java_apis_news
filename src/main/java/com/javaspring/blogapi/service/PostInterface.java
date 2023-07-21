package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.post.PostRequestDTO;
import com.javaspring.blogapi.dto.post.PostResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostInterface {
    PostResponseDTO save(PostRequestDTO postRequestDTO, MultipartFile[] file) throws IOException;
    void deletePosts(Long[] ids);
    List<PostResponseDTO> findAll(Pageable pageable, String username, String category, String title);
    PostResponseDTO findById(Long id);
    void deletePost(Long id);
    int countItems();
}
