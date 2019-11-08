package sic.modelo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"fechaApertura", "sucursal"})
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCuentaCorriente", scope = CuentaCorriente.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CuentaCorrienteProveedor.class), 
  @JsonSubTypes.Type(value = CuentaCorrienteCliente.class) 
})
public abstract class CuentaCorriente implements Serializable {
    
    // bug: https://jira.spring.io/browse/DATAREST-304
    @JsonGetter(value = "type")
    public String getType() {
        return this.getClass().getSimpleName();
    }

    private Long idCuentaCorriente;    
    private boolean eliminada;    
    private LocalDateTime fechaApertura;
    private Sucursal sucursal;
    private BigDecimal saldo;
    private LocalDateTime fechaUltimoMovimiento;
    private List<RenglonCuentaCorriente> renglones;    
    
}
