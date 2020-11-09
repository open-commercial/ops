package sic.modelo;

public enum TipoDeComprobante {    
    FACTURA_A("Factura \"A\""),    
    FACTURA_B("Factura \"B\""),    
    FACTURA_C("Factura \"C\""),    
    FACTURA_X("Factura \"X\""),    
    FACTURA_Y("Factura \"Y\""),    
    PEDIDO("Pedido"),     
    PRESUPUESTO("Presupuesto"),    
    RECIBO("Recibo"),    
    GASTO("Gasto"),        
    NOTA_CREDITO_A("Nota de Credito \"A\""),    
    NOTA_CREDITO_B("Nota de Credito \"B\""),    
    NOTA_CREDITO_C("Nota de Credito \"C\""),
    NOTA_CREDITO_X("Nota de Credito \"X\""),    
    NOTA_CREDITO_Y("Nota de Credito \"Y\""),    
    NOTA_CREDITO_PRESUPUESTO("Nota de Credito \"P\""),    
    NOTA_DEBITO_A("Nota de Debito \"A\""),    
    NOTA_DEBITO_B("Nota de Debito \"B\""),    
    NOTA_DEBITO_C("Nota de Debito \"C\""), 
    NOTA_DEBITO_PRESUPUESTO("Nota de Debito \"P\""),    
    NOTA_DEBITO_Y("Nota de Debito \"Y\""),    
    NOTA_DEBITO_X("Nota de Debito \"X\""),
    REMITO("Remito");

    private final String text;

    private TipoDeComprobante(final String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
