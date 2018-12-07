package kakuro;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.JTextField;
import solucionParalela.Forks;
import solucionParalela.Hilos;

/**
 *
 * @author Fabricio
 */
public class Kakuro {
    
    public static void main(String[] args) throws InterruptedException {
        Inicio ini = new Inicio();
        ini.setVisible(true);
        
        /*while(true){
            try{
                Tablero tab = new Tablero();  
                tab.generarTablero();
                tab.imprimirKakuro();
                Kakuro k = new Kakuro();
                k.resolverKakuroForks(tab.getTablero());
                break;
            } catch(java.lang.StackOverflowError e){
                System.out.println("no funcionó");
                continue;
            }

        }*/
        /*Tablero tab = new Tablero();  
        tab.generarTablero();
        tab.imprimirKakuro();
        Kakuro k = new Kakuro();
        k.resolverKakuroForks(tab.getTablero());*/

        //tab.setTablero(h.getSolucion());
        //tab.resolverKakuro(tab.getTablero());

    }
    
    public void resolverKakuroForks(Casilla[][] tablero){
        Casilla pos = new Casilla();
        pos = buscarVacia(tablero, pos);
        if (pos == null){
            return;
        }
        for (int num = 9; num>0; num--){
            if (esPrometedora(tablero, pos.getFila(), pos.getColumna(), num)){
                tablero[pos.getFila()][pos.getColumna()].setValor(num);
                Tablero tab = new Tablero(tablero);

                Forks f = new Forks(tab);
                f.invoke();
                f.compute();
                
                resolverKakuroForks(tablero);
                tablero[pos.getFila()][pos.getColumna()].setValor(0);
            }           
        }

    }
    
    public void resolverKakuroHilos(Casilla[][] tablero){
        Casilla pos = new Casilla();
        pos = buscarVacia(tablero, pos);
        if (pos == null){
            return;
        }
        for (int num = 9; num>0; num--){
            if (esPrometedora(tablero, pos.getFila(), pos.getColumna(), num)){
                tablero[pos.getFila()][pos.getColumna()].setValor(num);
                Tablero tab = new Tablero(tablero);
                //while (Thread.activeCount() >= maxHilos){
                //    System.out.println("...Esperando");
                //}
                Hilos h = new Hilos(tab, String.valueOf(Thread.activeCount()));
                h.start();
                tablero[pos.getFila()][pos.getColumna()].setValor(0);
            }           
        }
    }
      
    public void imprimirKakuro(Casilla[][] tablero){
        String fila = "";
        int i = 0,j = 0,vacias = 0,cont = 0;
        while (i <14){
            while (j <14){
                if (tablero[i][j].getValor() != 0){
                    fila += "[ " + tablero[i][j].getValor() + "  ]";
                    vacias++;
                    j++;
                }
                else if (tablero[i][j].isVacia()){
                    fila += "[    ]";
                    j++;
                }else{
                    if (tablero[i][j].getSumaAbajo() == 0 && tablero[i][j].getSumaDerecha() == 0){
                        fila += "[ 0  ]";
                        j++;
                    }else{
                        fila += "[" + tablero[i][j].getSumaAbajo() + "/" + tablero[i][j].getSumaDerecha() + "]";
                        j++;
                    }
                }
            }
         System.out.println(fila);
         fila = "";
         j = 0;
         i++;
        }
    }
    
    public Casilla buscarVacia(Casilla[][] tablero, Casilla vacia){
        for (int i=1; i<14; i++){
            for (int j=1; j<14; j++){
                if (tablero[i][j].isVacia()){
                    if (tablero[i][j].getValor() == 0){
                        vacia.setFila(i);
                        vacia.setColumna(j);
                        return vacia;
                    }
                }
            }
        }return null;
    }
    
    public boolean buscarFila(Casilla[][] tablero, int fila, int columna, int numero){
        int i = 1;
        int num = 0;
        while (i < columna){
            if (tablero[fila][columna-i].isVacia()){
                num = tablero[fila][columna-i].getValor();
                if (num != numero)
                    i++;
                else
                    return false; 
            }else
                i = columna;
        }i = 1;
        while (i < 14-columna){
            if (tablero[fila][columna+i].isVacia()){
                num = tablero[fila][columna+i].getValor();
                if (num != numero)
                    i++;
                else
                    return false; 
            }else
                i = 14;
        }return true;
    }
 
