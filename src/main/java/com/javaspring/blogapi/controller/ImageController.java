package com.javaspring.blogapi.controller;

import com.javaspring.blogapi.dto.error.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "Image Controller")
public class ImageController {
    private final String uploadDir = "uploads"; // Tên thư mục uploads
    @Operation(
            description = "Lấy hình ảnh",
            responses = {
                    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class))), responseCode = "200")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
            })
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) throws IOException {
        // Lấy path đến thư mục uploads
        Path folderImages = Paths.get(System.getProperty("user.dir"), uploadDir);

        // Lấy đường dẫn đến file
        Path filePath = folderImages.resolve(fileName);
        byte[] imageBytes = Files.readAllBytes(filePath);
        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        // Trả về file dưới dạng hình ảnh
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
