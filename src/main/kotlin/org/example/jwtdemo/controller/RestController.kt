package org.example.jwtdemo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.example.jwtdemo.model.LoginDto
import org.example.jwtdemo.model.SecretDto
import org.example.jwtdemo.model.TokenDto
import org.example.jwtdemo.model.isValid
import org.example.jwtdemo.security.util.generateJwt
import org.example.jwtdemo.security.util.generateRandomFingerprint
import org.springframework.web.bind.annotation.RequestParam

@RestController
class RestController {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginDto, response: HttpServletResponse): ResponseEntity<TokenDto> {
        return if (loginRequest.isValid()) {
            val fingerprint = generateRandomFingerprint()
            val token = TokenDto(generateJwt(fingerprint.hash))
            val cookie = Cookie("fingerprint", fingerprint.raw).apply {
                isHttpOnly = true
                maxAge = 3600
                setAttribute("SameSite", "Strict")
                //secure = true
            }
            response.addCookie(cookie)
            ResponseEntity(token, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/secret")
    fun secret(): ResponseEntity<SecretDto> {
        return ResponseEntity(SecretDto("True value of Pi", "3"), HttpStatus.OK)
    }
    
    @GetMapping("/echo")
    fun echo(@RequestParam value: String, response: HttpServletResponse) {
        val why = value
        response.sendRedirect("/")
    }
}
