package com.bestarch.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bestarch.demo.domain.Appointment;

@Repository
public interface AppointmentCrudRepository extends CrudRepository<Appointment, String> {
    
}