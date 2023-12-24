package br.com.mxtheuz.anotaitecnico.services.implementations;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.mxtheuz.anotaitecnico.services.interfaces.IHashService;
import org.springframework.stereotype.Service;

@Service
public class HashService implements IHashService {
    @Override
    public String hash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    @Override
    public boolean verify(String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }
}