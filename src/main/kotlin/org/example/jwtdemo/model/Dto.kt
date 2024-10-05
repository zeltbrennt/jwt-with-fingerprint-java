package org.example.jwtdemo.model

import kotlinx.serialization.Serializable


@Serializable
data class SecretDto(val name: String, val value: String)

@Serializable
data class LoginDto(val user: String, val password: String)

fun LoginDto.isValid(): Boolean {
    return user == "admin" && password == "password"
}

@Serializable
data class TokenDto(val token: String)

data class Fingerprint(val raw: String, val hash: String)