package br.com.mxtheuz.anotaitecnico.repositories;

import br.com.mxtheuz.anotaitecnico.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
