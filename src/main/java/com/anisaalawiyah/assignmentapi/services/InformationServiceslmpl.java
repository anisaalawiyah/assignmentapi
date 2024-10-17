package com.anisaalawiyah.assignmentapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InformationServiceslmpl implements InformationServices {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getAllBanners() {
        String sql = "SELECT *FROM banner";
        return jdbcTemplate.queryForList(sql);
    }
    @Override
    public List<Map<String, Object>> getAllServices() {
        String sql = "SELECT *FROM services";
        return jdbcTemplate.queryForList(sql);
    }
   
}
