package com.javaspring.blogapi;

import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.model.RoleEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.repository.UserRepository;
import com.javaspring.blogapi.service.impl.RoleService;
import com.javaspring.blogapi.service.impl.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class initDatabase {
    @Autowired
    private RoleService roleService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private RoleEntity roleEntityUser = new RoleEntity();
    private RoleEntity roleEntityAdmin = new RoleEntity();
    private UserEntity userEntity = new UserEntity();
    @PostConstruct
    public void init(){
        if(roleService.findByName("ROLE_USER") == null){
            roleEntityUser.setCode("ROLE_USER");
            roleEntityUser.setName("ROLE_USER");
            roleEntityUser = roleService.save(roleEntityUser);
        }
        if(roleService.findByName("ROLE_ADMIN") == null){
            roleEntityAdmin.setName("ROLE_ADMIN");
            roleEntityAdmin.setCode("ROLE_ADMIN");
            roleEntityAdmin = roleService.save(roleEntityAdmin);
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
        if(userRepository.findByUsername("admin@gmail.com") == null){
            userEntity.setStatus(1);
            userEntity.setEnabled(true);
            userEntity.setUsername("admin@gmail.com");
            userEntity.setFullName("Tran Van Admin");
            userEntity.setPassword(passwordEncoder.encode("123Abc"));
            roleEntityAdmin = roleService.findByName("ROLE_ADMIN");
            userEntity.getRoleEntities().add(roleEntityAdmin);
            userEntity = userRepository.save(userEntity);
        }
    }
}
