package org.example.jwtdemo.security.util

import io.jsonwebtoken.Jwts
import org.example.jwtdemo.security.SecretProvider
import java.util.*

fun generateJwt(fingerprint: String): String {
    return Jwts.builder()
        .subject("admin")
        .issuer("jwtdemo")
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + 3600000))
        .claim("fpt", fingerprint)
        .signWith(SecretProvider.getSigningKey())
        .compact()
}