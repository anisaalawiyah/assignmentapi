package com.anisaalawiyah.assignmentapi.controllers;

import com.anisaalawiyah.assignmentapi.services.InformationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@ApiOperation(value = "Modul information", position = 2)
@Tag(name = "2.Modul information")
public class InformationController {
    
    @Autowired
    private InformationServices informationServices;

    

    @GetMapping("Banner")
    public ResponseEntity<List<Map<String, Object>>> getAllBanners() {
        List<Map<String, Object>> getAllBanners = informationServices.getAllBanners();
        return ResponseEntity.ok(getAllBanners);
    }



   @GetMapping("services")
   public ResponseEntity<List<Map<String, Object>>> getAllServices() {
    List<Map<String, Object>> getAllServices = informationServices.getAllServices();
    return ResponseEntity.ok(getAllServices);
   }

 
}
