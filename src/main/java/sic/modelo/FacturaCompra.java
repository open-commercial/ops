package sic.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaCompra extends Factura implements Serializable {

      private Long idProveedor;
      private String razonSocialProveedor;
      private LocalDateTime fechaAlta;
      
}
