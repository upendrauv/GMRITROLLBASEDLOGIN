package com.example.rollbasedlogin.util;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.rollbasedlogin.model.Booking;
import com.example.rollbasedlogin.model.Driver;
import com.example.rollbasedlogin.repository.BookingRepository;
import com.example.rollbasedlogin.repository.DriverRepository;

@Component
public class TripCompletionScheduler 
{

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private DriverRepository driverRepo;

    @Scheduled(fixedRate = 30000) // every half minute
    public void checkTrips() {
        List<Booking> all = bookingRepo.findAll();
          System.out.println("cheching time");
        for (Booking b : all) {
            if (!b.isCompleted() && b.getDriverEmail() != null) {
                LocalDateTime bookingTime = b.getCreatedAt();
                if (Duration.between(bookingTime, LocalDateTime.now()).toMinutes() >= b.getDurationMin()) {
                    // Mark trip completed
                    b.setCompleted(true);
                    b.setStatus("COMPLETED");
                    bookingRepo.save(b);

                    // Make driver available again
                    Driver d = driverRepo.findByEmail(b.getDriverEmail());
                    if (d != null) 
                    {
                        d.setAvailable(true);
                        driverRepo.save(d);
                    }
                }
            }
        }
    }


    // inside TripCompletionScheduler

@Scheduled(fixedRate = 30000)
public void assignWaitingBookings() 
{
    List<Booking> waiting = bookingRepo.findByStatus("BOOKED");
    System.out.println("checking status");
    for (Booking b : waiting) {
        List<Driver> drivers = driverRepo.findByCabTypeAndAvailable(b.getCabType(), true);
        if (!drivers.isEmpty()) {
            Driver d = drivers.get(0);
            b.setDriverEmail(d.getEmail());
            b.setStatus("ASSIGNED");

            d.setAvailable(false);
            driverRepo.save(d);
            bookingRepo.save(b);
        }
    }
}

}
