package com.javaspring.blogapi.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @CreatedBy
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;
    @LastModifiedBy
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String updatedBy;
    @LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updatedDate;
}
