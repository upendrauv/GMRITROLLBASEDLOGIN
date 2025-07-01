package com.example.rollbasedlogin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rollbasedlogin.model.Booking;
import com.example.rollbasedlogin.model.Driver;
import com.example.rollbasedlogin.repository.BookingRepository;
import com.example.rollbasedlogin.repository.DriverRepository;

@RestController
@RequestMapping("/api/driver")
@CrossOrigin(origins = "*")
public class DriverController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private DriverRepository driverRepo;

    @GetMapping("/mytrips")
    public List<Booking> getDriverBookings(@RequestParam String email) {
        return bookingRepo.findByDriverEmail(email);
    }

    @PutMapping("/complete-trip/{bookingId}")
public ResponseEntity<String> completeTrip(@PathVariable Long bookingId) {
    Optional<Booking> optional = bookingRepo.findById(bookingId);
    if (optional.isPresent()) {
        Booking booking = optional.get();
        booking.setStatus("COMPLETED");
        bookingRepo.save(booking);

        // Make driver available again
        String driverEmail = booking.getDriverEmail();
        if (driverEmail != null) {
            Optional<Driver> driverOpt = driverRepo.findAll()
                .stream()
                .filter(d -> d.getEmail().equals(driverEmail))
                .findFirst();

            driverOpt.ifPresent(driver -> {
                driver.setAvailable(true);
                driverRepo.save(driver);
            });
        }

        return ResponseEntity.ok("Trip marked as completed");
    } else {
        return ResponseEntity.status(404).body("Booking not found");
    }
}


}
