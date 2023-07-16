package com.javaspring.blogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_roles")
@Table(name = "tbl_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity extends BaseModel {
    @Column
    private String code;
    @Column
    private String name;

    @ManyToMany(mappedBy = "roleEntities") //map theo ten list role ở User model
    private List<UserEntity> userEntities = new ArrayList<>();

    @Override
    public String toString() {
        return this.name;
    }
}