    public boolean buscarColumna(Casilla[][] tablero, int fila, int columna, int numero){
        int i = 1;
        int num = 0;
        while (i < fila){
            if (tablero[fila-i][columna].isVacia()){
                num = tablero[fila-i][columna].getValor();
                if (num != numero)
                    i++;
                else
                    return false; 
            }else
                i = fila;
        }i = 1;
        while (i < 14-fila){
            if (tablero[fila+i][columna].isVacia()){
                num = tablero[fila+i][columna].getValor();
                if (num != numero)
                    i++;
                else
                    return false; 
            }else
                i = 14;
        }return true;
    }
    
    public boolean esPrometedora(Casilla[][] tablero, int fila, int columna, int num){
        if (buscarFila(tablero, fila, columna, num) && buscarColumna(tablero, fila, columna, num)){
            //if (tablero[fila][columna].buscarNum(num)){
                if (validarFila(tablero, fila, columna, num) && validarColumna(tablero, fila, columna, num))
                    return true;              
            //}return false;
        }return false;
    }
    
    public boolean validarFila(Casilla[][] tablero, int fila, int columna, int num){
        int sumaDerecha = buscarSumaDerecha(tablero, fila, columna);
        int vaciasIzquierda = vaciasFila(tablero, fila, columna);
        int parcial = parcialFila(tablero, fila, columna);
        if (vaciasIzquierda == 0){
            if (num+parcial == sumaDerecha)
                return true;
            return false;
        }if (vaciasIzquierda == 1){
            if (sumaDerecha-(parcial+num) < 10)
                return true;
            return false;
        }
        if (num+parcial < sumaDerecha)
            return true;
        return false;       
    }
    
    public boolean validarColumna(Casilla[][] tablero, int fila, int columna, int num){
        int sumaAbajo = buscarSumaAbajo(tablero, fila, columna);
        int vaciasAbajo = vaciasColumna(tablero, fila, columna);
        int parcial = parcialColumna(tablero, fila, columna);
        if (vaciasAbajo == 0){
            if (num+parcial == sumaAbajo)
                return true;
            return false;
        }if (vaciasAbajo == 1){
            if (sumaAbajo-(parcial+num) < 10)
                return true;
            return false;
        }
        if (num+parcial < sumaAbajo)
            return true;
        return false;  
    }
    
    public int buscarSumaDerecha(Casilla[][] tablero, int fila, int columna){
        int ind = 1;
        while (tablero[fila][columna-ind].isVacia()){
            ind++;
        }return tablero[fila][columna-ind].getSumaDerecha();
    }
    
    public int buscarSumaAbajo(Casilla[][] tablero, int fila, int columna){
        int ind = 1;
        while (tablero[fila-ind][columna].isVacia()){
            ind++;
        }return tablero[fila-ind][columna].getSumaAbajo();
    }
    
    public int vaciasFila(Casilla[][] tablero,int fila, int columna){
        int sum = 0;
        int i = 1;
        while (i < 14-columna){
            if (tablero[fila][columna+i].isVacia()){
                sum++;
                i++;
            }else
                return sum;
        }return sum;
    }
    
    public int vaciasColumna(Casilla[][] tablero, int fila, int columna){
        int sum = 0;
        int i = 1;
        while (i < 14-fila){
            if (tablero[fila+i][columna].isVacia()){
                sum++;
                i++;
            }else
                return sum;
        }return sum;
    }
    
    public int parcialFila(Casilla[][] tablero, int fila, int columna){
        int sum = 0;
        int i = 1;
        while(tablero[fila][columna-i].isVacia()){
            sum += tablero[fila][columna-i].getValor();
            i++;
        }return sum;
    }
    
    public int parcialColumna(Casilla[][] tablero, int fila, int columna){
        int sum = 0;
        int i = 1;
        while(tablero[fila-i][columna].isVacia()){
            sum += tablero[fila-i][columna].getValor();
            i++;
        }return sum;
    }
      
}