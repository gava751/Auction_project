package com.auction.platform.dto;

public record AuthResponse(String token, String email, String role) {}