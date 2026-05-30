package com.demo.persistencia.demopersistencia.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import com.demo.persistencia.demopersistencia.entidades.Usuario;

public class JwtUtil {

    // ✅ CLAVE SEGURA (mínimo 32 caracteres)
    private static final Key SECRET = Keys.hmacShaKeyFor(
        "mi_clave_super_segura_12345678901234567890".getBytes()
    );

    // ✅ GENERAR TOKEN
public static String generarToken(Usuario usuario) {
    return Jwts.builder()
            .setSubject(usuario.getUsername())
            .claim("rol", usuario.getTipoUsuario()) 
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
}

    // ✅ OBTENER USERNAME
    public static String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ OBTENER ROL
    public static String getTipoUsuario(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tipo");
    }
    public static String getRol(String token) {
    return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .get("rol", String.class);
}
}