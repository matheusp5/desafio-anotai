package br.com.mxtheuz.anotaitecnico.repositories;

import br.com.mxtheuz.anotaitecnico.models.Category;
import br.com.mxtheuz.anotaitecnico.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByOwner(User owner);
}
