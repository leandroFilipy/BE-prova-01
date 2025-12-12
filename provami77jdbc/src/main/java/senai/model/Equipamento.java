package senai.model;

public class Equipamento {
    private int id;
    private String nome;
    private String numeroSerie;
    private int fornecedorId;

    public Equipamento() {}

    public Equipamento(String nome, String numeroSerie, int fornecedorId) {
        this.nome = nome;
        this.numeroSerie = numeroSerie;
        this.fornecedorId = fornecedorId;
    }

    public Equipamento(int id, String nome, String numeroSerie, int fornecedorId) {
        this.id = id;
        this.nome = nome;
        this.numeroSerie = numeroSerie;
        this.fornecedorId = fornecedorId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }
}
