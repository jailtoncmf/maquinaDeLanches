package br.com.bda.machinesnack.itens;

public class SnackItem {

    private int id;  
    private String marca;
    private String produto;
    private int estoque;
    private double preco;  

    
    public SnackItem(int id, String marca, String produto, int estoque, double preco) {
        this.id = id;
        this.marca = marca;
        this.produto = produto;
        this.estoque = estoque;
        this.preco = preco;
    }
    public SnackItem(String produto, double preco) {
        this.produto = produto;
        this.preco = preco;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
   
}
