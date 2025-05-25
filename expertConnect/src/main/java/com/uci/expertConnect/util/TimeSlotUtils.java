package com.uci.expertConnect.util;

import com.uci.expertConnect.dto.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public static String getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().toString();
    }
    
    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }
    
    public static List<TimeSlot> generateNotAvailableSlots(List<TimeSlot> availableSlots) {
        List<TimeSlot> notAvailableSlots = new ArrayList<>();
        
        // If no available slots, entire day is not available
        if (availableSlots == null || availableSlots.isEmpty()) {
            notAvailableSlots.add(new TimeSlot("00:00", "23:59"));
            return notAvailableSlots;
        }
        
        // Add not available slots before first available slot
        TimeSlot firstSlot = availableSlots.get(0);
        if (!firstSlot.getFrom().equals("00:00")) {
            notAvailableSlots.add(new TimeSlot("00:00", 
                decrementHour(firstSlot.getFrom())));
        }
        
        // Add not available slots between available slots
        for (int i = 0; i < availableSlots.size() - 1; i++) {
            TimeSlot current = availableSlots.get(i);
            TimeSlot next = availableSlots.get(i + 1);
            
            if (!current.getTo().equals(next.getFrom())) {
                notAvailableSlots.add(new TimeSlot(
                    incrementHour(current.getTo()),
                    decrementHour(next.getFrom())
                ));
            }
        }
        
        // Add not available slots after last available slot
        TimeSlot lastSlot = availableSlots.get(availableSlots.size() - 1);
        if (!lastSlot.getTo().equals("23:59")) {
            notAvailableSlots.add(new TimeSlot(
                incrementHour(lastSlot.getTo()),
                "23:59"
            ));
        }
        
        return notAvailableSlots;
    }
    
    private static String incrementHour(String time) {
        LocalTime localTime = parseTime(time);
        return localTime.plusMinutes(1).format(TIME_FORMATTER);
    }
    
    private static String decrementHour(String time) {
        LocalTime localTime = parseTime(time);
        return localTime.minusMinutes(1).format(TIME_FORMATTER);
    }
} 