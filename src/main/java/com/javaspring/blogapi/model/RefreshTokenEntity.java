package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "tbl_refresh_token")
@Table(name = "tbl_refresh_token")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshTokenEntity extends BaseModel {
    @Column
    private String refreshToken;
    @Column
    private Date expired;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;
}
