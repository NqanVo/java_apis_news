package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findUserEntityByUsername(String userName);
    UserEntity findByVerifyCodeEmail(String code);
}
