package GestorBiblioteca.GestorBiblioteca.controller;

import GestorBiblioteca.GestorBiblioteca.dto.request.LibroRequestDTO;
import GestorBiblioteca.GestorBiblioteca.entity.Libro;
import GestorBiblioteca.GestorBiblioteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    // Endpoint: Lista libros[cite: 1]
    @GetMapping
    public ResponseEntity<List<Libro>> listar() {
        return ResponseEntity.ok(libroService.listarLibros());
    }

    // Endpoint: Detalle completo de un libro específico[cite: 1]
    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    // Endpoint: Registrar un nuevo libro en el catálogo[cite: 1]
    @PostMapping
    public ResponseEntity<Libro> registrar(@RequestBody LibroRequestDTO request) {
        return ResponseEntity.ok(libroService.registrarLibro(request));
    }

    // Endpoint: Actualizar datos o stock de un libro[cite: 1]
    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizar(@PathVariable Long id, @RequestBody LibroRequestDTO request) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, request));
    }

    // Endpoint: Eliminación lógica/física de un libro[cite: 1]
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}