package GestorBiblioteca.GestorBiblioteca.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Data
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; //[cite: 1]

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id")
    private Libro libro; //[cite: 1]

    private LocalDate fechaPrestamo; //[cite: 1]
    private LocalDate fechaDevolucionEsperada; //[cite: 1]
    private LocalDate fechaDevolucionReal; //[cite: 1]

    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado; // ACTIVO, DEVUELTO, ATRASADO[cite: 1]
}