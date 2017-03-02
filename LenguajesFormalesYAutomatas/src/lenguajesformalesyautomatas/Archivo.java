/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajesformalesyautomatas;

import java.awt.FileDialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gabriel
 */
public class Archivo 
{
    FileDialog fd = null;
    RandomAccessFile lector = null;
    Principal form = new Principal();
    String nombreArchivo;
    long tamArchivo;
    byte[] buffer = null;
    
    long inicioExp;
    long finExp;
    long parentesis;
    long filaError;
    long columnaError;
    
    List ListaNumeros = new ArrayList();
    public String error = "";
    
    List Acciones = new ArrayList();
    List AccionesTokens = new ArrayList();
    
    public Archivo()
    {
        fd = new FileDialog(form, "Abrir archivo", FileDialog.LOAD);
    }
    public void Cargar() throws IOException
    {
        fd.setVisible(true);
        nombreArchivo = fd.getDirectory() + fd.getFile();
        //Leer(nombreArchivo, 1, 23);
        Analizar();
    }
    public void Leer(String nombreA, int size, long pos) throws FileNotFoundException, IOException
    {
        lector = new RandomAccessFile(nombreA, "r");
        buffer = new byte[size];
        tamArchivo = lector.length();
        lector.seek(pos);
        lector.read(buffer);
        lector.close();
    }
    private void CalcularFilaColumna(long cont) throws IOException
    {
        filaError = 0;
        columnaError = 0;
        long aux = 0;
        while(aux < cont)
        {
            columnaError++;
            Leer(nombreArchivo, 1, aux);
            String caracterA = new String(buffer).toLowerCase();
            if(caracterA.equals("\n"))
            {
                filaError++;
                columnaError = 0;
            }
            aux++;
        }
        filaError++;
    }
    public void Analizar() throws IOException
    {
        long cont = 0;
        boolean banderaInicial = false; //Determina cuando llegamos al inicio de lo que nos interesa.
        boolean banderaFinal = false;
        while(!banderaFinal)
        {
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer).toLowerCase();
            
            if(caracterA.equals("t"))
            {
                cont++;
                Leer(nombreArchivo, 5, cont);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("okens"))
                {
                    banderaInicial = true;
                    cont = cont + 5;
                }
                else if(banderaInicial)
                {
                    Leer(nombreArchivo, 5, cont);
                    caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals("oken ")||caracterA.equals("oken\n")||caracterA.equals("oken\t"))
                    {
                        cont = cont + 5;
                        cont = AnalizarToken(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                    }
                    else
                    {
                        //es conjunto
                        cont = AnalizarConjunto(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    error = "TOKENS expected.";
                    break;
                }
            }
            else if(cont == tamArchivo)
            {
                CalcularFilaColumna(cont);
                error = "TOKENS expected. Fila: " + filaError + " Columna: " + columnaError;
                break;
            }
            else if(EsCaracter(cont))
            {
                if (banderaInicial) 
                {
                    if (caracterA.equals("a")) {
                        cont++;
                        Leer(nombreArchivo, 7, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("cciones"))
                        {
                            //seccion de acciones
                            cont = cont + 7;
                            cont = AnalizarAcciones(cont);
                        }
                        else
                        {
                            //Es conjunto
                            cont = AnalizarConjunto(cont);
                            if(!error.equals(""))
                            {
                                break;
                            }
                        }
                    }
                    else if (caracterA.equals("e")) 
                    {
                        cont++;
                        Leer(nombreArchivo, 4, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("rror"))
                        {
                            //seccion de error
                            cont = cont + 4;
                            cont = AnalizarError(cont);
                            if(!error.equals(""))
                            {
                                break;
                            }
                        }
                        else
                        {
                            //Es conjunto
                            cont = AnalizarConjunto(cont);
                            if(!error.equals(""))
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        //Es conjunto
                        cont = AnalizarConjunto(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    CalcularFilaColumna(cont);
                    error = "TOKENS expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else
            {
                cont++;
            }
            if(!error.equals(""))
            {
                break;
            }
        }
        
    }
    private void AgregarNumero(long num, long cont) throws IOException
    {
        boolean NoExiste = true;
        for (Object ListaNumero : ListaNumeros) 
        {
            if(ListaNumero.equals(num))
            {
                NoExiste = false;
                CalcularFilaColumna(cont);
                error = "Number already taken. Fila: " + filaError + " Columna: " + columnaError;
                break;
            }
        }
        if(NoExiste)
        {
            ListaNumeros.add(num);
        }
    }
    private void AgregarIDToken(String iden, long cont) throws IOException
    {
        boolean NoExiste = true;
        for (Object nombre : AccionesTokens) 
        {
            if(nombre.equals(iden))
            {
                NoExiste = false;
                break;
            }
        }
        if(NoExiste)
        {
            AccionesTokens.add(iden.trim());
        }
    }
    public long AnalizarToken(long cont) throws IOException
    {
        String caracterA = "";
        boolean banderaNumero = false;
        boolean banderaIgual = false;
        int tam = 0;
        long aux;
        long numero;
        coma: while(!caracterA.equals(";"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (Character.isDigit(caracterA.charAt(0))) //Encontrar numero
            {
                aux = cont;
                while(Character.isDigit(caracterA.charAt(0)))
                {
                    cont++; 
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                    tam++;
                }
                Leer(nombreArchivo, tam, aux);
                caracterA = new String(buffer).toLowerCase();
                numero = Long.parseLong(caracterA);
                AgregarNumero(numero,cont);
                banderaNumero = true;//Se encontro el numero
            }
            else if (banderaNumero) //Buscar caracter '='
            {
                cont = ComerEspacio(cont);
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("=")) 
                {
                    cont++;
                    banderaIgual = true;
                }
                else if (banderaIgual) 
                {
                    cont = SaltarEspacios(cont);
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                    long flag = cont;
                    OUTER:
                    while (!caracterA.equals(";")) 
                    {
                        flag++;    
                        Leer(nombreArchivo, 1, flag);
                        caracterA = new String(buffer).toLowerCase();
                        switch (caracterA) 
                        {
                            case "a":
                                flag++;
                                Leer(nombreArchivo, 7, flag);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals("cciones")) 
                                {
                                    CalcularFilaColumna(cont);
                                    error = "';' expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break coma;
                                }
                                else
                                {
                                    flag--;
                                }
                                break;
                            case "e":
                                flag++;
                                Leer(nombreArchivo, 4, flag);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals("rror")) 
                                {
                                    CalcularFilaColumna(cont);
                                    error = "';' expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break coma;
                                }
                                else
                                {
                                    flag--;
                                }
                                break;
                            case "t":
                                flag++;
                                Leer(nombreArchivo, 4, flag);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals("oken")) 
                                {
                                    CalcularFilaColumna(cont);
                                    error = "';' expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break coma;
                                }
                                else
                                {
                                    flag--;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    inicioExp = cont;//Sirve para validar parentesis una vez solamente
                    finExp = flag;
                    parentesis = ValidarParentesis(inicioExp, finExp);
                    cont = EvaluarExpresion(cont, flag);//Se evalua la expresion regular
                    cont++;
                    cont = SaltarEspacios(cont);
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                    if(caracterA.equals("["))
                    {
                        cont = TokensEspeciales(cont);
                    }
                    inicioExp = 0;
                    finExp = 0;
                    parentesis = 0;
                    break;
                }
                else
                {
                    CalcularFilaColumna(cont);
                    error = "'=' expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else
            {
                if (EsCaracter(cont)) 
                {
                    CalcularFilaColumna(cont);
                    error = "Token number expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
                else
                {
                    cont = ComerEspacio(cont);
                }
            }
            
        }
        return cont+1;
    }
    private long SaltarEspacios(long cont) throws IOException
    {
        long espacios = cont;
        while(!EsCaracter(espacios))
        {
            espacios++;
        }
        return espacios;
    }
    public long ComerEspacio(long cont) throws IOException
    {
        if(!EsCaracter(cont)) 
        {
            cont++;
        }
        return cont;
    }
    
    public boolean EsCaracter(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        return !caracterA.equals(" ") && !caracterA.equals("\t") && !caracterA.equals("\n") && !caracterA.equals("\r");
        
    }
    
    int contElementos=0;
    public long AnalizarConjunto(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        boolean banderaID = false, banderaNombre = false;
        if (caracterA.equals("{"))
        {
            CalcularFilaColumna(cont);
            error = "Invalid group name. Fila: " + filaError + " Columna: " + columnaError;
            return cont;
        }
        while(!caracterA.equals("{"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            
            if (Character.isDigit(caracterA.charAt(0)) && !banderaID)
            {
                if (banderaNombre) 
                {
                    CalcularFilaColumna(cont);
                    error = "{ expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
                else
                {
                    CalcularFilaColumna(cont);
                    error = "Invalid group name. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (Character.isLetter(caracterA.charAt(0)) && !banderaID)
            {
                if (!banderaID) {
                    cont++;
                    banderaID=true;
                }
            } 
            else if (banderaID) 
            {
                if (caracterA.equals("_")) {
                    cont++;
                }
                else if (Character.isDigit(caracterA.charAt(0))) {
                    cont++;
                }
                else if (Character.isLetter(caracterA.charAt(0))) {
                    cont++;
                }
                else if (!EsCaracter(cont)) {
                    banderaNombre=true;
                    banderaID=false;
                    cont++;
                }
                else if (caracterA.equals("{")) {
                    cont++;
                }
                else if (EsCaracter(cont) && !caracterA.equals("{")) 
                {
                    CalcularFilaColumna(cont);
                    error = "Invalid group name. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (banderaNombre)
            {
                if (!EsCaracter(cont)||caracterA.equals("{") ) {
                    cont++;
                }
                else
                {
                    CalcularFilaColumna(cont);
                    error = "{ expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else
            {
                CalcularFilaColumna(cont);
                error = "Invalid group name. Fila: " + filaError + " Columna: " + columnaError;
                break;
            }
        }
        
        if (error.equals("")) //si no hay error evaluar el contenido
        {
            //cont++;
            
            while(!caracterA.equals("}"))
            {
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("'")) 
                {
                    Leer(nombreArchivo, 1, cont+2);
                    caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals("'")) {
                        cont = cont+3;
                        contElementos++;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = "' expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                else if (caracterA.equals("\"")) 
                {
                    Leer(nombreArchivo, 1, cont+2);
                    caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals("\"")) 
                    {
                        cont = cont+3;
                        contElementos++;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = "\" expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                else if (caracterA.equals(".")) 
                {
                    Leer(nombreArchivo, 1, cont+1);
                    caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals(".")) 
                    {
                        //Buscar elemento siguiente y anterior.
                        cont = AnalizarRango(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                        contElementos++;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = ". expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                else if (caracterA.equals("c")) 
                {
                    Leer(nombreArchivo, 2, cont+1);
                    caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals("hr"))
                    {
                        cont = cont+3;
                        //es un chr
                        cont = AnalizarCHR(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                        contElementos++;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                else
                {
                    if(!EsCaracter(cont)) 
                    {
                        cont = ComerEspacio(cont);
                    }
                    else if (caracterA.equals("+"))
                    {
                        if (contElementos == 1) {
                            cont++;
                            contElementos--;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Bad use of +. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                    }
                    else if(EsCaracter(cont)&&!caracterA.equals("}"))
                    {
                        CalcularFilaColumna(cont);
                        error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }

                }
                
                if (contElementos > 1) 
                {
                    CalcularFilaColumna(cont);
                    error = "+ expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
        }
        
        contElementos=0;
        return cont + 1;
    }
    
    public long AnalizarRango(long cont) throws IOException
    {
        long Aux = cont;
        boolean PrimerElemento=false, SegundoElemento=false;
        byte Elemento = DistinguirPrimerElemento(cont);
        if(!error.equals(""))
        {
            return cont;
        }
        
        Leer(nombreArchivo, 1, Aux-1);
        String caracterA = new String(buffer).toLowerCase();
        
        //if (Elemento == -1 || Elemento == 1) 
       // {
            if (PrimerElemento==false) 
            {
                if(caracterA.equals("'")) {
                    PrimerElemento=true;
                }
                else if(caracterA.equals("\"")) {
                    PrimerElemento=true;
                }
                else if (caracterA.equals(")")) {
                    PrimerElemento=true;
                }
                else   
                {
                    if (Elemento == -1) {
                        while(!caracterA.equals("'"))
                        {
                            Aux--;
                            Leer(nombreArchivo, 1, Aux);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("'")) {
                                PrimerElemento=true;
                            }
                            else if (EsCaracter(Aux)) 
                            {
                                CalcularFilaColumna(cont);
                                error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                    }
                    else if (Elemento == 0) {
                        while(!caracterA.equals("\""))
                        {
                            Aux--;
                            Leer(nombreArchivo, 1, Aux);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("\"")) 
                            {
                                PrimerElemento=true;
                            }
                            else if (EsCaracter(Aux)) 
                            {
                                CalcularFilaColumna(cont);
                                error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                    }
                    else if (Elemento == 1) {
                        while(!caracterA.equals(")"))
                        {
                            Aux--;
                            Leer(nombreArchivo, 1, Aux);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals(")")) 
                            {
                                PrimerElemento=true;
                            }
                            else if (EsCaracter(Aux)) 
                            {
                                CalcularFilaColumna(cont);
                                error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                    }
                }
                
                if (PrimerElemento==true) 
                {
                    Leer(nombreArchivo, 1, cont+2);
                    caracterA = new String(buffer).toLowerCase();
                    if(caracterA.equals("'")) {
                        SegundoElemento=true;
                        cont = cont + 2;
                    }
                    else if(caracterA.equals("\"")) {
                        SegundoElemento=true;
                        cont = cont + 2;
                    }
                    else if (caracterA.equals("c")) {
                        //examinar c
                        cont = cont + 2;
                        Leer(nombreArchivo, 2, cont+1);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("hr"))
                        {
                            cont = cont+3;
                            //es un chr
                            cont = AnalizarCHR(cont);
                            if(!error.equals(""))
                            {
                                return cont;
                            }
                            
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                            return cont;
                        }
                    }
                    else   
                    {
                        cont = cont + 1;
                        while(!caracterA.equals("'")&& !caracterA.equals("c")&& !caracterA.equals("\""))
                        {
                            cont++;
                            Leer(nombreArchivo, 1, cont);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("'")) {
                                SegundoElemento=true;
                            }
                            else if (caracterA.equals("\"")) {
                                SegundoElemento=true;
                            }
                            else if (caracterA.equals("c")) {
                                //examinar c
                                Leer(nombreArchivo, 2, cont+1);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals("hr"))
                                {
                                    cont = cont+3;
                                    //es un chr
                                    cont = AnalizarCHR(cont);
                                    if(!error.equals(""))
                                    {
                                        break;
                                    }
                                    caracterA = "c";
                                }
                                else
                                {
                                    CalcularFilaColumna(cont);
                                    error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                                    break;
                                }
                            }
                            else if(EsCaracter(cont)) 
                            {
                                CalcularFilaColumna(cont);
                                error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        } 
                    }
                    
                    if (SegundoElemento==true) 
                    {                      
                        if (caracterA.equals("'")) 
                        {
                            Leer(nombreArchivo, 1, cont+2);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("'")) {
                                cont = cont+3;                                
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = "' expected. Fila: " + filaError + " Columna: " + columnaError;
                                return cont;
                            }
                        }
                        else if (caracterA.equals("\"")) 
                        {
                            Leer(nombreArchivo, 1, cont + 2);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("\"")) 
                            {
                                cont = cont+3;                                
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = "\" expected. Fila: " + filaError + " Columna: " + columnaError;
                                return cont;
                            }
                        }
                    }
                    contElementos--;
                }
            }
            
        return cont;
    }
    
    public byte DistinguirPrimerElemento(long cont) throws IOException
    {
        boolean BanderaComillas= false;
        byte PrimerElemento = 0;
        long Aux = cont;
        Leer(nombreArchivo, 1, Aux-1);
        String caracterA = new String(buffer).toLowerCase();
        if(caracterA.equals("'")) {
            PrimerElemento = -1;
        }
        else if (caracterA.equals("\"")) {
            PrimerElemento = 0;
        }
        else if (caracterA.equals(")")) {
            PrimerElemento = 1;
        }
        else   
        {
            while(!caracterA.equals("\"")&&!caracterA.equals("'")&&!caracterA.equals(")"))
            {
                Aux--;
                Leer(nombreArchivo, 1, Aux);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("\"")) {
                    PrimerElemento = 0;
                }
                else if (caracterA.equals("'")) {
                    PrimerElemento = -1;
                }
                else if (caracterA.equals(")")) {
                    PrimerElemento = 1;
                }
                else if (EsCaracter(Aux)) 
                {
                    CalcularFilaColumna(cont);
                    error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }        
        }
        return PrimerElemento;
    }
    
    public long AnalizarCHR(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        boolean banderaNum=false, banderaYano= false;
        if (caracterA.equals("(")) {
            cont++;
        }
        else
        {
            while(!caracterA.equals("("))
            {
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                if (!EsCaracter(cont)) {
                    cont++;
                }
                else if (!caracterA.equals("(")) 
                {
                    CalcularFilaColumna(cont);
                    error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            if(!error.equals(""))
            {
                return cont;
            }
            cont++;            
        }
        
        while(!caracterA.equals(")"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (!EsCaracter(cont)) {
                if (!banderaNum) {
                    cont++;
                }
                else if (banderaNum) {
                    cont++;
                    banderaYano=true;
                }
            }
            else if (Character.isDigit(caracterA.charAt(0))&& !banderaYano) {
                cont++;
                banderaNum=true;
            }
            else if (!caracterA.equals(")")) 
            {
                CalcularFilaColumna(cont);
                error = ") expected. Fila: " + filaError + " Columna: " + columnaError;
                break;
            }
        }
        if (!banderaNum&&!banderaYano) 
        {
            CalcularFilaColumna(cont);
            error = "number expected. Fila: " + filaError + " Columna: " + columnaError;
            return cont;
        }
        return cont+1;
    }
    
    public long AnalizarAcciones(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        String Accion="";
        boolean banderaFin=false;
        boolean banderaID = false, banderaNombre = false, banderaParentesis = false;
        while(!banderaFin)
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (!EsCaracter(cont)) {
                cont++;
            }
            else if (caracterA.equals("e")) {
                cont++;
                Leer(nombreArchivo, 4, cont);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("rror"))
                {
                    //seccion de error
                    cont =  cont+4;
                    cont = AnalizarError(cont);
                    banderaFin=true;
                }
                else
                {
                    cont--;
                    Leer(nombreArchivo, 4, cont);
                    caracterA = new String(buffer).toLowerCase();
                    while(!caracterA.equals("{"))
                    {
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer).toLowerCase();

                        if (Character.isDigit(caracterA.charAt(0)) && !banderaID)
                        {
                            if (banderaNombre) 
                            {
                                CalcularFilaColumna(cont);
                                error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        else if (Character.isLetter(caracterA.charAt(0)) && !banderaID)
                        {
                            if (banderaNombre) 
                            {
                                CalcularFilaColumna(cont);
                                error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                            else if (!banderaID) {
                                cont++;
                                Accion = Accion + caracterA;
                                banderaID=true;
                            }
                        } 
                        else if (banderaID) 
                        {
                            if (caracterA.equals("_")) {
                                cont++;
                                Accion = Accion + caracterA;
                            }
                            else if (Character.isDigit(caracterA.charAt(0))) {
                                cont++;
                                Accion = Accion + caracterA;
                            }
                            else if (Character.isLetter(caracterA.charAt(0))) {
                                cont++;
                                Accion = Accion + caracterA;
                            }
                            else if (!EsCaracter(cont)) {
                                banderaNombre=true;
                                banderaID=false;
                                cont++;
                            }
                            else if (EsCaracter(cont) && !caracterA.equals("{")) {
                                CalcularFilaColumna(cont);
                                error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        else if (banderaNombre)
                        {
                            if (!EsCaracter(cont)) {
                                cont++;
                            }
                            else if (caracterA.equals("(")) {
                                Leer(nombreArchivo, 1, cont+1);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals(")")) {
                                    cont=cont+2;
                                    banderaParentesis = true;
                                }
                                else
                                {
                                    CalcularFilaColumna(cont);
                                    error = ") expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break;
                                }
                            }
                            else if (caracterA.equals("{") && banderaParentesis) {
                                cont++;
                            }
                            else
                            {
                                if (!banderaParentesis) {
                                    CalcularFilaColumna(cont);
                                    error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break;
                                }
                                else
                                {
                                    CalcularFilaColumna(cont);
                                    error = "{ expected. Fila: " + filaError + " Columna: " + columnaError;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                    }
                    if(!error.equals(""))
                    {
                        break;
                    }
                    
                    cont = AnalizarContenidoAcciones(cont);
                    if(!error.equals(""))
                    {
                        break;
                    }
                    Acciones.add(Accion);
                    banderaID=false;
                    banderaNombre =false;
                    banderaParentesis=false;
                    Accion="";
                }
            }
            else if (EsCaracter(cont)) {
                while(!caracterA.equals("{"))
                {
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();

                    if (Character.isDigit(caracterA.charAt(0)) && !banderaID)
                    {
                        if (banderaNombre) 
                        {
                            CalcularFilaColumna(cont);
                            error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                    }
                    else if (Character.isLetter(caracterA.charAt(0)) && !banderaID)
                    {
                        if (banderaNombre) 
                        {
                            CalcularFilaColumna(cont);
                            error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                        else if (!banderaID) {
                            cont++;
                            Accion = Accion + caracterA;
                            banderaID=true;
                        }
                        
                    } 
                    else if (banderaID) 
                    {
                        if (caracterA.equals("_")) {
                            cont++;
                            Accion = Accion + caracterA;
                        }
                        else if (Character.isDigit(caracterA.charAt(0))) {
                            cont++;
                            Accion = Accion + caracterA;
                        }
                        else if (Character.isLetter(caracterA.charAt(0))) {
                            cont++;
                            Accion = Accion + caracterA;
                        }
                        else if (!EsCaracter(cont)) {
                            banderaNombre=true;
                            banderaID=false;
                            cont++;
                        }
                        else if (caracterA.equals("(")) {
                            banderaNombre=true;
                            banderaID=false;
                            if (banderaNombre) {
                                Leer(nombreArchivo, 1, cont+1);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals(")")) {
                                cont=cont+2;
                                banderaParentesis = true;
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = ") expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        else if (EsCaracter(cont) && !caracterA.equals("(")) 
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                    }
                    else if (banderaNombre)
                    {
                        if (!EsCaracter(cont)) {
                            cont++;
                        }
                        else if (caracterA.equals("(")) {
                            Leer(nombreArchivo, 1, cont+1);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals(")")) {
                                cont=cont+2;
                                banderaParentesis = true;
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = ") expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        else if (caracterA.equals("{") && banderaParentesis) {
                            cont++;
                        }
                        else
                        {
                            if (!banderaParentesis) 
                            {
                                CalcularFilaColumna(cont);
                                error = "( expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                            else
                            {
                                CalcularFilaColumna(cont);
                                error = "{ expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                if(!error.equals(""))
                {
                    break;
                }
                cont = AnalizarContenidoAcciones(cont);
                if(!error.equals(""))
                {
                    break;
                }
                Acciones.add(Accion);
                banderaID=false;
                banderaNombre =false;
                banderaParentesis=false;
                Accion="";
            }
        }
        //Vaidar si las acciones definidas en los tokens si estan
        if (error.equals("")||error.equals("File read successfully.")) {
            ValidarAcciones();
        }
        return cont;
    }
    
    public void ValidarAcciones()
    {
        boolean Existe=false;
        for (int i = 0; i < AccionesTokens.size(); i++) 
        {
            for (int j = 0; j < Acciones.size(); j++) 
            {
                if (AccionesTokens.get(i).equals(Acciones.get(j))) 
                {
                    Existe=true;
                    break;
                }
            }
            if (!Existe) 
            {
                error="Undefined action: "+AccionesTokens.get(i);
                break;
            }
        }
    }
    
    public long AnalizarContenidoAcciones(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        String Token="";
        boolean banderaNum=false, banderaIgual=false, banderaComilla = false;
        while(!caracterA.equals("}"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (Character.isDigit(caracterA.charAt(0))) 
            {
                if (banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "' or \" expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
                if (!banderaNum) {
                    cont++;
                    Token=Token+caracterA;
                    banderaNum=true;   
                }
                else if (banderaNum) {
                    Leer(nombreArchivo, 1, cont-1);
                    caracterA = new String(buffer).toLowerCase();
                    if (Character.isDigit(caracterA.charAt(0))) {
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer).toLowerCase();
                        Token=Token+caracterA;
                        cont++;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = "= expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
            }
            else if (caracterA.equals("=")) 
            {
                if (banderaNum) {
                    if (!banderaIgual) {
                        cont++;
                        banderaIgual = true;
                    }
                    else if (banderaIgual) 
                    {
                        CalcularFilaColumna(cont);
                        error = "' or \" expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }

                }
                else if (!banderaNum) 
                {
                    CalcularFilaColumna(cont);
                    error = "number expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (caracterA.equals("'")) {
                if (banderaIgual) {
                    if (!banderaComilla) {
                        banderaComilla = true;
                        cont++;
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer).toLowerCase();
                        while(!caracterA.equals("'"))
                        {
                            Leer(nombreArchivo, 1, cont);
                            caracterA = new String(buffer).toLowerCase();
                            if (!caracterA.equals("'")) {
                                cont++;
                            }
                            else if (caracterA.equals("")) 
                            {
                                CalcularFilaColumna(cont);
                                error = "' expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        cont++;
                    }
                    else if (banderaComilla) 
                    {
                        CalcularFilaColumna(cont);
                        error = "number or } expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                    
                }
                else if (!banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "= expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (caracterA.equals("\"")) {
                if (banderaIgual) {
                    if (!banderaComilla) {
                        banderaComilla = true;
                        cont++;
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer).toLowerCase();
                        while(!caracterA.equals("\""))
                        {
                            Leer(nombreArchivo, 1, cont);
                            caracterA = new String(buffer).toLowerCase();
                            if (!caracterA.equals("\"")) {
                                cont++;
                            }
                            else if (caracterA.equals("")) 
                            {
                                CalcularFilaColumna(cont);
                                error = "\" expected. Fila: " + filaError + " Columna: " + columnaError;
                                break;
                            }
                        }
                        cont++;
                    }
                    else if (banderaComilla) 
                    {
                        CalcularFilaColumna(cont);
                        error = "number or } expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                    
                }
                else if (!banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "= expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (!EsCaracter(cont)) {
                cont++;
                if (banderaComilla) {
                    banderaNum=false;
                    banderaIgual=false;
                    banderaComilla=false;
                    if (!ListaNumeros.contains(Token)) {
                        ListaNumeros.add(Token);
                        Token="";
                    }
                    else if (ListaNumeros.contains(Token)) 
                    {
                        CalcularFilaColumna(cont);
                        error = "Token id already taken. Fila: " + filaError + " Columna: " + columnaError;
                    }
                }
            }
            else if (EsCaracter(cont))
            {
                if (!caracterA.equals("}")) 
                {
                    CalcularFilaColumna(cont);
                    error = "Invalid char. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
                else if(caracterA.equals("}"))
                { 
                    if (banderaComilla) {
                        banderaNum=false;
                        banderaIgual=false;
                        banderaComilla=false;
                        if (!ListaNumeros.contains(Token)) {
                            ListaNumeros.add(Token);
                            Token="";
                        }
                        else if (ListaNumeros.contains(Token)) 
                        {
                            CalcularFilaColumna(cont);
                            error = "Token id already taken. Fila: " + filaError + " Columna: " + columnaError;
                        }
                    }
                    if (banderaComilla || banderaIgual || banderaNum) 
                    {
                        CalcularFilaColumna(cont);
                        error = "Invalid char. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
            }
        }
        cont = cont +1;
        return cont;
    }

    private long ValidarParentesis(long cont, long fin) throws IOException
    {
        long master = 0;
        while(cont < fin)
        {
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer).toLowerCase();
            switch(caracterA)
            {
                case "(":
                    master++;
                    break;
                case ")":
                    master--;
                    break;
                default:
                    break;
            }
            cont++;
        }
        if(parentesis < 0)
        {
            CalcularFilaColumna(cont);
            error = "( Missing. Fila: " + filaError + " Columna: " + columnaError;
        }
        else if(parentesis > 0)
        {
            CalcularFilaColumna(cont);
            error = ") Expected. Fila: " + filaError + " Columna: " + columnaError;
        }
        return master;
    }
    
    private long EvaluarExpresion(long cont, long posBandera) throws IOException
    {
        if(parentesis == 0)
        {
            boolean bandera = false; //Valida que una expresion sea correcta para no volverla a evaluar
            long inicio = cont;
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer).toLowerCase();
            loop: while(cont < posBandera)
            {
                switch(caracterA)
                {
                    case "|":
                    {
                        cont = EvaluarExpresion(inicio, cont);
                        cont = EvaluarExpresion(cont + 1, posBandera);
                        cont--;
                        bandera = true;
                        break;
                    }
                    case "'":
                    {
                        Leer(nombreArchivo, 1, cont+2);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("'"))
                        {
                            cont = cont + 2;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Closing ' expected. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "\"":
                    {
                        Leer(nombreArchivo, 1, cont + 2);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("\""))
                        {
                            cont = cont + 2;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Closing \" expected. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "*":
                    {
                        if(bandera)
                        {
                            cont = EvaluarExpresion(inicio, cont);
                            //cont--;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid regex symbol '*'. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "+":
                    {
                        if(bandera)
                        {
                            cont = EvaluarExpresion(inicio, cont);
                            //cont--;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid regex symbol '+'. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "?":
                    {
                        if(bandera)
                        {
                            cont = EvaluarExpresion(inicio, cont);
                            //cont--;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid regex symbol '?'. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "a":
                    {
                        cont++;
                        Leer(nombreArchivo, 7, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("cciones")) 
                        {
                            Leer(nombreArchivo, 1, posBandera);
                            String caracterBandera = new String(buffer).toLowerCase();
                            CalcularFilaColumna(cont);
                            error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "t":
                    {
                        Leer(nombreArchivo, 4, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("oken")) 
                        {
                            Leer(nombreArchivo, 1, posBandera);
                            String caracterBandera = new String(buffer).toLowerCase();
                            CalcularFilaColumna(cont);
                            error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    case "[":
                    {
                        Leer(nombreArchivo, 1, posBandera);
                        String caracterBandera = new String(buffer).toLowerCase();
                        CalcularFilaColumna(cont);
                            error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                        break loop;
                    }
                    case ";":
                    {
                        Leer(nombreArchivo, 7, posBandera);
                        String caracterBandera = new String(buffer).toLowerCase();
                        CalcularFilaColumna(cont);
                        error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                        break loop;
                    }
                    case "e":
                    {
                        Leer(nombreArchivo, 4, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("rror")) 
                        {
                            Leer(nombreArchivo, 1, posBandera);
                            String caracterBandera = new String(buffer).toLowerCase();
                            CalcularFilaColumna(cont);
                            error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                            break loop;
                        }
                        break;
                    }
                    default:
                        break;
                }
                bandera = true;
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
            }
        }
        return cont;
    }
    private long TokensEspeciales(long cont) throws IOException
    {
        String nombre;
        int tam = 0;
        long aux;
        cont = SaltarEspacios(cont);
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        if(Character.isDigit(caracterA.charAt(0)))
        {
            CalcularFilaColumna(cont);
            error = "Invalid action name. Fila: " + filaError + " Columna: " + columnaError;
        }
        else
        {
            aux = cont + 1;
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            while(!caracterA.equals("("))
            {
                
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                if(caracterA.equals("("))
                {
                    cont++;
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                    if(caracterA.equals(")"))
                    {
                        cont++;
                        break;
                    }
                    else
                    {
                        CalcularFilaColumna(cont);
                        error = ") Expected. Fila: " + filaError + " Columna: " + columnaError;
                        break;
                    }
                }
                tam++;
            }
            
            Leer(nombreArchivo, tam, aux);
            nombre = new String(buffer).toLowerCase();
            AgregarIDToken(nombre, cont);
            cont = ComerEspacio(cont);
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if(!caracterA.equals("]"))
            {
                CalcularFilaColumna(cont);
                error = "] Expected. Fila: " + filaError + " Columna: " + columnaError;
            }
        }
        return cont;
    }
    
    public long AnalizarError(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA;
        boolean banderaFinal=false,banderaIgual=false, banderaNum=false;
        String Token = "";
        while(!banderaFinal)
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (!EsCaracter(cont)) {
                cont++;
                if (banderaIgual && banderaNum) {
                    if (!ListaNumeros.contains(Token)) {
                        ListaNumeros.add(Token);
                        error="File read successfully.";
                        break;
                    }
                    else if (ListaNumeros.contains(Token)) 
                    {
                        CalcularFilaColumna(cont);
                        error = "Token id already taken. Fila: " + filaError + " Columna: " + columnaError;
                    }
                }
            }
            else if (caracterA.equals("=")) {
                if (!banderaIgual) {
                    banderaIgual=true;
                    cont++;
                }
                else if (banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "Number expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (Character.isDigit(caracterA.charAt(0))) {
                if (banderaIgual) {
                    if (!banderaNum) {
                        Token=Token+caracterA;
                        banderaNum=true;
                        cont++;
                    }
                    else if (banderaNum) {
                        Leer(nombreArchivo, 1, cont-1);
                        caracterA = new String(buffer).toLowerCase();
                        if (Character.isDigit(caracterA.charAt(0))) 
                        {
                            Leer(nombreArchivo, 1, cont);
                            caracterA = new String(buffer).toLowerCase();
                            Token=Token+caracterA;
                            cont++;
                        }
                        else
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid character. Fila: " + filaError + " Columna: " + columnaError;
                            break;
                        }
                    }
                }
                else if (!banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "'=' Expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            else if (EsCaracter(cont)) 
            {
                if (!banderaNum) 
                {
                    CalcularFilaColumna(cont);
                    error = "Invalid character. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
                else if (!banderaIgual) 
                {
                    CalcularFilaColumna(cont);
                    error = "Invalid character. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
        }
        return cont;
    }
}
