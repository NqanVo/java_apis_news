package com.javaspring.blogapi;

import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.service.impl.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class initDatabase {
    @Autowired
    private RoleService roleService;
    @Autowired
    private CategoryRepository categoryRepository;
    private RoleEntity roleEntityUser = new RoleEntity();
    private RoleEntity roleEntityAdmin = new RoleEntity();
    @PostConstruct
    public void init(){
        if(roleService.findByName("ROLE_USER") == null){
            roleEntityUser.setCode("ROLE_USER");
            roleEntityUser.setName("ROLE_USER");
            roleService.save(roleEntityUser);
        }
        if(roleService.findByName("ROLE_ADMIN") == null){
            roleEntityAdmin.setName("ROLE_ADMIN");
            roleEntityAdmin.setCode("ROLE_ADMIN");
            roleService.save(roleEntityAdmin);
        }
        if(categoryRepository.findByCode("am-nhac") == null){
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCode("am-nhac");
            categoryEntity.setName("Âm nhạc");
            categoryRepository.save(categoryEntity);
        }
        if(categoryRepository.findByCode("the-thao") == null){
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCode("the-thao");
            categoryEntity.setName("Thể thao");
            categoryRepository.save(categoryEntity);
        }
    }
}
