package solucionParalela;

import java.util.concurrent.RecursiveAction;
import kakuro.Casilla;
import kakuro.Tablero;

/**
 *
 * @author Bryan
 */
public class Forks extends RecursiveAction{    
    private Tablero tablero;
    private int fila;
    private int col;

    public Forks(Tablero tablero, int fila, int col) {
        this.tablero = tablero;
        this.fila = fila;
        this.col = col;
    }
    
    public Forks(Tablero tablero) {
        this.tablero = tablero;
        this.fila = 0;
        this.col = 0;
    }
    
    @Override
    public void compute() {
        if(fila==14 && col==14){
            System.out.println("fin");
            System.out.println(System.currentTimeMillis());
            return;
        }
        else if (fila==14 && col!=14){
            Forks f1 = new Forks(tablero, fila, col+1);
            f1.exec();
            f1.tablero.resolverKakuro(tablero.getTablero());
        }
        else if (fila!=14 && col==14){
            Forks f1 = new Forks(tablero, fila+1, col);
            f1.exec();
            f1.tablero.resolverKakuro(tablero.getTablero());

        }
        else{
            Forks f1 = new Forks(tablero, fila+1, col);
            f1.tablero.resolverKakuro(tablero.getTablero());
            Forks f2 = new Forks(tablero, fila, col+1);
            f2.tablero.resolverKakuro(tablero.getTablero());
            invokeAll(f1,f2);
        }
        tablero.resolverKakuro(tablero.getTablero());
    }
    
}