package com.example.rollbasedlogin.controller;



import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rollbasedlogin.model.Booking;
import com.example.rollbasedlogin.model.Driver;
import com.example.rollbasedlogin.repository.BookingRepository;
import com.example.rollbasedlogin.repository.DriverRepository;
import com.example.rollbasedlogin.service.ExcelExportService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HRController {

  @Autowired
private DriverRepository driverRepo;

@Autowired
ExcelExportService excelExportService;

@Autowired
private BookingRepository bookingRepo;

@PostMapping("/book")
public String bookCab(@RequestBody Booking booking) 
{
    booking.setBookingDate(LocalDate.now().toString());
    booking.setStatus("BOOKED");

    // 🔍 Try to auto-assign driver
    List<Driver> drivers = driverRepo.findByCabTypeAndAvailable(booking.getCabType(), true);
    if (!drivers.isEmpty()) 
    {
        Driver assignedDriver = drivers.get(0); // Pick first available
        booking.setDriverEmail(assignedDriver.getEmail());
        booking.setStatus("ASSIGNED");

        // Mark driver as unavailable
        assignedDriver.setAvailable(false);
        driverRepo.save(assignedDriver);
    }

    bookingRepo.save(booking);
    return "Booking Successful!";
}



    @GetMapping("/mybookings")
    public java.util.List<Booking> getHRBookings(@RequestParam String email) {
        // return bookingRepo.findByHrEmail(email);
       return bookingRepo.findByHrEmailOrderByCreatedAtDesc(email);

    }
    
    @GetMapping("/mybookings/paged")
public Page<Booking> getHRBookingsPaged(
        @RequestParam String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    return bookingRepo.findByHrEmailOrderByCreatedAtDesc(email, PageRequest.of(page, size));
}


@GetMapping("/bookings/download")
public ResponseEntity<InputStreamResource> downloadExcel(@RequestParam String email) throws IOException, java.io.IOException {
    List<Booking> bookings = bookingRepo.findByHrEmailOrderByCreatedAtDesc(email);
    ByteArrayInputStream stream = excelExportService.bookingsToExcel(bookings);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=bookings.xlsx");

    return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(new InputStreamResource(stream));
}
}
