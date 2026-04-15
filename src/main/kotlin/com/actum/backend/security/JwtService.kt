package com.actum.backend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {

    private val secret = "my_super_secret_key_123456"

    fun generateToken(login: String): String {
        return Jwts.builder()
            .setSubject(login)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun extractLogin(token: String): String {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
            .subject
    }
}