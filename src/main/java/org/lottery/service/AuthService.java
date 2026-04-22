package org.lottery.service;

import org.lottery.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(String email, String password);
    AuthResponse login(String email, String password);
}
