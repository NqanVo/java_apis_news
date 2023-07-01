package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.repository.RoleRepository;
import com.javaspring.blogapi.service.RoleInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements RoleInterface {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleEntity save(RoleEntity roleEntity){
        return roleRepository.save(roleEntity);
    }
    @Override
    public RoleEntity findByName(String name){
        return roleRepository.findByName(name);
    }
}
