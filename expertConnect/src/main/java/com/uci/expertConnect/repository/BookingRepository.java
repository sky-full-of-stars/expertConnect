package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' AND b.recordingLink IS NULL " +
           "AND b.bookingDate <= CURRENT_DATE AND b.bookingTime <= CURRENT_TIME")
    List<Booking> findCompletedMeetingsWithoutRecording();

    List<Booking> findByExpertIdAndBookingDateBetweenOrderByBookingDate(
        String expertId, 
        LocalDate startDate, 
        LocalDate endDate
    );

    Page<Booking> findByExpertIdOrderByBookingDateDescBookingTimeDesc(String expertId, Pageable pageable);

    List<Booking> findByExpertIdOrderByBookingDateDescBookingTimeDesc(String expertId);
} 