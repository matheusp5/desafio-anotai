package br.com.mxtheuz.anotaitecnico.services.interfaces;

public interface IHashService {
    String hash(String password);
    boolean verify(String password, String hashed);
}