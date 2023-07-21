package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.category.CategoryRequestDTO;
import com.javaspring.blogapi.dto.post.PostResponseDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.response.CustomFilterProps;
import com.javaspring.blogapi.service.impl.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/category")
@Tag(name = "Category Controller")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CustomFilterProps customFilterProps;
    Set<String> props = new HashSet<>();

    public CategoryController() {
        props.add("id");
        props.add("code");
        props.add("name");
    }

    @Operation(
            description = "Lấy danh sách danh mục",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping
    public List<CategoryResponseDTO> findAllCat() {
        return categoryService.findAllCat();
    }

    //    public MappingJacksonValue findAllCat() {
//        return customFilterProps.mappingJacksonValue(categoryService.findAllCat(), props, "CategoryFilter", false);
//    }
    @Operation(
            description = "Lấy danh mục theo code",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping(path = "/{code}")
    public CategoryResponseDTO findCatByCode(@PathVariable String code) {
        return categoryService.findByCode(code);
    }

    //    public MappingJacksonValue findCatByCode(@PathVariable String code) {
//        return customFilterProps.mappingJacksonValue(categoryService.findByCode(code), props, "CategoryFilter", true);
//    }
    @Operation(
            description = "Tạo danh mục, quyền ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponseDTO>> createCat(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        categoryService.save(categoryRequestDTO);
        return ResponseEntity.ok().body(categoryService.findAllCat());
    }

    //    public ResponseEntity<MappingJacksonValue> createCat(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
//        categoryService.save(categoryRequestDTO);
//        return ResponseEntity.ok().body(customFilterProps.mappingJacksonValue(categoryService.findAllCat(), props, "CategoryFilter", false));
//    }
    @Operation(
            description = "Chỉnh sửa danh mục, quyền ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryRequestDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateCat(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO, @PathVariable Long id) {
        categoryRequestDTO.setId(id);
        return ResponseEntity.ok().body(categoryService.save(categoryRequestDTO));
    }

    @Operation(
            description = "Xóa danh mục, quyền ADMIN",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDTO.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Truy cập bị cấm", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryResponseDTO> deleteCat(@PathVariable String code) {
        return categoryService.deleteCat(code);
    }
}
