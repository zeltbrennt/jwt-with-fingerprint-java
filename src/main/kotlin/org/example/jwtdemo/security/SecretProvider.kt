package org.example.jwtdemo.security

import io.jsonwebtoken.Jwts
import javax.crypto.SecretKey

object SecretProvider {

    private val key = Jwts.SIG.HS256.key().build();
    fun getSigningKey(): SecretKey {
        return key
    }
}
