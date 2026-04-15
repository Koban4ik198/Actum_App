package com.actum.backend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    private val secretString = "my_super_secret_key_123456_my_super_secret_key_123456"
    private val secretKey: SecretKey =
        Keys.hmacShaKeyFor(secretString.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(login: String): String {
        return Jwts.builder()
            .setSubject(login)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractLogin(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}