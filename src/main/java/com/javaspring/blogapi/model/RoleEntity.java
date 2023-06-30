package com.javaspring.blogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_roles")
@Table(name = "tbl_roles")
public class RoleEntity extends BaseModel {
    @Column
    private String code;
    @Column
    private String name;

    @ManyToMany(mappedBy = "roleEntities") //map theo ten list role ở User model
    private List<UserEntity> userEntities = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserEntity> getUsers() {
        return userEntities;
    }

    public void setUsers(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
