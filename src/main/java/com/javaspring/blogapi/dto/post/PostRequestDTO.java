package com.javaspring.blogapi.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostRequestDTO extends BaseDTO {
    @NotNull(message = "Tiêu đề không được trống")
    @NotBlank(message = "Tiêu đề không được trống")
    @Size(min = 10, max = 300, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String title;
    @NotNull(message = "Nội dung không được trống")
    @NotBlank(message = "Nội dung không được trống")
//    @Size(min = 10, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s$).*$", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String content;
    @NotNull(message = "Mô tả ngắn không được trống")
    @NotBlank(message = "Mô tả không được trống")
    @Size(min = 10, max = 300, message = "Tối thiểu là 10 ký tự và tối đa là 300")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String shortDesc;
    @NotNull(message = "Thể loại không được trống")
    @NotBlank(message = "Thể loại không được trống")
    @Pattern(regexp = "^[a-z]+(-[a-z]+)*$", message = "Định dạng mã danh mục không hợp lệ")
    private String categoryCode;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String thumbnail;
}

