package GestorBiblioteca.GestorBiblioteca.service;

import GestorBiblioteca.GestorBiblioteca.dto.request.LoginRequestDTO;
import GestorBiblioteca.GestorBiblioteca.dto.request.RegistroRequestDTO;
import GestorBiblioteca.GestorBiblioteca.dto.response.AuthResponseDTO;
import GestorBiblioteca.GestorBiblioteca.entity.EstadoUsuario;
import GestorBiblioteca.GestorBiblioteca.entity.RolUsuario;
import GestorBiblioteca.GestorBiblioteca.entity.Usuario;
import GestorBiblioteca.GestorBiblioteca.exception.ResourceNotFoundException;
import GestorBiblioteca.GestorBiblioteca.repository.UsuarioRepository;
import GestorBiblioteca.GestorBiblioteca.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegistroRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        // Manejo adecuado de contraseñas con PasswordEncoder (BCrypt)[cite: 1]
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Registro de usuarios con rol predeterminado LECTOR[cite: 1]
        usuario.setRol(RolUsuario.LECTOR);
        usuario.setEstado(EstadoUsuario.ACTIVO);

        usuarioRepository.save(usuario);

        // Genera el token una vez registrado
        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponseDTO(jwtToken);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // Autentica credenciales y retorna el token JWT[cite: 1]
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponseDTO(jwtToken);
    }
}