package com.uci.expertConnect.service;

import java.util.List;

public interface UserSearchHistoryService {
    List<String> getSearchQueriesByUserId(String userId);
}
