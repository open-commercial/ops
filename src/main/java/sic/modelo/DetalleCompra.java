package sic.modelo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleCompra {

    private LocalDateTime fecha;
    private Long serie;
    private Long nroNota;
    private Long CAE;
}
