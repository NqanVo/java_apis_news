package com.javaspring.blogapi.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.javaspring.blogapi.config.IsUserOrIsAdmin;
import com.javaspring.blogapi.converter.PostConverter;
import com.javaspring.blogapi.dto.post.PostRequestDTO;
import com.javaspring.blogapi.dto.post.PostResponseDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.repository.PostRepository;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.PostInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final IsUserOrIsAdmin userDetailsJwt = new IsUserOrIsAdmin();
    @Autowired
    private FilesService filesService;
    @Autowired
    private Cloudinary cloudinary;
    private final EntityManager entityManager;

    public PostService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    String newImageName = null;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PostResponseDTO save(PostRequestDTO postRequestDTO, MultipartFile[] file) throws IOException {
        try {
            CategoryEntity categoryEntity = categoryRepository.findByCode(postRequestDTO.getCategoryCode());
            userDetailsJwt.getUserAndIsAdmin();
            PostEntity newPostEntity;
            String oldImageName = null;
            boolean updateWithImage = false;
            if (postRequestDTO.getId() == null) {
                // * Kiểm tra xem tiêu đề tồn tại chưa
                if (postRepository.existsByTitle(postRequestDTO.getTitle()))
                    throw new CustomException.BadRequestException("Bài đăng đã tồn tại");
                // * Kiểm tra xem file hợp lệ không
                if (!(filesService.notEmpty(file) && filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 5))) {
                }
                // * 2 Lưu ảnh mới
//            newImageName = new Date().getTime() + file[0].getOriginalFilename();
//            filesService.moveImageToFolder(file[0], newImageName);
                newImageName = cloudinary.uploader().upload(file[0].getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
                postRequestDTO.setThumbnail(newImageName);
                // * Chuyển dto sang entity
                newPostEntity = postConverter.ConverterPostDTOToPost(postRequestDTO);
                newPostEntity.setUserEntity(userDetailsJwt.getUserEntity());
            } else {
                // * Tìm post cũ
                PostEntity oldPostEntity = postRepository.findById(postRequestDTO.getId())
                        .orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng " + postRequestDTO.getId()));
                // * Kiểm tra xem đúng user hay có quyền admin không
                if (!(userDetailsJwt.getUserEntity().equals(oldPostEntity.getUserEntity()) || userDetailsJwt.isAdmin()))
                    throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
                // * Kiểm tra xem tiêu đề tồn tại chưa
                if (postRepository.existsByTitle(postRequestDTO.getTitle()))
                    throw new CustomException.BadRequestException("Bài đăng đã tồn tại");
                // * 1 Lấy thumbnail cũ
                oldImageName = oldPostEntity.getThumbnail();
                if (!(file == null || file.length == 0)) {
                    if (!(filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 5))) {
                    }
                    // * cập nhật có kèm ảnh
                    updateWithImage = true;
                    // * 2 Lưu ảnh mới
//                newImageName = new Date().getTime() + file[0].getOriginalFilename();
//                filesService.moveImageToFolder(file[0], newImageName);
                    newImageName = cloudinary.uploader().upload(file[0].getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
                    postRequestDTO.setThumbnail(newImageName);
                } else {
                    postRequestDTO.setThumbnail(oldImageName);
                }
                // * Đè Post mới sang post cũ
                newPostEntity = postConverter.ConverterNewPostDTOToOldPost(postRequestDTO, oldPostEntity);
            }
            newPostEntity.setCategoryEntity(categoryEntity);
            // * 3 Lưu post || sửa post cũ
            newPostEntity = postRepository.save(newPostEntity);
            // * Chuyển entity sang dto
            PostResponseDTO dto = postConverter.ConverterPostToPostDTO(newPostEntity);
            // * 4 Xóa thumbnail cũ nếu có
            if (!(oldImageName == null || oldImageName.equals("")) && updateWithImage) {
//            filesService.deleteImageFromFolder(oldImageName);
                String publicId = getPublicIdFromImageUrl(oldImageName);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
            return dto;
        } catch (Exception ex) {
            if (newImageName != null) {
                String publicId = getPublicIdFromImageUrl(newImageName);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
            throw new CustomException.BadRequestException(ex.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deletePosts(Long[] ids) {
        try {
            userDetailsJwt.getUserAndIsAdmin();
            for (Long id : ids) {
                PostEntity post = postRepository.findById(id).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng " + id));
                String oldAvatar = post.getThumbnail();
                if (!(userDetailsJwt.getUserEntity().equals(post.getUserEntity()) || userDetailsJwt.isAdmin()))
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
            if (!(userDetailsJwt.getUserEntity().equals(post.getUserEntity()) || userDetailsJwt.isAdmin()))
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
    public List<PostResponseDTO> findAll(Pageable pageable, String username, String category, String title) {
        List<PostResponseDTO> postResponseDTOlist = new ArrayList<>();
        List<PostEntity> postEntityList = postRepository.findAll(pageable).getContent();
        for (PostEntity item : postEntityList)
            postResponseDTOlist.add(postConverter.ConverterPostToPostDTO(item));
        return postResponseDTOlist;
    }

    public ResponseFilter<PostResponseDTO> findByFilter(Pageable pageable, String username, String categoryCode, String title, Date createFrom, Date createTo) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        CriteriaQuery<PostEntity> filterQuery = criteriaBuilder.createQuery(PostEntity.class);

        // Select count(*) from tbl_posts
        Root<PostEntity> countRoot = countQuery.from(PostEntity.class);
        countQuery.select(criteriaBuilder.count(countRoot));

        // Select * from tbl_posts
        Root<PostEntity> root = filterQuery.from(PostEntity.class);
        filterQuery.select(root);

        Predicate filterPredicate = buildPredicate(criteriaBuilder, root, username, categoryCode, title, createFrom, createTo);
        Predicate countPredicate = buildPredicate(criteriaBuilder, countRoot, username, categoryCode, title, createFrom, createTo);

        // * Truy vấn theo điều kiện, sắp xếp theo ngày tạo
        filterQuery.where(filterPredicate).orderBy(criteriaBuilder.desc(root.get("createdDate")));
        // * Truy vấn tổng kết quả
        countQuery.where(countPredicate);

        // * Phân trang
        TypedQuery<PostEntity> typedQuery = entityManager.createQuery(filterQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        // * Đếm
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);

        // * Thực hiện truy vấn
        List<PostEntity> postEntities = typedQuery.getResultList();
        Long totalResults = countTypedQuery.getSingleResult();

        List<PostResponseDTO> postResponseDTOS = postEntities.stream().map(postEntity -> postConverter.ConverterPostToPostDTO(postEntity)).toList();

        return new ResponseFilter<PostResponseDTO>(postResponseDTOS, totalResults);
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<PostEntity> root, String username, String categoryCode, String title, Date createFrom, Date createTo) {
        // Danh sách điều kiện nếu có
        List<Predicate> predicateList = new ArrayList<>();

        if (title != null && !title.equals("")) {
            Predicate titlePredicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            predicateList.add(titlePredicate);
        }

        if (username != null && !username.equals("")) {
            Join<PostEntity, UserEntity> userJoin = root.join("userEntity");
            Predicate usernamePredicate = criteriaBuilder.like(userJoin.get("username"), "%" + username + "%");
            predicateList.add(usernamePredicate);
        }

        if (categoryCode != null && !categoryCode.equals("")) {
            Join<PostEntity, CategoryEntity> categoryJoin = root.join("categoryEntity");
            Predicate categoryCodePredicate = criteriaBuilder.equal(categoryJoin.get("code"), categoryCode);
            predicateList.add(categoryCodePredicate);
        }

        if (createFrom != null && createTo != null) {
            Predicate createDatePredicate = criteriaBuilder.between(root.get("createdDate"), createFrom, createTo);
            predicateList.add(createDatePredicate);
        }
        // * Kết hợp các điều kiện
        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    @Override
    public PostResponseDTO findById(Long idPost) {
        Optional<PostEntity> post = postRepository.findById(idPost);
        if (post.isEmpty()) {
            throw new CustomException.NotFoundException("Không tìm thấy bài đăng");
        }
        return postConverter.ConverterPostToPostDTO(post.get());
    }

    private String getPublicIdFromImageUrl(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf('/') + 1;
        int endIndex = imageUrl.lastIndexOf('.');
        return imageUrl.substring(startIndex, endIndex);
    }
}