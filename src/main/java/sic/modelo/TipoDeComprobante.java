package sic.modelo;

/**
 * Describe los distintos tipos de comprobante.
 */

public enum TipoDeComprobante {
    
    FACTURA_A("Factura A"),
    
    FACTURA_B("Factura B"),
    
    FACTURA_C("Factura C"),
    
    FACTURA_X("Factura X"),
    
    FACTURA_Y("Factura Y"),
    
    PEDIDO("Pedido"), 
    
    PRESUPUESTO("Presupuesto"),
    
    NOTA_CREDITO_A("Nota de Credito A"),
    
    NOTA_CREDITO_B("Nota de Credito B"),
    
    NOTA_CREDITO_X("Nota de Credito X"), 
    
    NOTA_DEBITO_A("Nota de Debito A"),
    
    NOTA_DEBITO_B("Nota de Debito B"),
    
    NOTA_DEBITO_X("Nota de Debito X");
    
    private final String text;

    private TipoDeComprobante(final String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
