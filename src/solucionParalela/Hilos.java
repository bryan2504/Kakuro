package solucionParalela;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.StringValueExp;
import kakuro.Casilla;
import kakuro.Tablero;

/**
 *
 * @author Fabricio
 */
public class Hilos extends Thread{
    public static int maxHilos = 1000;
    private Thread t;
    Tablero tablero;
    private String nombreHilo;
    
    public Hilos(Tablero tablero, String nombreHilo) {
        this.nombreHilo = nombreHilo;
        this.tablero = tablero;
    }
    @Override
    public void run() {
        synchronized(tablero){
            tablero.resolverKakuro(tablero.getTablero());
        }
        //System.out.println("Terminando " + nombreHilo);
    }
    
    @Override
    public void start (){
        //System.out.println("Empezando " + nombreHilo);
        if (t == null){
            t = new Thread(this, String.valueOf(Thread.activeCount()));
            t.start();
        }
    }    
    
}
