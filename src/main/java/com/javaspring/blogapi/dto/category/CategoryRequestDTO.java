package com.javaspring.blogapi.dto.category;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonFilter("CategoryFilter")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryRequestDTO extends BaseDTO {
    @NotNull(message = "Tên danh mục không được trống")
    @NotBlank(message = "Tên danh mục không được trống")
    @Size(min = 3, max = 20, message = "Tối thiểu là 3 ký tự và tối đa là 20")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String code;
}
