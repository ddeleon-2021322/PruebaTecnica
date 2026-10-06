package GestorBiblioteca.GestorBiblioteca.service;

import GestorBiblioteca.GestorBiblioteca.dto.response.PrestamoResponseDTO;
import GestorBiblioteca.GestorBiblioteca.entity.*;
import GestorBiblioteca.GestorBiblioteca.exception.BusinessRuleException;
import GestorBiblioteca.GestorBiblioteca.exception.ResourceNotFoundException;
import GestorBiblioteca.GestorBiblioteca.repository.LibroRepository;
import GestorBiblioteca.GestorBiblioteca.repository.PrestamoRepository;
import GestorBiblioteca.GestorBiblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional // Garantiza la integridad de la base de datos bajo estrés
    public PrestamoResponseDTO registrarPrestamo(Long usuarioId, Long libroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Regla: Si no devuelve a tiempo, pasa a SANCIONADO al intentar nuevo préstamo
        verificarSanciones(usuario);

        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            throw new BusinessRuleException("El usuario está sancionado y no puede realizar préstamos");
        }

        // Regla: LECTOR no puede tener más de 3 préstamos activos simultáneamente[cite: 1]
        if (usuario.getRol() == RolUsuario.LECTOR) {
            long activos = prestamoRepository.countByUsuarioAndEstado(usuario, EstadoPrestamo.ACTIVO);
            if (activos >= 3) {
                throw new BusinessRuleException("El lector ya alcanzó el límite de 3 préstamos activos");
            }
        }

        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        // Regla: Un libro no puede prestarse si su stock disponible es 0[cite: 1]
        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException("No hay ejemplares disponibles de este libro");
        }

        // Descontar stock (Protegido por @Version contra concurrencia extrema)
        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        // Crear préstamo (Plazo de 14 días[cite: 1])
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(14));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapearADto(guardado);
    }

    private void verificarSanciones(Usuario usuario) {
        // Busca si tiene préstamos donde la fecha actual superó la esperada
        boolean tieneAtrasados = prestamoRepository.existsByUsuarioAndEstadoAndFechaDevolucionEsperadaBefore(
                usuario, EstadoPrestamo.ACTIVO, LocalDate.now());

        if(tieneAtrasados && usuario.getEstado() != EstadoUsuario.SANCIONADO) {
            usuario.setEstado(EstadoUsuario.SANCIONADO);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional // Control transaccional en el flujo de devolución[cite: 1]
    public PrestamoResponseDTO registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("Este libro ya fue devuelto anteriormente.");
        }

        // Registrar la entrega del libro y marca como DEVUELTO[cite: 1]
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        // Actualiza stock[cite: 1]
        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);
        libroRepository.save(libro);

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapearADto(guardado);
    }

    // Método utilitario para convertir Entidad a DTO
    private PrestamoResponseDTO mapearADto(Prestamo prestamo) {
        PrestamoResponseDTO dto = new PrestamoResponseDTO();
        dto.setId(prestamo.getId());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada());
        dto.setEstado(prestamo.getEstado().name());
        return dto;
    }
}