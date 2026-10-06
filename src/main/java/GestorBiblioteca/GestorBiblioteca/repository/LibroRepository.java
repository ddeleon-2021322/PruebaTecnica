package GestorBiblioteca.GestorBiblioteca.repository;

import GestorBiblioteca.GestorBiblioteca.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Aqui se podran agregar metodos mas tarde
}