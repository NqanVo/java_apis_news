package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleEntity save(RoleEntity roleEntity){
        return roleRepository.save(roleEntity);
    }
    public RoleEntity findByName(String name){
        return roleRepository.findByName(name);
    }
}
