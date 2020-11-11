package com.abdulmanov.schedule.security.jwt

import com.abdulmanov.schedule.security.SecurityConstants
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(private val userDetailsService: UserDetailsService) {

    private val secretKey = Base64.getEncoder().encodeToString(SecurityConstants.SECRET_KEY.toByteArray())

    fun createToken(username: String): String {
        val claims = Jwts.claims().setSubject(username)
        val issuedAt = Date()
        val expiration = Date(issuedAt.time + SecurityConstants.EXPIRATION_TIME)

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        }catch (e: JwtException){
            throw JwtAuthenticationException(HttpStatus.UNAUTHORIZED, SecurityConstants.JWT_INVALID_MESSAGE)
        }catch (e: IllegalArgumentException){
            throw JwtAuthenticationException(HttpStatus.UNAUTHORIZED, SecurityConstants.JWT_INVALID_MESSAGE)
        }
    }

    fun getAuthentication(token: String): Authentication? {
        val username = getUsername(token)
        val user = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(user,"", listOf())
    }

    fun resolveToken(request: HttpServletRequest):String? {
        return request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
    }

    fun getUsername(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }
}