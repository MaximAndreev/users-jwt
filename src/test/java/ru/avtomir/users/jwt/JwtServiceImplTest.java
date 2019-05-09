package ru.avtomir.users.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class JwtServiceImplTest {

    private final String secret = "TestSecret";
    private final Long expiration = 3600L; // 1 hour

    @Test
    void jwt() {
        JwtService jwtService = new JwtServiceImpl(secret, expiration);
        String name = "John";
        Set<String> roles = Set.of("ADMIN", "USER");
        JwtToken token = jwtService.getToken(name, roles);
        JwtUser jwtUser = jwtService.getJwtUser(token.getToken());
        Assertions.assertEquals(name, jwtUser.getUserName());
        Assertions.assertEquals(roles, jwtUser.getRoles());
    }

    @Test
    void jwtWrongSecret() {
        JwtService jwtService1 = new JwtServiceImpl(secret, expiration);
        String name = "John";
        Set<String> roles = Set.of("ADMIN", "USER");
        JwtToken token = jwtService1.getToken(name, roles);

        JwtService jwtService2 = new JwtServiceImpl("SomeOtherSecret", expiration);
        Assertions.assertThrows(SignatureException.class, () -> jwtService2.getJwtUser(token.getToken()));
    }

    @Test
    void jwtExpired() throws Exception {
        String name = "John";
        Set<String> roles = Set.of("ADMIN", "USER");
        JwtService jwtService = new JwtServiceImpl(secret, 1L);
        JwtToken token = jwtService.getToken(name, roles);
        Thread.sleep(5000L);
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtService.getJwtUser(token.getToken()));
    }
}
