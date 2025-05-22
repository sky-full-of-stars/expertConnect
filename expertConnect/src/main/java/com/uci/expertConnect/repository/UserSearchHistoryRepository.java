package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findTop5ByUserIdOrderBySearchedAtDesc(String userId);
}
