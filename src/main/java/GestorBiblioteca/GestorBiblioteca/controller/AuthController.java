package GestorBiblioteca.GestorBiblioteca.controller;

import GestorBiblioteca.GestorBiblioteca.dto.request.LoginRequestDTO;
import GestorBiblioteca.GestorBiblioteca.dto.request.RegistroRequestDTO;
import GestorBiblioteca.GestorBiblioteca.dto.response.AuthResponseDTO;
import GestorBiblioteca.GestorBiblioteca.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Endpoint: Registro de usuarios con rol predeterminado LECTOR
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegistroRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Endpoint: Autentica credenciales y retorna el token JWT[cite: 1]
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}