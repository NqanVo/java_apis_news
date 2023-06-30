package com.javaspring.blogapi.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaspring.blogapi.dto.BaseDTO;
import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SubCommentDTO extends BaseDTO {
    @NotNull(message = "Bình luận không được trống")
    @NotBlank(message = "Bình luận không được trống")
    @Size(min = 5, max = 1000, message = "Tối thiểu là 5 ký tự và tối đa là 1000")
    @Pattern(regexp = "(?=^[^\\s].*)(?=.*[^\\s]$).*", message = "Không được để thừa dấu cách đầu/cuối đoạn")
    private String comment;

    @JsonIgnore
    private CommentEntity commentEntity;
    @JsonIgnore
    private UserEntity userEntity;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean existsSubComment = false;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idPrimaryComment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentEntity getCommentEntity() {
        return commentEntity;
    }

    public void setCommentEntity(CommentEntity commentEntity) {
        this.commentEntity = commentEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Long getIdPrimaryComment() {
        return idPrimaryComment;
    }

    public void setIdPrimaryComment(Long idPrimaryComment) {
        this.idPrimaryComment = idPrimaryComment;
    }
}
