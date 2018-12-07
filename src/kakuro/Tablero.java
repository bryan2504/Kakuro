package kakuro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;
import kakuro.Casilla;

/**
 *
 * @author Fabricio
 */
public class Tablero implements Serializable {
    private Casilla[][] tablero; //matriz de Clase Casilla
    private int[] sumaCasillas; //sumas para cada cantidad de casillas
    private long tiempo;
    ArrayList<Casilla[][]> listaTableros;
    
    public Tablero() {
        tablero = new Casilla[14][14];
        sumaCasillas = new int[9];
        for (int i=1; i<10; i++)
            sumaCasillas[i-1]= i;
        listaTableros = new ArrayList<>();
    }

    public Tablero(Casilla[][] tablero) {
        this.tablero = tablero;
    }

    public Casilla[][] getTablero() {
        return tablero;
    }

    public void setTablero(Casilla[][] tablero) {
        this.tablero = tablero;
    }

    @Override
    public String toString() {
        return "Tablero{" + "tablero=" + tablero + '}';
    }
    
    public void generarTablero(){
        for (int i = 0; i<14; i++){
            for (int j = 0; j<14; j++)
                tablero[i][j] = new Casilla();         
        }
        for (int i = 0; i<14; i++){
            tablero[i][0].setVacia(false);
            tablero[0][i].setVacia(false);
        }
        insertarCasillas();
        generarNumeros();
        colocarSumas();
        borrarNumeros();
        //candidatosCasillas();
    }
    
    public void insertarCasillas(){
        //inserta casillas nulas aleatorias, hasta tener una solución prometedora
        if (verificarTablero())
            return;
        else{
            int fila;
            int columna;
            Random rand = new Random();
            fila = rand.nextInt(13)+1;
            columna = rand.nextInt(13)+1;
            if (fila == 1  || fila == 13){
                insertarHorizontal(fila, columna);
                insertarCasillas();
            }else if (columna == 1 || columna == 13){
                insertarVertical(fila, columna);
                insertarCasillas();
            }else if (fila == 2 || fila == 12 || columna == 2 || columna == 12){
                insertarArribaIzq(fila, columna);
                insertarCasillas();
            }else{
                insertarNula(fila, columna);
                insertarCasillas();                
            }
        }return;
    }
    
    public boolean verificarTablero(){
        //verifica si el tablero actual es prometedor
        int i = 0;
        int j = 0;
        while (i<14){
            while (j<14){
                if (tablero[i][j].isVacia())
                    j++;
                else{
                    if (vaciasFila(i, j) > 9 || vaciasColumna(i, j) > 9)
                        return false;
                    else
                        j++;
                }
            }
            j=0;
            i++;
        }
        return true;
    }
    
    public void generarNumeros(){
        //genera numeros aleatorios en todas las casillas tomando en cuenta las restricciones
        Random random = new Random();
        int num;
        int ind;
        for (int i=0; i<6; i++){
            for (int j=1; j<14; j++){
                if (tablero[i][j].isVacia()){
                    if (tablero[i][j].getValor() == 0){
                        ind = 0;
                        while (ind < 9){
                            num = random.nextInt(9)+1;
                            if (buscarColumna(i, j, num) && buscarFila(i, j, num)){
                                tablero[i][j].setValor(num);
                                break;
                            }ind++;
                        }if (tablero[i][j].getValor() == 0)
                            generarNumeros();
                    }                                         
                }
            }
        }
        for (int i=6; i<14; i++){
            for (int j=1; j<14; j++){
                if (tablero[i][j].isVacia()){
                    if (tablero[i][j].getValor() == 0){
                        for (int val=9; val>0; val--){
                            if (buscarColumna(i, j, val) && buscarFila(i, j, val)){
                                tablero[i][j].setValor(val);
                                break;
                            }
                        }if (tablero[i][j].getValor() == 0)
                            generarNumeros();
                    }                                         
                }
            }
        }
    }
    
    public void borrarNumeros(){
        int num;
        int ind;
        for (int i=1; i<14; i++){
            for (int j=1; j<14; j++){
                if (tablero[i][j].isVacia()){
                    if (tablero[i][j].getValor() != 0)
                        tablero[i][j].setValor(0);
                }
            }
        }
    }
  
