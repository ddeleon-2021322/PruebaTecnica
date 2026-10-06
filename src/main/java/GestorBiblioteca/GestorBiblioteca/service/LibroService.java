package GestorBiblioteca.GestorBiblioteca.service;

import GestorBiblioteca.GestorBiblioteca.dto.request.LibroRequestDTO;
import GestorBiblioteca.GestorBiblioteca.entity.Libro;
import GestorBiblioteca.GestorBiblioteca.exception.ResourceNotFoundException;
import GestorBiblioteca.GestorBiblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public Libro obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
    }

    @Transactional
    public Libro registrarLibro(LibroRequestDTO request) {
        Libro libro = new Libro();
        libro.setIsbn(request.getIsbn());
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setCategoria(request.getCategoria());
        libro.setStockTotal(request.getStockTotal());
        libro.setStockDisponible(request.getStockTotal()); // Al inicio, el disponible es igual al total

        return libroRepository.save(libro);
    }

    @Transactional
    public Libro actualizarLibro(Long id, LibroRequestDTO request) {
        Libro libro = obtenerPorId(id);
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setCategoria(request.getCategoria());

        // Cuidado con el stock al actualizar
        int diferenciaStock = request.getStockTotal() - libro.getStockTotal();
        libro.setStockTotal(request.getStockTotal());
        libro.setStockDisponible(libro.getStockDisponible() + diferenciaStock);

        return libroRepository.save(libro);
    }

    @Transactional
    public void eliminarLibro(Long id) {
        Libro libro = obtenerPorId(id);
        // Eliminación física[cite: 1]
        libroRepository.delete(libro);
    }
}