package GestorBiblioteca.GestorBiblioteca.repository;

import GestorBiblioteca.GestorBiblioteca.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Boot traduce este nombre de método automáticamente a:
    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);
}