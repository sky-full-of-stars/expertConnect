package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.model.UserSearchHistory;
import com.uci.expertConnect.repository.UserSearchHistoryRepository;
import com.uci.expertConnect.service.UserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchHistoryServiceImpl implements UserSearchHistoryService {

    private final UserSearchHistoryRepository userSearchHistoryRepository;

    @Override
    public List<String> getSearchQueriesByUserId(String userId) {
        return userSearchHistoryRepository.findTop5ByUserIdOrderBySearchedAtDesc(userId)
                .stream()
                .map(UserSearchHistory::getQuery)
                .toList();
    }
}
