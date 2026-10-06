package GestorBiblioteca.GestorBiblioteca.dto.request;
import lombok.Data;

@Data
public class LibroRequestDTO {
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private Integer stockTotal;
}