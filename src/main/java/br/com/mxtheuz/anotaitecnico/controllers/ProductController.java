package br.com.mxtheuz.anotaitecnico.controllers;

import br.com.mxtheuz.anotaitecnico.dto.CreateProductDTO;
import br.com.mxtheuz.anotaitecnico.dto.Response;
import br.com.mxtheuz.anotaitecnico.dto.UpdateProductDTO;
import br.com.mxtheuz.anotaitecnico.models.Category;
import br.com.mxtheuz.anotaitecnico.models.Product;
import br.com.mxtheuz.anotaitecnico.models.User;
import br.com.mxtheuz.anotaitecnico.repositories.ICategoryRepository;
import br.com.mxtheuz.anotaitecnico.repositories.IProductRepository;
import br.com.mxtheuz.anotaitecnico.repositories.IUserRepository;
import br.com.mxtheuz.anotaitecnico.services.interfaces.IJWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/product")
public class ProductController {

    private final IJWTService _jwtService;
    private IUserRepository _userRepository;
    private ICategoryRepository _categoryRepository;
    private IProductRepository _productRepository;

    public ProductController(IJWTService jwtService, IUserRepository userRepository, ICategoryRepository categoryRepository, IProductRepository productRepository) {
        _jwtService = jwtService;
        _userRepository = userRepository;
        _categoryRepository = categoryRepository;
        _productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<Response> Create(@RequestBody CreateProductDTO dto, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Optional<Category> category = _categoryRepository.findById(dto.category());
        if(category.isEmpty()) {
            return ResponseEntity.status(404).body(new Response(404, "category was not found"));
        }

        if(!Objects.equals(category.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "category is not yours"));
        }

        Product product = Product.builder()
                .title(dto.title())
                .description(dto.description())
                .price(dto.price())
                .owner(user.get())
                .category(category.get())
                .build();

        var result = _productRepository.save(product);

        result.setOwner(null);
        result.setCategory(null);

        return ResponseEntity.status(201).body(new Response(201, result));

    }


    @GetMapping
    public ResponseEntity<Response> Find(@RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);

        if(user.isEmpty()) {
            var result = _productRepository.findAll().stream().map(x -> Product.builder()
                    .owner(User.builder().name(x.getOwner().getName()).build())
                    .category(Category.builder().id(x.getCategory().getId()).title(x.getCategory().getTitle()).description(x.getCategory().getDescription()).build())
                    .title(x.getTitle())
                    .description(x.getDescription())
                    .price(x.getPrice())
                    .build());

            return ResponseEntity.status(200).body(new Response(200, result));
        }

        var result = _productRepository.findByOwner(user.get()).stream().map(x -> Product.builder()
                .owner(null)
                .category(Category.builder().id(x.getCategory().getId()).title(x.getCategory().getTitle()).description(x.getCategory().getDescription()).build())
                .title(x.getTitle())
                .description(x.getDescription())
                .price(x.getPrice())
                .build());

        return ResponseEntity.status(200).body(new Response(200, result));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Response> Delete(@PathVariable("id") String id, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Optional<Product> product = _productRepository.findById(id);
        if(product.isEmpty()) {
            return ResponseEntity.status(404).body(new Response(404, "product was not found"));
        }

        if(!Objects.equals(product.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        _productRepository.delete(product.get());

        return ResponseEntity.status(200).body(new Response(200, null));
    }

    @PutMapping
    public ResponseEntity<Response> Update(@RequestBody UpdateProductDTO dto, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Optional<Product> product = _productRepository.findById(dto.id());
        if(product.isEmpty()) {
            return ResponseEntity.status(404).body(new Response(404, "product was not found"));
        }

        if(!Objects.equals(product.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "the product is not yours"));
        }

        product.get().setTitle(dto.title());
        product.get().setDescription(dto.description());
        product.get().setPrice(dto.price());

        Optional<Category> category = _categoryRepository.findById(dto.category());
        if(category.isEmpty()) {
            return ResponseEntity.status(404).body(new Response(404, "category was not found"));
        }
        if(!Objects.equals(category.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "the category is not yours"));
        }

        product.get().setCategory(category.get());
        var result = _productRepository.save(product.get());

        result.setOwner(null);
        result.setCategory(Category.builder().id(result.getCategory().getId()).title(result.getCategory().getTitle()).description(result.getCategory().getDescription()).build());

        return ResponseEntity.status(200).body(new Response(200, result));

    }
}
