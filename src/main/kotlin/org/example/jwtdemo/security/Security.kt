package org.example.jwtdemo.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.jwtdemo.model.Fingerprint
import org.example.jwtdemo.security.util.isValid
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class Security(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/secret").authenticated()
                .anyRequest().permitAll()
        }
            .sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {

    private val uriMatcher = AntPathRequestMatcher("/secret", HttpMethod.GET.name())

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.getHeader("Authorization")
        val cookie = request.cookies.find { it.name == "fingerprint" }
        logger.info("Token: $token")
        logger.info("Cookie: ${cookie?.value ?: "null"}")
        if (token != null && cookie != null && token.startsWith("Bearer ")) {
            val jwt = token.substring(7)
            try {
                val claims = Jwts.parser()
                    .verifyWith(SecretProvider.getSigningKey())
                    .build()
                    .parseSignedClaims(jwt)
                val fingerprint = Fingerprint(raw = cookie.value, hash = claims.payload["fpt"] as String)
                if (fingerprint.isValid().not()) {
                    throw JwtException("Fingerprint mismatch")
                }
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(claims.payload.subject, null, emptyList())
            } catch (e: JwtException) {
                SecurityContextHolder.clearContext()
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
                return
            }
        }
        filterChain.doFilter(request, response)
    }
    @Override
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val matcher = NegatedRequestMatcher(uriMatcher)
        return matcher.matches(request)
    }

}