    public void colocarSumas(){
        //coloca las sumas en las casillas correspondientes
        for (int i=0; i<14; i++){
            for (int j=0; j<14; j++){
                if (!tablero[i][j].isVacia()){
                    colocarSumaDerecha(i, j);
                    colocarSumaAbajo(i, j);
                }
            }
        }
    }
    
    public void colocarSumaDerecha(int fila, int columna){
        int sum = 0;
        int i = 1;
        while (i < 14-columna){
            if (!tablero[fila][columna+i].isVacia()){
                tablero[fila][columna].setSumaDerecha(sum);
                return;
            }else{
                sum += tablero[fila][columna+i].getValor();
                i++;
            }
        }tablero[fila][columna].setSumaDerecha(sum);
    }
    
    public void colocarSumaAbajo(int fila, int columna){
        int sum = 0;
        int i = 1;
        while (i < 14-fila){
            if (tablero[fila+i][columna].getValor() != 0){
                sum += tablero[fila+i][columna].getValor();
                i++;
            }else{
                tablero[fila][columna].setSumaAbajo(sum);
                return;
            }
        }tablero[fila][columna].setSumaAbajo(sum);
    }
    
    public boolean buscarFila(int fila, int columna, int numero){
        int j = 1;
        int num = 0;
        while (j < columna){
            if (tablero[fila][columna-j].isVacia()){
                num = tablero[fila][columna-j].getValor();
                if (num != numero)
                    j++;
                else
                    return false; 
            }else
                j = columna;
        }j = 1;
        while (j < 14-columna){
            if (tablero[fila][columna+j].isVacia()){
                num = tablero[fila][columna+j].getValor();
                if (num != numero)
                    j++;
                else
                    return false; 
            }else
                j = 14;
        }return true;
    }
 
