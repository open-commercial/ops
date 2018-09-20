package sic.modelo;

public enum CategoriaIVA {
    RESPONSABLE_INSCRIPTO("Responsable Inscripto"),
    EXENTO("Exento"),
    CONSUMIDOR_FINAL("Consumidor Final"),
    MONOTRIBUTO("Monotributo");
    
    private final String text;

    private CategoriaIVA(final String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
