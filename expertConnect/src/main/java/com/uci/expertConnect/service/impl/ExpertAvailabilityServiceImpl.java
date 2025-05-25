package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.ExpertAvailabilityResponse;
import com.uci.expertConnect.dto.TimeSlot;
import com.uci.expertConnect.exception.ResourceNotFoundException;
import com.uci.expertConnect.model.Booking;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.repository.BookingRepository;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.service.ExpertAvailabilityService;
import com.uci.expertConnect.util.TimeSlotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertAvailabilityServiceImpl implements ExpertAvailabilityService {

    private final ExpertRepository expertRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ExpertAvailabilityResponse getExpertAvailability(String expertId) {
        try {
            // Get expert's base availability
            Expert expert = expertRepository.findById(Long.parseLong(expertId))
                .orElseThrow(() -> new ResourceNotFoundException("Expert not found with id: " + expertId));
            
            Map<String, List<TimeSlot>> baseAvailability = expert.getAvailability();
            
            // Get bookings for next 14 days
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(14);
            List<Booking> bookings = bookingRepository
                .findByExpertIdAndBookingDateBetweenOrderByBookingDate(expertId, startDate, endDate);
            
            // Create a map of day -> bookings for optimization
            Map<String, List<TimeSlot>> bookingsByDay = processBookings(bookings);
            
            // Process each day's availability
            Map<String, List<TimeSlot>> notAvailable = new HashMap<>();
            for (String day : baseAvailability.keySet()) {
                List<TimeSlot> availableSlots = baseAvailability.get(day);
                List<TimeSlot> notAvailableSlots = TimeSlotUtils.generateNotAvailableSlots(availableSlots);
                
                // Add booked slots to not available slots
                List<TimeSlot> bookedSlots = bookingsByDay.getOrDefault(day, new ArrayList<>());
                notAvailableSlots.addAll(bookedSlots);
                
                notAvailable.put(day, notAvailableSlots);
            }
            
            return new ExpertAvailabilityResponse(baseAvailability, notAvailable);
            
        } catch (ResourceNotFoundException e) {
            log.error("Expert not found: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Invalid expert ID format: {}", expertId);
            throw new IllegalArgumentException("Invalid expert ID format");
        } catch (Exception e) {
            log.error("Error getting expert availability: {}", e.getMessage());
            throw new RuntimeException("Error processing expert availability");
        }
    }
    
    private Map<String, List<TimeSlot>> processBookings(List<Booking> bookings) {
        return bookings.stream()
            .collect(Collectors.groupingBy(
                booking -> TimeSlotUtils.getDayOfWeek(booking.getBookingDate()),
                Collectors.mapping(
                    booking -> new TimeSlot(
                        booking.getBookingTime().toString(),
                        booking.getBookingTime().plusHours(1).toString()
                    ),
                    Collectors.toList()
                )
            ));
    }
} 