    public boolean buscarColumna(int fila, int columna, int numero){
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
    
    public int vaciasFila(int fila, int columna){
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
    
    public int vaciasColumna(int fila, int columna){
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
    
    public void insertarHorizontal(int fila, int columna){
        //se encarga de insetar nula, si es posible, cuando la fila es 1 o 13 
        if (fila == 1){
            if(tablero[fila+2][columna].isVacia()){
                if(columna == 1){
                    if(tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 2){
                    if (!tablero[fila][columna-1].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 12){
                    if(!tablero[fila][columna+1].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 13){
                    if(tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if(tablero[fila][columna+2].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                    }
                } 
            }else
                return;              
        }else{
            if(tablero[fila-2][columna].isVacia()){
                if(columna == 1){
                    if(tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 2){
                    if (!tablero[fila][columna-1].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 12){
                    if(!tablero[fila][columna+1].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(columna == 13){
                    if(tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if(tablero[fila][columna+2].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                    }
                } 
            }else
                return;
        }
    }
    
    public void insertarVertical(int fila, int columna){
        //se encarga de insertar, si es posible, cuando columna es 1 o 13
        if (columna == 1){
            if(tablero[fila][columna+2].isVacia()){
                if(fila == 1){
                    if(tablero[fila+2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 2){
                    if (!tablero[fila-1][columna].isVacia() && tablero[fila+2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 12){
                    if(!tablero[fila+1][columna].isVacia() && tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 13){
                    if(tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if(tablero[fila+2][columna].isVacia() && tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                    }
                } 
            }else
                return;              
        }else{
            if(tablero[fila][columna-2].isVacia()){
                if(fila == 1){
                    if(tablero[fila+2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 2){
                    if (!tablero[fila-1][columna].isVacia() && tablero[fila+2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 12){
                    if(!tablero[fila+1][columna].isVacia() && tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if(fila == 13){
                    if(tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if(tablero[fila+2][columna].isVacia() && tablero[fila-2][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                    }
                } 
            }else
                return;
        }
    }
    
    public void insertarNula(int fila, int columna){
        //inserta nula, si es posible, cuando la columna o fila va de 3 - 11
        if (tablero[fila-2][columna].isVacia() && tablero[fila+2][columna].isVacia() &&
            tablero[fila][columna-2].isVacia() && tablero[fila][columna+2].isVacia()){
            tablero[fila][columna].setVacia(false);
            return;
        }return;
    }
    
    public void insertarArribaIzq(int fila, int columna){
        //insetar si es posible casilla nula, cuando la columna o fila es 2 0 12
        if (fila == 2){
            insertarHorizontal(fila-1, columna);
            if (!tablero[fila-1][columna].isVacia()){
                if (columna == 1){
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if (columna == 2){
                    insertarVertical(fila, columna-1);
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna+2].isVacia() &&
                        !tablero[fila][columna-1].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna-1].setVacia(false);
                        return;
                    }return;
                }else if (columna == 12){
                    insertarVertical(fila, columna+1);
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna-2].isVacia() &&
                        !tablero[fila][columna+1].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna+1].setVacia(false);
                        return;
                    }return;
                }else if (columna == 13){
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if (tablero[fila][columna+2].isVacia() && tablero[fila+2][columna].isVacia() && 
                        tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }
            }return;
        }else if (fila == 12){
            insertarHorizontal(fila+1, columna);
            if (!tablero[fila+1][columna].isVacia()){
                if (columna == 1){
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if (columna == 2){
                    insertarVertical(fila, columna-1);
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna+2].isVacia() &&
                        !tablero[fila][columna-1].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna-1].setVacia(false);
                        return;
                    }return;
                }else if (columna == 12){
                    insertarVertical(fila, columna+1);
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna-2].isVacia() &&
                        !tablero[fila][columna+1].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna+1].setVacia(false);
                        return;
                    }return;
                }else if (columna == 13){
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if (tablero[fila][columna+2].isVacia() && tablero[fila-2][columna].isVacia() && 
                        tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }
            }return;  
        }else if (columna == 2){
            insertarVertical(fila, columna-1);
            if (!tablero[fila][columna-1].isVacia()){
                if (fila == 1){
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if (fila == 2){
                    insertarHorizontal(fila-1, columna);
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna+2].isVacia() &&
                        !tablero[fila-1][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna-1].setVacia(false);
                        return;
                    }return;
                }else if (fila == 12){
                    insertarHorizontal(fila+1, columna);
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna+2].isVacia() &&
                        !tablero[fila+1][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna+1].setVacia(false);
                        return;
                    }return;
                }else if (fila == 13){
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if (tablero[fila-2][columna].isVacia() && tablero[fila+2][columna].isVacia() && 
                        tablero[fila][columna+2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }
            }return;
        }else{
            insertarVertical(fila, columna+1);
            if (!tablero[fila][columna+1].isVacia()){
                if (fila == 1){
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else if (fila == 2){
                    insertarHorizontal(fila-1, columna);
                    if (tablero[fila+2][columna].isVacia() && tablero[fila][columna-2].isVacia() &&
                        !tablero[fila-1][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila-1][columna+1].setVacia(false);
                        return;
                    }return;
                }else if (fila == 12){
                    insertarHorizontal(fila+1, columna);
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna-2].isVacia() &&
                        !tablero[fila+1][columna].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        tablero[fila+1][columna+1].setVacia(false);
                        return;
                    }return;
                }else if (fila == 13){
                    if (tablero[fila-2][columna].isVacia() && tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }else{
                    if (tablero[fila+2][columna].isVacia() && tablero[fila-2][columna].isVacia() && 
                        tablero[fila][columna-2].isVacia()){
                        tablero[fila][columna].setVacia(false);
                        return;
                    }return;
                }
            }return;
        }
    }
    
    public void imprimirKakuro(){
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
    
    public boolean resolverKakuro(Casilla[][] tablero){
        Casilla pos = new Casilla();
        pos = buscarVacia(tablero, pos);
        if (pos == null){
            imprimirKakuro(tablero);
            return true;
        }
        for (int num = 9; num>0; num--){
            if (esPrometedora(tablero, pos.getFila(), pos.getColumna(), num)){
                tablero[pos.getFila()][pos.getColumna()].setValor(num);
                //imprimirKakuro(tablero);
                if (resolverKakuro(tablero)){
                    return true;
                }
                tablero[pos.getFila()][pos.getColumna()].setValor(0);
            }           
        }
        return false;
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
    
    
    
    /*desde aqui tocó Bryan*/
    
/*  Para insetarCandidatos:
    *lista contiene strings con posibles candidatos
    *fila y col determinan la posicion de la casila desde donde inicia(la que contiene la suma)
    *suma es lo que deberían de sumar todos los elementos
    *cantidad de casillas
    *pos: la orientación de la casilla: filas('F') o columnas('C')*/


    public void candidatosCasillas(){
        for (int i = 0; i<14; i++){
            for (int j = 0; j<14; j++){
                if (tablero[i][j].isVacia()==false){
                    if(tablero[i][j].getSumaDerecha()!=0){
                        insertaCandidatos(i, j, tablero[i][j].getSumaDerecha(), cantCasillas(i, j, 'F'), 'F');
                        String elem = todosLosCandidatos(tablero[i][j].getCandFilas());
                        insertarPosibilidadesCasilla(i, j, 'F', elem);
                        //recorre la vara hasta encontrarse con otro cero o hasta el 14
                    }
                    if(tablero[i][j].getSumaAbajo()!=0){
                        insertaCandidatos(i, j, tablero[i][j].getSumaAbajo(), cantCasillas(i, j, 'C'), 'C');
                        String elem = todosLosCandidatos(tablero[i][j].getCandCol());
                        insertarPosibilidadesCasilla(i, j, 'C', elem);
                        
                    }
                }
            }
        }
    }

    public void insertaCandidatos(int fila, int col, int suma, int cantCasillas, char pos){
        String digitosDisp = buscaSumandos(suma);
        ArrayList<String> lista = new ArrayList<>();  
        lista = permutacion("", digitosDisp, lista);
        lista = candidatos(lista, cantCasillas);
        lista=elimCandidatos(lista, suma);
        
        tablero[fila][col].instertaCandidato(lista, pos);//'F' filas, 'C' columnas;
    }
    private ArrayList<String> permutacion(String result, String str,ArrayList<String> lista) { //lista
        int n = str.length();
        if (n == 0){
            lista.add(result);
//            System.out.println(prefix);
        }
        else {
            for (int i = 0; i < n; i++)
                permutacion(result + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),lista);
        }
//        System.out.println(lista.toString());
        return lista;
    }
    public String buscaSumandos(int num){//busca los posibles sumando para una suma
        String result = new String();
        for(int e = 1; e<10; e++){
            if(e<=num){
                result=result + Integer.toString(e);
            }
        }
        return result;
    }
    private ArrayList<String> candidatos(ArrayList<String> lista, int tamanno){
        ArrayList<String> result = new ArrayList<>();
        if (lista.size() == tamanno){
            return result;
        }
        ArrayList<String> resultF = new ArrayList<>();
        for(String e : lista){
            resultF.add(e.substring(e.length()-tamanno, e.length()));
        }
        return resultF;//falta validar repetidos
    }
    public int sumaDigitos(String string){
        int suma = 0;
        for (int i=0;i<=string.length()-1; i++){
            suma+=Integer.valueOf(string.substring(i,i+1));
        }
        return suma;
    }
    public ArrayList<String> elimCandidatos(ArrayList<String> lista, int suma){
        ArrayList<String> temp = new ArrayList<>();
        for (String e : lista){
            if (sumaDigitos(e)==suma){
                temp.add(e);
            }
        }
        lista.clear();
        for(String e : temp){
            if (lista.indexOf(e)==-1){
                lista.add(e);
            }
        }
        return lista;        
    }
    
    public String todosLosCandidatos(ArrayList<String> lista){
        String result=new String();
        for (String e : lista){
            for(int i = 0; i<e.length(); i++){
                if(!result.contains(e.substring(i, i+1))){
                    result= result + e.substring(i, i+1);
                }
            }
        }
        char[] chars = result.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);
        return sorted;
    }
    public int cantCasillas(int fil, int col, char orient){
        int result=0;
        if (orient == 'C'){
            fil++;
            while(fil<14 && tablero[fil][col].isVacia()==true){
                result++;
                fil++;
            }
        }
        else if (orient == 'F'){
            col++;
            while(col<14 && tablero[fil][col].isVacia()==true){
                result++;
                col++;
            }
        }
        return result;
    }
    public void insertarPosibilidadesCasilla(int fil, int col, char orient,String elem){
        if (orient == 'C'){
            fil++;
            while(fil<14 && tablero[fil][col].isVacia()==true){
                tablero[fil][col].setPosibilidades(elem);
                fil++;
            }
        }
        else if (orient == 'F'){
            col++;
            while(col<14 && tablero[fil][col].isVacia()==true){
                tablero[fil][col].setPosibilidades(elem);
                col++;
            }
        }
    }
    
}   
