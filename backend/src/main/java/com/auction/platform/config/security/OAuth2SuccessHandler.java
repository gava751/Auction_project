package com.auction.platform.config.security;

import com.auction.platform.domain.User;
import com.auction.platform.repository.UserRepository;
import com.auction.platform.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required from Google");
                return;
            }

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);

                String firstName = oAuth2User.getAttribute("given_name");
                if (firstName == null) firstName = oAuth2User.getAttribute("name");
                if (firstName == null) firstName = "Google User";

                String lastName = oAuth2User.getAttribute("family_name");
                if (lastName == null) lastName = " ";

                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setRole("ROLE_BUYER");
                newUser.setStatus("ACTIVE");
                newUser.setPasswordHash(UUID.randomUUID().toString());

                return userRepository.save(newUser);
            });

            String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash() != null ? user.getPasswordHash() : "oauth2-user")
                    .authorities(user.getRole())
                    .build());

            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/callback")
                    .queryParam("token", token)
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Ошибка при OAuth2 авторизации: ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication processing failed");
        }
    }
}