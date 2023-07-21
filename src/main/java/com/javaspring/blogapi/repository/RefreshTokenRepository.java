package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.RefreshTokenEntity;
import com.javaspring.blogapi.model.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    List<RefreshTokenEntity> findRefreshTokenEntitiesByUserEntity(UserEntity userEntity);

    @Transactional
    void deleteRefreshTokenEntityByUserEntity(UserEntity userEntity);

    @Transactional
    void deleteRefreshTokenEntityByUserEntityUsername(String username);

    @Transactional
    boolean existsRefreshTokenEntitiesByRefreshToken(String refreshToken);
}
