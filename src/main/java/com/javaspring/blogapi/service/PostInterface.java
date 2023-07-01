package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.PostDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostInterface {
    PostDTO save(PostDTO postDTO, MultipartFile[] file) throws IOException;
    void deletePosts(Long[] ids);
    List<PostDTO> findAll(Pageable pageable);
    PostDTO findById(Long id);
    void deletePost(Long id);
    int countItems();
}
