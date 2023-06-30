package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.PostDTO;
import com.javaspring.blogapi.dto.comment.CommentDTO;
import com.javaspring.blogapi.dto.comment.SubCommentDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.response.ResponseList;
import com.javaspring.blogapi.service.FilesService;
import com.javaspring.blogapi.service.impl.CommentService;
import com.javaspring.blogapi.service.impl.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/posts")
@Tag(name = "Posts/Comments Controller")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired private FilesService filesService;

    @Operation(
            description = "Lấy danh sách bài viết",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping
    public ResponseList<PostDTO> findAllPost(@RequestParam(required = false) Long limit, @RequestParam(required = false) Long currentPage) {
        Long actualLimit = (limit != null && limit > 0) ? limit : 10;
        Long actualCurrentPage = (currentPage != null && currentPage > 0) ? currentPage : 1;
        Pageable pageable = PageRequest.of(actualCurrentPage.intValue() - 1, actualLimit.intValue());
        List<PostDTO> result = postService.findAll(pageable);

        ResponseList res = new ResponseList();
        res.setCurrentPage(actualCurrentPage);
        res.setLimit(actualLimit);
        res.setTotalItems((long) postService.countItems());
        res.setTotalPages((long) Math.ceil((double) postService.countItems() / actualLimit));
        res.setData(result);
        return res;
    }
    @Operation(
            description = "Lấy bài viết theo id",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/{idPost}")
    public PostDTO findByIdPost(@PathVariable Long idPost) {
        return postService.findById(idPost);
    }
    @Operation(
            description = "Tạo bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public PostDTO createPost(@Valid @RequestPart("post") PostDTO postDTO, @RequestPart(required = false) MultipartFile[] file) throws IOException {
        // * Kiểm tra xem file hợp lệ không
        if(!(filesService.notEmpty(file) && filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0],5))){}
        return postService.save(postDTO, file);
    }
    @Operation(
            description = "Cập nhật bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{idPost}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public PostDTO updatePost(@PathVariable Long idPost, @Valid @RequestPart("post") PostDTO postDTO, @RequestPart(required = false) MultipartFile[] file) throws IOException {
        postDTO.setId(idPost);
        return postService.save(postDTO,file);
    }
    @Operation(
            description = "Xóa bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> deletePosts(@RequestBody Long[] ids) {
        postService.deletePosts(ids);
        String idsToString = String.join(", ", Arrays.asList(ids).stream().map(String::valueOf).collect(Collectors.toList()));
        return ResponseEntity.ok().body("Delete success posts: " + idsToString);
    }
    @Operation(
            description = "Xóa bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @DeleteMapping("/{idPost}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().body("Delete success posts: " + id);
    }
    // * COMMENTS
    @Operation(
            description = "Lấy bình luận theo id bài viết",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/{idPost}/comments")
    public ResponseEntity<ResponseComment<CommentDTO>> findAllCommentsPost(@PathVariable Long idPost, @RequestParam(required = false) Long limit) {
        Long actualLimit = (limit == null || limit <= 0) ? 10 : limit;
        // Tạo đối tượng Sort theo thời gian tạo giảm dần
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(0, actualLimit.intValue(), sort);

        return ResponseEntity.ok().body(new ResponseComment<CommentDTO>(commentService.findByIdPost(idPost, pageable), actualLimit));
    }
    @Operation(
            description = "Tạo bình luận, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content, responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping(path = "/{idPost}/comments")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void createCommentPost(@PathVariable Long idPost, @Valid @RequestBody CommentDTO commentDTO) {
        commentService.save(idPost, commentDTO);
    }
    @Operation(
            description = "Chỉnh sửa bình luận, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{idPost}/comments/{idComment}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void updateCommentPost(@PathVariable Long idPost, @PathVariable Long idComment, @Valid @RequestBody CommentDTO commentDTO) {
        commentDTO.setId(idComment);
        commentService.save(idPost, commentDTO);
    }
    @Operation(
            description = "Xóa bình luận, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content, responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @DeleteMapping(path = "/{idPost}/comments/{idCmt}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void deleteCommentPost(@PathVariable Long idCmt) {
        commentService.deleteComment(idCmt);
    }

    // * SUBCOMMENTS
    @Operation(
            description = "Lấy bình luận con của bình luận cha",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubCommentDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/{idPost}/comments/{idComment}/subcomments")
    public ResponseEntity<ResponseComment<SubCommentDTO>> findAllSubComments(@PathVariable Long idComment, @RequestParam(required = false) Long limit) {
        Long actualLimit = (limit == null || limit <= 0) ? 10 : limit;
        // Tạo đối tượng Sort theo thời gian tạo giảm dần
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(0, actualLimit.intValue(), sort);

        return ResponseEntity.ok().body(new ResponseComment<SubCommentDTO>(commentService.subFindByIdComents(idComment, pageable), actualLimit));
    }
    @Operation(
            description = "Tạo bình luận con, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content, responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping(path = "/{idPost}/comments/{idComment}/subcomments")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void createSubComment(@PathVariable Long idComment, @Valid @RequestBody SubCommentDTO subCommentDTO) {
        commentService.subSave(idComment, subCommentDTO);
    }
    @Operation(
            description = "Sửa bình luận con, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content, responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{idPost}/comments/{idComment}/subcomments/{idSubCmt}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void updateSubComment(@PathVariable Long idComment, @PathVariable Long idSubCmt, @Valid @RequestBody SubCommentDTO subCommentDTO) {
        subCommentDTO.setId(idSubCmt);
        commentService.subSave(idComment, subCommentDTO);
    }
    @Operation(
            description = "Xóa bình luận con, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content, responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @DeleteMapping(path = "/{idPost}/comments/{idComment}/subcomments/{idSubCmt}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void deleteSubComment(@PathVariable Long idSubCmt) {
        commentService.deleteSubCmt(idSubCmt);
    }
}

record ResponseComment<T>(List<T> comments, Long limit) {
}