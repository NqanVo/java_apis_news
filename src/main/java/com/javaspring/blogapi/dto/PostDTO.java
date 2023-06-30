package com.javaspring.blogapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.comment.CommentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PostDTO extends BaseDTO<PostDTO> {
    @NotNull(message = "Tiêu đề không được trống_N")
    @NotBlank(message = "Tiêu đề không được trống_B")
    @Size(min = 10, max = 300, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String title;
    @NotNull(message = "Nội dung không được trống")
    @NotBlank(message = "Nội dung không được trống_B")
    @Size(min = 10, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String content;
    @NotNull(message = "Mô tả ngắn không được trống")
    @NotBlank(message = "Mô tả không được trống_B")
    @Size(min = 10, max = 300, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String shortDesc;
    @NotNull(message = "Thể loại không được trống")
    @NotBlank(message = "Thể loại không được trống_B")
    @Pattern(regexp = "^[a-z]+(-[a-z]+)*$", message = "Định dạng mã danh mục không hợp lệ")
    private String categoryCode;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @NotNull(message = "Ảnh bài viết không được trống")
//    private MultipartFile[] file;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String thumbnail;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long totalComments;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

//    public MultipartFile[] getFile() {
//        return file;
//    }
//
//    public void setFile(MultipartFile[] thumbnailFile) {
//        this.file = thumbnailFile;
//    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }
}
