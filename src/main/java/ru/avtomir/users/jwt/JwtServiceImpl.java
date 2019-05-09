package ru.avtomir.users.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JwtServiceImpl implements JwtService {
    private static final String ROLES_JWT_NAME = "roles";
    private static final String ROLES_JWT_SEPARATOR = ",";

    private final String secret;
    private final Long expiration;

    public JwtServiceImpl(String secret,
                          Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    @Override
    public JwtToken getToken(String userName, Set<String> roleNames) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        String token = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .claim(ROLES_JWT_NAME, String.join(ROLES_JWT_SEPARATOR, roleNames))
                .compact();
        return new JwtToken(token);
    }

    @Override
    public JwtUser getJwtUser(String token) {
        String userName = getClaimFromToken(token, Claims::getSubject);
        Set<String> roles = getClaimFromToken(token,
                claims -> Arrays.stream(claims.get(ROLES_JWT_NAME).toString().split(ROLES_JWT_SEPARATOR))
                        .collect(Collectors.toSet()));
        return new JwtUser(userName, roles);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }
}
