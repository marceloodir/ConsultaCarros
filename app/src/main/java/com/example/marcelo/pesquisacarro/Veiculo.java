package com.example.marcelo.pesquisacarro;

/**
 * Created by marcelo on 20/08/15.
 */
public class Veiculo {

    private String placa;
    private String renavam;

    public Veiculo(String placa, String renavam) {
        this.placa = placa;
        this.renavam = renavam;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getRenavam() {
        return renavam;
    }

    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }
}
