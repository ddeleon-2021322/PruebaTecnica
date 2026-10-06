package GestorBiblioteca.GestorBiblioteca.dto.response;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PrestamoResponseDTO {
    private Long id;
    private String libroTitulo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private String estado;
}