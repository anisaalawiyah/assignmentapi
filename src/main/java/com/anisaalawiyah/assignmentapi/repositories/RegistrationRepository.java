package com.anisaalawiyah.assignmentapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anisaalawiyah.assignmentapi.entities.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, String> {

    

}
