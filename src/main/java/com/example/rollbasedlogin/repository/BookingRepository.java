package com.example.rollbasedlogin.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rollbasedlogin.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByHrEmail(String hrEmail);
    List<Booking> findByDriverEmail(String driverEmail);
     List<Booking> findByStatus(String status);
      List<Booking> findByHrEmailOrderByCreatedAtDesc(String email);
      List<Booking> findAllByOrderByCreatedAtDesc();
Page<Booking> findByHrEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}
