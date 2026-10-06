package GestorBiblioteca.GestorBiblioteca.dto.request;
import lombok.Data;

@Data
public class RegistroRequestDTO {
    private String nombre;
    private String email;
    private String password;
}