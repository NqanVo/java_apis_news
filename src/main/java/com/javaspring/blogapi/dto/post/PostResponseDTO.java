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
public class PostResponseDTO extends BaseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String content;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String shortDesc;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String thumbnail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long totalComments;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CategoryResponseDTO category;
}

