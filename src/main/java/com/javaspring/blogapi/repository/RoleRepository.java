package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
     RoleEntity findByName(String name);
}
