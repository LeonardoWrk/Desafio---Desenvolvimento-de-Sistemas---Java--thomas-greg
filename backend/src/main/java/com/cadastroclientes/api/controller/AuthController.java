package com.cadastroclientes.api.controller;

import com.cadastroclientes.api.model.Usuario;
import com.cadastroclientes.api.payload.JwtAuthenticationResponse;
import com.cadastroclientes.api.payload.LoginRequest;
import com.cadastroclientes.api.payload.SignUpRequest;
import com.cadastroclientes.api.repository.UsuarioRepository;
import com.cadastroclientes.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Chegou no método authenticateUser");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, usuario.getId(), usuario.getUsername()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Nome de usuário já está em uso!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email já está em uso!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Criando a conta do usuário
        Usuario usuario = new Usuario(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            usuario.setRoles(Collections.singleton("ROLE_USER"));
        } else {
            usuario.setRoles(signUpRequest.getRoles());
        }

        Usuario result = usuarioRepository.save(usuario);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/usuarios/{id}")
                .buildAndExpand(result.getId()).toUri();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuário registrado com sucesso!");
        return ResponseEntity.created(location).body(response);
    }
}
