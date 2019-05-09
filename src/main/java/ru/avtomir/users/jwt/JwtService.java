package ru.avtomir.users.jwt;

import java.util.Set;

public interface JwtService {

    /**
     * Convert userName and roles to JWT.
     *
     * @param userName
     * @param roleNames
     * @return token.
     */
    JwtToken getToken(String userName, Set<String> roleNames);

    /**
     * Extract user from JWT.
     *
     * @param token
     * @return user.
     * Note: throws all exceptions from {@link io.jsonwebtoken.JwtParser#parseClaimsJws(String)}
     */
    JwtUser getJwtUser(String token);
}
