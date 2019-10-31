package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPedido", "nombreEmpresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Pedido", scope = Pedido.class)
public class Pedido implements Serializable {
    
    private long id_Pedido;
    private long nroPedido;    
    private LocalDateTime fecha;    
    private LocalDate fechaVencimiento;    
    private String observaciones;  
    private String nombreEmpresa;
    private boolean eliminado;    
    private String nombreFiscalCliente;  
    private String nombreUsuario;
    private Long idViajante;
    private String nombreViajante;
    private List<Factura> facturas;        
    private List<RenglonPedido> renglones;
    private BigDecimal subTotal;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal totalEstimado;
    private BigDecimal totalActual;
    private EstadoPedido estado;
}
