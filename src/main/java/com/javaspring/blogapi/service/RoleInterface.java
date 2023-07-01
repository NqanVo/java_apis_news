package com.javaspring.blogapi.service;

import com.javaspring.blogapi.model.RoleEntity;

public interface RoleInterface {
    RoleEntity save(RoleEntity roleEntity);

    RoleEntity findByName(String name);
}
