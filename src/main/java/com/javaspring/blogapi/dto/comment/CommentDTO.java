package com.javaspring.blogapi.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import com.javaspring.blogapi.dto.user.UserDTO;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.model.SubCommentEntity;
import com.javaspring.blogapi.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO extends BaseDTO {
    @NotNull(message = "Bình luận không được trống")
    @NotBlank(message = "Bình luận không được trống")
    @Size(min = 5, max = 1000, message = "Tối thiểu là 5 ký tự và tối đa là 1000")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String comment;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean existsSubComment = false;
    @JsonIgnore
    private PostEntity postEntity;
    @JsonIgnore
    private UserEntity userEntity;
}
