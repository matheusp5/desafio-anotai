package br.com.mxtheuz.anotaitecnico.services.interfaces;

public interface IJWTService {
    String createToken(String uuid);
    String decodeToken(String token);
}