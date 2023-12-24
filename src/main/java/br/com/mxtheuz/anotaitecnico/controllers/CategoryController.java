package br.com.mxtheuz.anotaitecnico.controllers;

import br.com.mxtheuz.anotaitecnico.dto.CreateCategoryDTO;
import br.com.mxtheuz.anotaitecnico.dto.Response;
import br.com.mxtheuz.anotaitecnico.dto.UpdateCategoryDTO;
import br.com.mxtheuz.anotaitecnico.models.Category;
import br.com.mxtheuz.anotaitecnico.models.User;
import br.com.mxtheuz.anotaitecnico.repositories.ICategoryRepository;
import br.com.mxtheuz.anotaitecnico.repositories.IUserRepository;
import br.com.mxtheuz.anotaitecnico.services.interfaces.IJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/category")
public class CategoryController {
    private IJWTService _jwtService;
    private ICategoryRepository _categoryRepository;
    private IUserRepository _userRepository;

    @Autowired
    public CategoryController(IJWTService jwtService, ICategoryRepository categoryRepository, IUserRepository userRepository) {
        _jwtService = jwtService;
        _categoryRepository = categoryRepository;
        _userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Response> Create(@RequestBody CreateCategoryDTO dto, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Category category = Category.builder()
                .owner(user.get())
                .title(dto.title())
                .description(dto.description())
                .build();

        var result = _categoryRepository.save(category);
        result.setOwner(null);
        return ResponseEntity.status(201).body(new Response(201, result));
    }

    @GetMapping
    public ResponseEntity<Response> Find(@RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        var result = _categoryRepository.findByOwner(user.get()).stream().map(x -> {
            return Category.builder()
                    .owner(null)
                    .title(x.getTitle())
                    .id(x.getId())
                    .description(x.getDescription())
                    .build();
        });;

        return ResponseEntity.status(200).body(new Response(200, result));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Response> Delete(@PathVariable("id") String id, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Optional<Category> category = _categoryRepository.findById(id);
        if(category.isEmpty()) {
            return ResponseEntity.status(404).body(new Response(404, "category was not found"));
        }

        if(!Objects.equals(category.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        _categoryRepository.delete(category.get());

        return ResponseEntity.status(200).body(new Response(200, null));
    }

    @PutMapping
    public ResponseEntity<Response> Update(@RequestBody UpdateCategoryDTO dto, @RequestHeader("Authorization") String bearer) {
        String userId = _jwtService.decodeToken(bearer.replace("Bearer ", ""));
        Optional<User> user = _userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        Optional<Category> category = _categoryRepository.findById(dto.id());
        if(category.isEmpty()){
            return ResponseEntity.status(404).body(new Response(404, "category was not found"));
        }

        if(!Objects.equals(category.get().getOwner().getId(), userId)) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }

        category.get().setDescription(dto.description());
        category.get().setTitle(dto.title());

        var result = _categoryRepository.save(category.get());
        result.setOwner(null);

        return ResponseEntity.status(200).body(new Response(200, result));

    }
}
