package br.com.mxtheuz.anotaitecnico.services.implementations;

import br.com.mxtheuz.anotaitecnico.services.interfaces.IJWTService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JWTService implements IJWTService {

    private Algorithm algorithm = Algorithm.HMAC256("5456342524562542785642364357825743725");
    private JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer("uuid")
            .build();
    @Override
    public String createToken(String uuid) {
        return JWT.create()
                .withIssuer("uuid")
                .withSubject("uuid")
                .withClaim("user", uuid)
                .withIssuedAt(new Date())
                .withJWTId(UUID.randomUUID()
                        .toString())
                .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
                .sign(algorithm);
    }

    @Override
    public String decodeToken(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            Claim claim = decodedJWT.getClaim("user");
            return claim.asString();
        } catch (Exception e) {
            return "";
        }
    }
}