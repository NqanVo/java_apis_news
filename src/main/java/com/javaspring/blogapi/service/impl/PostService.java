package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.config.IsUserOrIsAdmin;
import com.javaspring.blogapi.converter.PostConverter;
import com.javaspring.blogapi.dto.PostDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.repository.PostRepository;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.PostInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class PostService implements PostInterface {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostConverter postConverter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IsUserOrIsAdmin userDetailsJwt = new IsUserOrIsAdmin();
    @Autowired
    private FilesService filesService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PostDTO save(PostDTO newPostDTO, MultipartFile[] file) throws IOException {
        CategoryEntity categoryEntity = categoryRepository.findByCode(newPostDTO.getCategoryCode());
        userDetailsJwt.getUserAndIsAdmin();
        PostEntity newPostEntity;
        String oldImageName = null;
        String newImageName;
        boolean updateWithImage = false;
        if (newPostDTO.getId() == null) {
            // * Kiểm tra xem tiêu đề tồn tại chưa
            if (postRepository.existsByTitle(newPostDTO.getTitle()))
                throw new CustomException.BadRequestException("Bài đăng đã tồn tại");
            // * Kiểm tra xem file hợp lệ không
            if (!(filesService.notEmpty(file) && filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 5))) {
            }
            // * 2 Lưu ảnh mới
            newImageName = new Date().getTime() + file[0].getOriginalFilename();
            filesService.moveImageToFolder(file[0], newImageName);
            newPostDTO.setThumbnail(newImageName);
            // * Chuyển dto sang entity
            newPostEntity = postConverter.ConverterPostDTOToPost(newPostDTO);
            newPostEntity.setUser(userDetailsJwt.getUserEntity());
        } else {
            // * Tìm post cũ
            PostEntity oldPostEntity = postRepository.findById(newPostDTO.getId())
                    .orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng " + newPostDTO.getId()));
            // * Kiểm tra xem đúng user hay có quyền admin không
            if (!(userDetailsJwt.getUserEntity().equals(oldPostEntity.getUser()) || userDetailsJwt.isAdmin()))
                throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
            // * Kiểm tra xem tiêu đề tồn tại chưa
//            if (postRepository.existsByTitle(newPostDTO.getTitle()))
//                throw new CustomException.BadRequestException("Bài đăng đã tồn tại");
            // * 1 Lấy thumbnail cũ
            oldImageName = oldPostEntity.getThumbnail();
            if (!(file == null || file.length == 0)) {
                if (!(filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 5))) {
                }
                // * cập nhật có kèm ảnh
                updateWithImage = true;
                // * 2 Lưu ảnh mới
                newImageName = new Date().getTime() + file[0].getOriginalFilename();
                filesService.moveImageToFolder(file[0], newImageName);
                newPostDTO.setThumbnail(newImageName);
            } else {
                newPostDTO.setThumbnail(oldImageName);
            }
            // * Đè Post mới sang post cũ
            newPostEntity = postConverter.ConverterNewPostDTOToOldPost(newPostDTO, oldPostEntity);
        }
        newPostEntity.setCategory(categoryEntity);
        // * 3 Lưu post || sửa post cũ
        newPostEntity = postRepository.save(newPostEntity);
        // * Chuyển entity sang dto
        PostDTO dto = postConverter.ConverterPostToPostDTO(newPostEntity);
        dto.setCategoryCode(categoryEntity.getCode());
        // * 4 Xóa thumbnail cũ nếu có
        if (!(oldImageName == null || oldImageName.equals("")) && updateWithImage) {
            filesService.deleteImageFromFolder(oldImageName);
        }
        return dto;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deletePosts(Long[] ids) {
        try {
            userDetailsJwt.getUserAndIsAdmin();
            for (Long id : ids) {
                PostEntity post = postRepository.findById(id).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng " + id));
                String oldAvatar = post.getThumbnail();
                if (!(userDetailsJwt.getUserEntity().equals(post.getUser()) || userDetailsJwt.isAdmin()))
                    throw new CustomException.UnauthorizedException("Không có quyền xóa bài đăng " + id);
                postRepository.deleteById(id);
                filesService.deleteImageFromFolder(oldAvatar);
            }
        } catch (IOException e) {
            System.out.println("Xóa ảnh không thành công");
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deletePost(Long id) {
        try {
            userDetailsJwt.getUserAndIsAdmin();
            PostEntity post = postRepository.findById(id).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng " + id));
            String oldAvatar = post.getThumbnail();
            if (!(userDetailsJwt.getUserEntity().equals(post.getUser()) || userDetailsJwt.isAdmin()))
                throw new CustomException.UnauthorizedException("Không có quyền xóa bài đăng " + id);
            postRepository.deleteById(id);
            filesService.deleteImageFromFolder(oldAvatar);
        } catch (IOException e) {
            System.out.println("Xóa ảnh không thành công");
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countItems() {
        return (int) postRepository.count();
    }

    @Override
    public List<PostDTO> findAll(Pageable pageable) {
        List<PostDTO> postDTOlist = new ArrayList<>();
        List<PostEntity> postEntityList = postRepository.findAll(pageable).getContent();
        for (PostEntity item : postEntityList)
            postDTOlist.add(postConverter.ConverterPostToPostDTO(item));
        return postDTOlist;
    }


    @Override
    public PostDTO findById(Long idPost) {
        Optional<PostEntity> post = postRepository.findById(idPost);
        if (post.isEmpty()) {
            throw new CustomException.NotFoundException("Không tìm thấy bài đăng");
        }
        return postConverter.ConverterPostToPostDTO(post.get());
    }
}
