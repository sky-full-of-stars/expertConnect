package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    
    List<Order> findByExpertId(Long expertId);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    Page<Order> findByExpertId(Long expertId, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.expert.id = :expertId AND o.status = :status")
    List<Order> findByExpertIdAndStatus(@Param("expertId") Long expertId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.sessionDate BETWEEN :startDate AND :endDate")
    List<Order> findBySessionDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 