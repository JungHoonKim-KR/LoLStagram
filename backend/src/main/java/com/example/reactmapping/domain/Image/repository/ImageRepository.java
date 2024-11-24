package com.example.reactmapping.domain.Image.repository;

import com.example.reactmapping.domain.Image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    @Query("SELECT i.url FROM Image i WHERE i.type = :type AND i.name = :name")
    Optional<String> findUrlByTypeAndName(@Param("type") String type, @Param("name") String name);


    @Query("select i.url from Image i where i.type = :type")
    List<String> findAllUrl(@Param("type") String type);

    @Query("select i.name from Image i where i.type = :type")
    List<String> findAllName(@Param("type") String type);
    List<Image> findAllByType(String type);
}
