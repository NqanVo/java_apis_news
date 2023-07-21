package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.post.PostRequestDTO;
import com.javaspring.blogapi.dto.post.PostResponseDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.response.ResponseList;
import com.javaspring.blogapi.service.impl.FilesService;
import com.javaspring.blogapi.service.impl.PostService;
import com.javaspring.blogapi.service.impl.ResponseFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/posts")
@Tag(name = "Posts Controller")
@SecurityRequirement(name = "Authorization")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private FilesService filesService;

    @Operation(
            description = "Lấy danh sách bài viết",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping
    public ResponseList<PostResponseDTO> findByFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTo,
            @RequestParam(required = false) Long limit,
            @RequestParam(required = false) Long currentPage) {
        long actualLimit = (limit != null && limit > 0) ? limit : 10;
        Long actualCurrentPage = (currentPage != null && currentPage > 0) ? currentPage : 1;
        Pageable pageable = PageRequest.of(actualCurrentPage.intValue() - 1, (int) actualLimit);

        if (createFrom != null && createTo == null)
            throw new CustomException.BadRequestException("Khoảng ngày thiếu dữ kiện đích");
        //Nếu ngày từ = ngày đến thì ngày đến + 1 ngày
        if (createFrom != null && (createFrom == createTo)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTo);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            createTo = calendar.getTime();
        }
        ResponseFilter<PostResponseDTO> result = postService.findByFilter(pageable, username, categoryCode, title, createFrom, createTo);

        return new ResponseList<PostResponseDTO>(
                actualCurrentPage,
                actualLimit,
                (long) Math.ceil((double) result.total() / actualLimit),
                result.total(),
                result.data());
    }

    @Operation(
            description = "Lấy bài viết theo id",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/{idPost}")
    public PostResponseDTO findByIdPost(@PathVariable Long idPost) {
        return postService.findById(idPost);
    }

    @Operation(
            description = "Tạo bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(schema = @Schema(implementation = PostResponseDTO.class)), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "multipart/form-data",
            schema = @Schema(implementation = FormUpload.class)
    ))
    public PostResponseDTO createPost(@RequestPart(name = "post") @Valid PostRequestDTO postRequestDTO,
                                      @RequestPart(name = "file") MultipartFile[] file) throws IOException {

        if (!(filesService.notEmpty(file) && filesService.isSingleFile(file) && filesService.isImageFile(file[0]) && filesService.maxSize(file[0], 5))) {
        }
        return postService.save(postRequestDTO, file);
    }

    @Operation(
            description = "Cập nhật bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{idPost}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public PostResponseDTO updatePost(@PathVariable Long idPost, @Valid @RequestPart("post") PostRequestDTO postRequestDTO, @RequestPart(name = "file") MultipartFile[] file) throws IOException {
        postRequestDTO.setId(idPost);
        return postService.save(postRequestDTO, file);
    }

    @Operation(
            description = "Xóa bài viết, quyền USER/ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
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
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
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
}

record FormUpload(
        MultipartFile[] file,
        PostRequestDTO postRequestDTO) {
}