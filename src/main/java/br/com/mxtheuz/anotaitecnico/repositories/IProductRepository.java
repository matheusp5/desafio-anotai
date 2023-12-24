package br.com.mxtheuz.anotaitecnico.repositories;

import br.com.mxtheuz.anotaitecnico.models.Product;
import br.com.mxtheuz.anotaitecnico.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, String> {
    List<Product> findByOwner(User owner);
}
