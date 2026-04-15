package com.cmanager.app.authentication.service;

import com.cmanager.app.authentication.data.TokenResponse;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    @Value("${app.jwt.issuer}")
    String issuer;
    @Value("${app.jwt.expiration-minutes}")
    long expMinutes;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    public TokenResponse login(String username, String password) {
        final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final String role = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER");
        final var user = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return getTokenResponse(user, role);
    }

    private TokenResponse getTokenResponse(User user, String role) {
        final Instant now = Instant.now();
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(expMinutes, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("role", role)
                .build();

        final JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        final String token = jwtEncoder
                .encode(JwtEncoderParameters.from(headers, claims))
                .getTokenValue();

        return new TokenResponse(token);
    }
}
