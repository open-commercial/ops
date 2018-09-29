package sic.modelo;

public enum TipoDeCliente {
    EMPRESA("Empresa"),
    PERSONA("Persona");

    private final String text;

    private TipoDeCliente(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
