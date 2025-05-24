package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' AND b.recordingLink IS NULL " +
           "AND b.bookingDate <= CURRENT_DATE AND b.bookingTime <= CURRENT_TIME")
    List<Booking> findCompletedMeetingsWithoutRecording();
} 