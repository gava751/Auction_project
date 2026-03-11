package com.auction.platform.config.security;

import com.auction.platform.domain.User;
import com.auction.platform.repository.UserRepository;
import com.auction.platform.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Ищем или создаем пользователя
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(oAuth2User.getAttribute("given_name"));
            newUser.setLastName(oAuth2User.getAttribute("family_name"));
            newUser.setRole("ROLE_BUYER");
            newUser.setStatus("ACTIVE");
            return userRepository.save(newUser);
        });

        // Генерируем JWT
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), "", authentication.getAuthorities()));

        // Перенаправляем на фронтенд с токеном в параметрах (простейший способ для курсовой)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/callback")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}