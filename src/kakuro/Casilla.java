package kakuro;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fabricio
 */
public class Casilla implements Serializable{
    private boolean vacia;
    private int valor;
    private int sumaDerecha;
    private int sumaAbajo;
    
    private String posibilidades;
    private ArrayList<Integer> probables = new ArrayList<>();
    
    private ArrayList<String> candFilas = new ArrayList<>();
    private ArrayList<String> candCol = new ArrayList<>();
    
    private ArrayList<Integer> candidatosFilas = new ArrayList<>();
    private ArrayList<Integer> candidatosCol = new ArrayList<>();
    
    private int fila;
    private int columna;

    public Casilla() {
        this.vacia = true;
        this.valor = 0;
        this.sumaDerecha = 0;
        this.sumaAbajo = 0;
    }

    public boolean isVacia() {
        return vacia;
    }

    public void setVacia(boolean vacia) {
        this.vacia = vacia;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getSumaDerecha() {
        return sumaDerecha;
    }

    public void setSumaDerecha(int sumaDerecha) {
        this.sumaDerecha = sumaDerecha;
    }

    public int getSumaAbajo() {
        return sumaAbajo;
    }

    public void setSumaAbajo(int sumaAbajo) {
        this.sumaAbajo = sumaAbajo;
    }

    /*Bryan*/

    public String getPosibilidades() {
        return posibilidades;
    }

    public void setPosibilidades(String posibilidades) {
        this.posibilidades = posibilidades;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public ArrayList<String> getCandFilas() {
        return candFilas;
    }

    public void setCandFilas(ArrayList<String> candFilas) {
        this.candFilas = candFilas;
    }

    public ArrayList<String> getCandCol() {
        return candCol;
    }

    public void setCandCol(ArrayList<String> candCol) {
        this.candCol = candCol;
    }

    public ArrayList<Integer> getProbables() {
        return probables;
    }

    public void setProbables(ArrayList<Integer> probables) {
        this.probables = probables;
    }
    
    @Override
    public String toString() {
        return "Casilla{" + ", vacia=" + vacia + ", valor=" + valor + ", sumaDerecha=" + sumaDerecha + ", sumaAbajo=" + sumaAbajo + ", posibilidades=" + posibilidades + ", candFilas="+candFilas+", candCol="+candCol+'}';
    }

    public void instertaCandidato(ArrayList<String> lista, char pos){ //'F' filas, 'C' columnas
        if (pos=='F')
            candFilas = lista;
        else
            candCol = lista;
    }
    
    public boolean buscarNum(int num){
        for (int i=0; i<posibilidades.length(); i++){
            if (posibilidades.substring(i, i+1).equals(String.valueOf(num)))
                return true;
        }return false;      
    }
}
