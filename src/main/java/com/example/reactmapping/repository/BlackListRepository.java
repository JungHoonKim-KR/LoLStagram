package com.example.reactmapping.repository;

import com.example.reactmapping.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListRepository extends JpaRepository<AccessToken,Long> {
    Optional<AccessToken> findAccessTokenByEmailId(String emailId);
}
