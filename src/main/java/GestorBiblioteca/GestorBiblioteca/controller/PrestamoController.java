package GestorBiblioteca.GestorBiblioteca.controller;

import GestorBiblioteca.GestorBiblioteca.dto.response.PrestamoResponseDTO;
import GestorBiblioteca.GestorBiblioteca.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    // Endpoint: Registrar la salida de un libro[cite: 1]
    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> registrarSalida(
            @RequestParam Long usuarioId,
            @RequestParam Long libroId) {
        return ResponseEntity.ok(prestamoService.registrarPrestamo(usuarioId, libroId));
    }

    // Endpoint: Registrar la entrega del libro[cite: 1]
    @PatchMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoResponseDTO> registrarEntrega(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id));
    }

    // (Opcional por ahora) Aquí irían los endpoints GET para mis-prestamos y atrasados[cite: 1]
}