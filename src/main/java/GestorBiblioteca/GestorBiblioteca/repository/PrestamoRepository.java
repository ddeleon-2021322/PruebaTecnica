package GestorBiblioteca.GestorBiblioteca.repository;

import GestorBiblioteca.GestorBiblioteca.entity.EstadoPrestamo;
import GestorBiblioteca.GestorBiblioteca.entity.Prestamo;
import GestorBiblioteca.GestorBiblioteca.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // REGLA: Un LECTOR no puede tener más de 3 préstamos activos simultáneamente
    // Este método cuenta cuántos préstamos tiene un usuario en un estado específico (ACTIVO)
    long countByUsuarioAndEstado(Usuario usuario, EstadoPrestamo estado);

    // REGLA: Si no se devuelve en la fecha pactada, el usuario pasa a estado SANCIONADO automáticamente[cite: 1]
    // Verifica si el usuario tiene préstamos ACTIVOS donde la fecha esperada ya pasó
    boolean existsByUsuarioAndEstadoAndFechaDevolucionEsperadaBefore(Usuario usuario, EstadoPrestamo estado, LocalDate fecha);

    // ENDPOINT: Para que el lector consulte su historial propio de préstamos[cite: 1]
    List<Prestamo> findByUsuario(Usuario usuario);
}