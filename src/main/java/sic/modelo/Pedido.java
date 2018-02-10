package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPedido", "nombreEmpresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Pedido", scope = Pedido.class)
public class Pedido implements Serializable {
    
    private long id_Pedido;
    private long nroPedido;    
    private Date fecha;    
    private Date fechaVencimiento;    
    private String observaciones;  
    private String nombreEmpresa;
    private boolean eliminado;    
    private String razonSocialCliente;  
    private String usuario;
    private List<Factura> facturas;        
    private List<RenglonPedido> renglones;
    private double totalEstimado;
    private double totalActual;
    private EstadoPedido estado;
}
