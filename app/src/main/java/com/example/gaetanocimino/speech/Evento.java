package com.example.gaetanocimino.speech;

public class Evento {

    private String nomeEvento;
    private int giorno;
    private int mese;
    private int anno;

    public Evento(String n,int g,int m,int a){
        nomeEvento = n;
        giorno = g;
        mese = m;
        anno = a;
    }

    public int getAnno() {
        return anno;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public int getMese() {
        return mese;
    }

    public void setMese(int mese) {
        this.mese = mese;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }
}
