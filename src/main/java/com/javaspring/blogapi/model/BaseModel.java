package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass //để có thể xế thừa các column
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class BaseModel {

    @Id //Not null. PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Tự động tăng id
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column
    @CreatedBy
    private String createdBy;
    @Column
    @CreatedDate
    private Date createdDate;
    @Column
    @LastModifiedBy
    private String updatedBy;
    @Column
    @LastModifiedDate
    private Date updatedDate;
}
