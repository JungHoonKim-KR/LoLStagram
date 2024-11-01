//package com.example.reactmapping.domain.Image.repository;
//
//import com.example.reactmapping.domain.Image.domain.Image;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface ImageRepository extends JpaRepository<Image,Long> {
//    @Query("select i from Image i where i.emailId=:emailId and i.objectType=:type")
//    Optional<Image> findProfileImg(@Param("emailId") String emailId,@Param("type")String type);
//}
