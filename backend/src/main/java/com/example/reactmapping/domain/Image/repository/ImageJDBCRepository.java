package com.example.reactmapping.domain.Image.repository;

import com.example.reactmapping.domain.Image.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImageJDBCRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageJDBCRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Image> imageList){
        String sql ="INSERT INTO image (type, name, url) VALUES (?,?,?)";
        jdbcTemplate.batchUpdate(sql, imageList, 50, (ps, image) ->{
            ps.setString(1, image.getType());
            ps.setString(2, image.getName());
            ps.setString(3, image.getUrl());
        });
    }
}
