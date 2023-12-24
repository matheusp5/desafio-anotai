package br.com.mxtheuz.anotaitecnico.controllers;

import br.com.mxtheuz.anotaitecnico.dto.LoginDTO;
import br.com.mxtheuz.anotaitecnico.dto.Response;
import br.com.mxtheuz.anotaitecnico.models.User;
import br.com.mxtheuz.anotaitecnico.repositories.IUserRepository;
import br.com.mxtheuz.anotaitecnico.services.interfaces.IHashService;
import br.com.mxtheuz.anotaitecnico.services.interfaces.IJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private IUserRepository _userRepository;
    private IHashService _hashService;
    private IJWTService _jwtService;

    @Autowired
    public AuthController(IUserRepository userRepository, IHashService hashService, IJWTService jwtService) {
        _userRepository = userRepository;
        _hashService = hashService;
        _jwtService = jwtService;
    }


    @PostMapping("register")
    public ResponseEntity<Response> Register(@RequestBody User dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(_hashService.hash(dto.getPassword()))
                .build();


        User result = _userRepository.save(user);

        return ResponseEntity.status(201).body(new Response(201, result));
    }

    @PostMapping("login")
    public ResponseEntity<Response> Login(@RequestBody LoginDTO dto) {
        Optional<User> user = _userRepository.findByEmail(dto.email());
        if(user.isEmpty()) {
            return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
        }
        if(_hashService.verify(dto.password(), user.get().getPassword())) {
            return ResponseEntity.status(200).body(new Response(200, _jwtService.createToken(user.get().getId())));
        }
        return ResponseEntity.status(401).body(new Response(401, "unauthorized"));
    }

}