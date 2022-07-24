package com.ducvt.news.account.security.jwt;

import java.util.Base64;
import java.util.Date;

import com.ducvt.news.account.payload.response.DecodeResponse;
import com.ducvt.news.account.security.services.UserDetailsImpl;
import com.ducvt.news.fw.exceptions.TokenExpiredException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${bezkoder.app.jwtSecret}")
  private String jwtSecret;

  @Value("${bezkoder.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
      throw new TokenExpiredException("token expired", "token expired");
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

//  public Jwt decode(String encodeJwt) {
//    Jwt jwt = Jwts.parser().parse(encodeJwt);
//    return jwt;
//  }

    public DecodeResponse decode(String encodeJwt) {
      String[] chunks = encodeJwt.split("\\.");
      Base64.Decoder decoder = Base64.getUrlDecoder();

      String header = new String(decoder.decode(chunks[0]));
      String payload = new String(decoder.decode(chunks[1]));
      JSONObject jsonObject = new JSONObject(payload);
      DecodeResponse decodeResponse = new DecodeResponse();
      decodeResponse.setEmail(jsonObject.getString("email"));
      decodeResponse.setName(jsonObject.getString("name"));
      return decodeResponse;
    }
}
