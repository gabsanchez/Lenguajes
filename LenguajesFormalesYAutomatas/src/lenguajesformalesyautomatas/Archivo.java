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
//import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.Stack;
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
    
    public List ListaNumeros = new ArrayList();
    public String error = "";
    
    public List<String> Tokens = new ArrayList();
    List Acciones = new ArrayList();
    List AccionesTokens = new ArrayList();
    List PunterosAccionesTokens = new ArrayList();
    List ContenidoAcciones = new ArrayList();
    
    List<String> ConjuntosLlamados = new ArrayList();
    List ConjuntosDeclarados = new ArrayList();
    List<String> Elementos = new ArrayList();
    List CHR = new ArrayList();
    
    List<String> CaracteresTokens;
    
    public Archivo()
    {
        //this.nodoKey = new ArrayList();
        fd = new FileDialog(form, "Abrir archivo", FileDialog.LOAD);
    }
    public void Cargar() throws IOException
    {
        fd.setVisible(true);
        nombreArchivo = fd.getDirectory() + fd.getFile();
        if(!nombreArchivo.equals("nullnull"))
        {
            Analizar();
            //Evaluar tokens para el automata.
            if (error.equals("File read successfully.")) {
                FirstLastFollow();
                
            }
        }
        else
        {
            error = "No file selected";
        }
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
    
    // <editor-fold defaultstate="collapsed" desc="Sintaxis">
    public void Analizar() throws IOException
    {
        long cont = 0;
        boolean banderaInicial = false; //Determina cuando llegamos al inicio de lo que nos interesa.
        boolean banderaFinal = false;
        OUTER:
        while (!banderaFinal) {
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer).toLowerCase();
            if (caracterA.equals("t")) {
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
                    if (caracterA.equals("oken ")||caracterA.equals("oken\n")||caracterA.equals("oken\r")||caracterA.equals("oken\t"))
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
                        cont--;
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
            else if (cont == tamArchivo) 
            {
                CalcularFilaColumna(cont);
                error = "TOKENS expected. Row: " + filaError + " Column: " + columnaError;
                break;
            } 
            else if (EsCaracter(cont)) 
            {
                if (banderaInicial) 
                {
                    switch (caracterA) 
                    {
                        case "a":
                        {
                            cont++;
                            Leer(nombreArchivo, 8, cont);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("cciones ")||caracterA.equals("cciones\t")||caracterA.equals("cciones\n")||caracterA.equals("cciones\r")) 
                            {
                                //seccion de acciones
                                cont = cont + 8;
                                cont = AnalizarAcciones(cont);
                            } 
                            else 
                            {
                                //Es conjunto
                                cont--;
                                cont = AnalizarConjunto(cont);
                                if (!error.equals("")) {
                                    break OUTER;
                                }
                            }
                            break;
                        }
                        case "e":
                        {
                            cont++;
                            Leer(nombreArchivo, 5, cont);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("rror ")||caracterA.equals("rror\t")||caracterA.equals("rror\n")||caracterA.equals("rror\r")) 
                            {
                                //seccion de error
                                cont = cont + 5;
                                cont = AnalizarError(cont);
                                if (error.equals("")||error.equals("File read successfully.")) 
                                {
                                    ValidarAcciones();
                                }
                                if (!error.equals("")) 
                                {
                                    break OUTER;
                                }
                            }
                            else 
                            {
                                //Es conjunto
                                cont--;
                                cont = AnalizarConjunto(cont);
                                if (!error.equals("")) 
                                {
                                    break OUTER;
                                }
                            }
                            break;
                        }
                        default:
                        {
                            //Es conjunto
                            cont = AnalizarConjunto(cont);
                            if (!error.equals("")) 
                            {
                                break OUTER;
                            }
                            break;
                        }
                    }
                } else {
                    CalcularFilaColumna(cont);
                    error = "TOKENS expected. Row: " + filaError + " Column: " + columnaError;
                    break;
                }
            } else {
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
                error = "Number already taken. Row: " + filaError + " Column: " + columnaError;
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
            PunterosAccionesTokens.add(cont);
        }
    }
    public long AnalizarToken(long cont) throws IOException
    {
        String caracterA = "";
        boolean banderaNumero = false;
        boolean banderaIgual = false;
        int tamNum = 0;
        long aux;
        int tamToken;
        String sToken = "";
        long auxT = 0;
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
                    tamNum++;
                }
                Leer(nombreArchivo, tamNum, aux);
                String auxiliar = new String(buffer).toLowerCase();
                numero = Long.parseLong(auxiliar);
                AgregarNumero(numero,cont);
                
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                banderaNumero = true;//Se encontro el numero
            }
            else if (banderaNumero) //Buscar caracter '='
            {
                cont = SaltarEspacios(cont);
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
                    auxT = cont;
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                    long flag = cont;
                    OUTER:
                    while (!caracterA.equals(";")) 
                    {
                        switch (caracterA) 
                        {
                            case "a":
                                flag++;
                                Leer(nombreArchivo, 8, flag);
                                caracterA = new String(buffer).toLowerCase();
                                if (caracterA.equals("cciones ")||caracterA.equals("cciones\t")||caracterA.equals("cciones\n")||caracterA.equals("cciones\r")) 
                                {
                                    CalcularFilaColumna(cont);
                                    error = "';' expected. Row: " + filaError + " Columna: " + columnaError;
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
                            case "'":
                            {
                                Leer(nombreArchivo, 1, flag + 2);
                                String tem = new String(buffer).toLowerCase();
                                if(tem.equals("'"))
                                {
                                    flag = flag + 2;
                                }
                                break;
                            }
                            case "\"":
                            {
                                Leer(nombreArchivo, 1, flag + 2);
                                String tem = new String(buffer).toLowerCase();
                                if(tem.equals("\""))
                                {
                                    flag = flag + 2;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        flag++;
                        Leer(nombreArchivo, 1, flag);
                        caracterA = new String(buffer).toLowerCase();
                    }
                    inicioExp = cont;//Sirve para validar parentesis una vez solamente
                    finExp = flag;
                    CaracteresTokens = new ArrayList();
                    cont = EvaluarExpresion(cont, flag);//Se evalua la expresion regular
                    parentesis = ValidarParentesis(inicioExp, finExp);
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
        tamToken = (int)(cont - auxT);
        String subcadena;
        for (long i = auxT; i < cont; i++) 
        {
            Leer(nombreArchivo, 1, i);
            subcadena = new String(buffer).toLowerCase();
            if(subcadena.equals(" "))
            {
                Leer(nombreArchivo, 1, i + 1);
                String aux1 = new String(buffer).toLowerCase();
                Leer(nombreArchivo, 1, i - 1);
                String aux2 = new String(buffer).toLowerCase();
                if((aux1.equals("\"") && aux2.equals("\"")) || (aux1.equals("\'") && aux2.equals("\'")))
                {
                    sToken = sToken + subcadena;
                }
            }
            else
            {
                sToken = sToken + subcadena;
            }
        }
        //Leer(nombreArchivo, tamToken, auxT);
        //sToken = new String(buffer).toLowerCase();
        Tokens.add(sToken.trim());
        //Tokens.add(sToken.replaceAll(" ","").trim());
        return cont;
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
        String NombreConjunto="";
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
                    NombreConjunto = NombreConjunto +caracterA;
                }
            } 
            else if (banderaID) 
            {
                if (caracterA.equals("_")) {
                    cont++;
                    NombreConjunto = NombreConjunto +caracterA;
                }
                else if (Character.isDigit(caracterA.charAt(0))) {
                    cont++;
                    NombreConjunto = NombreConjunto +caracterA;
                }
                else if (Character.isLetter(caracterA.charAt(0))) {
                    cont++;
                    NombreConjunto = NombreConjunto +caracterA;
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
        
        ConjuntosDeclarados.add(NombreConjunto);
        String Contenido = "";
        long contInicial=cont;
        
        if (error.equals("")) //si no hay error evaluar el contenido
        {
            OUTER:
            while (!caracterA.equals("}")) 
            {
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                switch (caracterA) 
                {
                    case "'":
                    {
                        Leer(nombreArchivo, 1, cont+2);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("'")) 
                        {
                            cont = cont+3;
                            contElementos++;
                        } 
                        else 
                        {
                            CalcularFilaColumna(cont);
                            error = "' expected. Fila: " + filaError + " Columna: " + columnaError;
                            break OUTER;
                        }
                        break;
                    }
                    case "\"":
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
                            break OUTER;
                        }
                        break;
                    }
                    case ".":
                    {
                        Leer(nombreArchivo, 1, cont+1);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals(".")) 
                        {
                            //Buscar elemento siguiente y anterior.
                            cont = AnalizarRango(cont);
                            if (!error.equals("")) 
                            {
                                break OUTER;
                            }
                            contElementos++;
                        } 
                        else 
                        {
                            CalcularFilaColumna(cont);
                            error = ". expected. Fila: " + filaError + " Columna: " + columnaError;
                            break OUTER;
                        }
                        break;
                    }
                    case "c":
                    {
                        Leer(nombreArchivo, 2, cont+1);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("hr")) 
                        {
                            cont = cont+3;
                            //es un chr
                            cont = AnalizarCHR(cont);
                            if (!error.equals("")) 
                            {
                                break OUTER;
                            }
                            contElementos++;
                        } 
                        else 
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                            break OUTER;
                        }
                        break;
                    }
                    default:
                        if (!EsCaracter(cont)) 
                        {
                            cont = ComerEspacio(cont);
                        } 
                        else if (caracterA.equals("+")) 
                        {
                            if (contElementos == 1) 
                            {
                                cont++;
                                contElementos--;
                            } 
                            else 
                            {
                                CalcularFilaColumna(cont);
                                error = "Bad use of +. Fila: " + filaError + " Columna: " + columnaError;
                                break OUTER;
                            }
                        } 
                        else if (EsCaracter(cont)&&!caracterA.equals("}")) 
                        {
                            CalcularFilaColumna(cont);
                            error = "Invalid group element. Fila: " + filaError + " Columna: " + columnaError;
                            break OUTER;
                        }
                        break;
                }
                if (contElementos > 1) 
                {
                    CalcularFilaColumna(cont);
                    error = "+ expected. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            //Almacenar contenido de los conjuntos.
            if (error.equals("")) {
                Contenido = GuardarElementos(contInicial,Contenido);
                Elementos.add(Contenido);                
            }
        }
        
        contElementos=0;
        return cont + 1;
    }
    
    public String GuardarElementos(long contInicial, String Contenido) throws IOException
    {
        String caracterA="";
        while (!caracterA.equals("}")) 
        {
            Leer(nombreArchivo, 1, contInicial);
            caracterA = new String(buffer).toLowerCase();
            //If de agregar contenido
            if (EsCaracter(contInicial)) {
                Contenido=Contenido+caracterA;
            }
            else if (!EsCaracter(contInicial)) {
                Leer(nombreArchivo, 1, contInicial-1);
                String Espacio1 = new String(buffer).toLowerCase();
                Leer(nombreArchivo, 1, contInicial+1);
                String Espacio2 = new String(buffer).toLowerCase();
                if (Espacio1.equals("'")&&Espacio2.equals("'")) {
                    Contenido=Contenido+caracterA;
                }
                else if (Espacio1.equals("\"")&&Espacio2.equals("\"")) {
                    Contenido=Contenido+caracterA;
                }
            }
            //fin del If de agregar contenido
            contInicial++;
        }
        return Contenido;
    }
    
    public long AnalizarRango(long cont) throws IOException
    {
        String Primervalor ="", Segundovalor="";
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
                switch (caracterA)
                {
                    case "'":
                        PrimerElemento=true;
                        Leer(nombreArchivo, 1, Aux-2);
                        Primervalor = new String(buffer).toLowerCase();
                        break;
                    case "\"":
                        PrimerElemento=true;
                        Leer(nombreArchivo, 1, Aux-2);
                        Primervalor = new String(buffer).toLowerCase();
                        break;
                    case ")":
                        PrimerElemento=true;
                        Primervalor = CHR.get(CHR.size()-1).toString();
                        CHR.remove(CHR.size()-1);
                        break;
                    default:
                        switch (Elemento) 
                        { 
                            case -1:
                                while(!caracterA.equals("'"))
                                {
                                    Aux--;
                                    Leer(nombreArchivo, 1, Aux);
                                    caracterA = new String(buffer).toLowerCase();
                                    if (caracterA.equals("'")) {
                                        PrimerElemento=true;
                                        Leer(nombreArchivo, 1, Aux-1);
                                        Primervalor = new String(buffer).toLowerCase();
                                    }
                                    else if (EsCaracter(Aux))
                                    {
                                        CalcularFilaColumna(cont);
                                        error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                        break;
                                    }
                                }
                                break;
                            case 0:
                                while(!caracterA.equals("\""))
                                {
                                    Aux--;
                                    Leer(nombreArchivo, 1, Aux);
                                    caracterA = new String(buffer).toLowerCase();
                                    if (caracterA.equals("\""))
                                    {
                                        PrimerElemento=true;
                                        Leer(nombreArchivo, 1, Aux-1);
                                        Primervalor = new String(buffer).toLowerCase();
                                    }
                                    else if (EsCaracter(Aux))
                                    {
                                        CalcularFilaColumna(cont);
                                        error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                        break;
                                    }
                                }
                                break;
                            case 1:
                                while(!caracterA.equals(")"))
                                {
                                    Aux--;
                                    Leer(nombreArchivo, 1, Aux);
                                    caracterA = new String(buffer).toLowerCase();
                                    if (caracterA.equals(")"))
                                    {
                                        PrimerElemento=true;
                                        Primervalor = CHR.get(CHR.size()-1).toString();
                                        CHR.remove(CHR.size()-1);
                                    }
                                    else if (EsCaracter(Aux))
                                    {
                                        CalcularFilaColumna(cont);
                                        error = "Range definition error. Fila: " + filaError + " Columna: " + columnaError;
                                        break;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                }
                if (PrimerElemento==true) 
                {
                    Leer(nombreArchivo, 1, cont+2);
                    caracterA = new String(buffer).toLowerCase();
                    switch (caracterA) 
                    {
                        case "'":
                            SegundoElemento=true;
                            cont = cont + 2;
                            break;
                        case "\"":
                            SegundoElemento=true;
                            cont = cont + 2;
                            break;
                        case "c":
                        {
                            //examinar c
                            cont = cont + 2;
                            Leer(nombreArchivo, 2, cont+1);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("hr"))
                            {
                                cont = cont+3;
                                //es un chr
                                cont = AnalizarCHR(cont);
                                Segundovalor = CHR.get(CHR.size()-1).toString();
                                CHR.remove(CHR.size()-1);
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
                            break;
                        }
                        default:
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
                                        Segundovalor = CHR.get(CHR.size()-1).toString();
                                        CHR.remove(CHR.size()-1);
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
                            break;
                        } 
                    }
                    
                    if (SegundoElemento==true) 
                    {                      
                        if (caracterA.equals("'")) 
                        {
                            Leer(nombreArchivo, 1, cont+2);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("'")) {
                                Leer(nombreArchivo, 1, cont+1);
                                Segundovalor = new String(buffer).toLowerCase();
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
                                Leer(nombreArchivo, 1, cont+1);
                                Segundovalor = new String(buffer).toLowerCase();
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
        //Validar el rango
        ValidacionRangos(Primervalor,Segundovalor);
        if (!error.equals("")) {
            CalcularFilaColumna(cont);
            error = error + " Fila: " + filaError + " Columna: " + columnaError;
            return cont;
        }
        return cont;
    }
    public byte DistinguirPrimerElemento(long cont) throws IOException
    {
        //boolean BanderaComillas= false;
        byte PrimerElemento = 0;
        long Aux = cont;
        Leer(nombreArchivo, 1, Aux-1);
        String caracterA = new String(buffer).toLowerCase();
        switch (caracterA)
        {
            case "'":
                PrimerElemento = -1;
                break;
            case "\"":
                PrimerElemento = 0;
                break;
            case ")":
                PrimerElemento = 1;
                break;
            default:
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
                break;
            }
        }
        return PrimerElemento;
    }
    public long AnalizarCHR(long cont) throws IOException
    {
        String Numero = "";
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
                Numero = Numero + caracterA;
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
        
        CHR.add(Numero);
        cont=cont+1;
        return cont;
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
                Leer(nombreArchivo, 5, cont);
                caracterA = new String(buffer).toLowerCase();
                if (caracterA.equals("rror ")||caracterA.equals("rror\t")||caracterA.equals("rror\n")||caracterA.equals("rror\r"))
                {
                    //seccion de error
                    cont =  cont+5;
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
                    ContenidoAcciones.add(Accion+":");
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
            else if (EsCaracter(cont)) 
            {
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
                ContenidoAcciones.add(Accion+":");
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
    public void ValidarAcciones() throws IOException
    {
        boolean Existe=false;
        for (int i = 0; i < AccionesTokens.size(); i++) 
        {
            Existe=false;
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
                CalcularFilaColumna((long)(PunterosAccionesTokens.get(i)));
                error = "Undefined action: " + AccionesTokens.get(i) + " " + "Row: " + filaError + " Column: " + columnaError;
                break;
            }
        }
    }   
    public long AnalizarContenidoAcciones(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        String Token="";
        String TodoCont = "";
        boolean banderaNum=false, banderaIgual=false, banderaComilla = false;
        while(!caracterA.equals("}"))
        {
            cont = SaltarEspacios(cont);
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
                    TodoCont=TodoCont+caracterA;
                    banderaNum=true;   
                }
                else if (banderaNum) {
                    Leer(nombreArchivo, 1, cont-1);
                    caracterA = new String(buffer).toLowerCase();
                    if (Character.isDigit(caracterA.charAt(0))) {
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer).toLowerCase();
                        Token=Token+caracterA;
                        TodoCont=TodoCont+caracterA;
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
                        TodoCont=TodoCont+caracterA;
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
                        TodoCont=TodoCont+caracterA;
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
                            TodoCont=TodoCont+caracterA;
                        }
                        cont++;
                        banderaIgual=false;
                        banderaNum=false;
                        banderaComilla=false;
                        if (!ListaNumeros.contains(Token)) 
                        {
                            ListaNumeros.add(Token);
                            ContenidoAcciones.add(TodoCont);
                            Token="";
                            TodoCont="";
                        }
                        else if (ListaNumeros.contains(Token)) 
                        {
                            CalcularFilaColumna(cont);
                            error = "Token id already taken. Fila: " + filaError + " Columna: " + columnaError;
                        }
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
                    if (!banderaComilla) 
                    {
                        banderaComilla = true;
                        cont++;
                        TodoCont=TodoCont+caracterA;
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
                            TodoCont=TodoCont+caracterA;
                        }
                        cont++;
                        banderaIgual=false;
                        banderaNum=false;
                        banderaComilla=false;
                        if (!ListaNumeros.contains(Token)) {
                            ListaNumeros.add(Token);
                            ContenidoAcciones.add(TodoCont);
                            Token="";
                            TodoCont="";
                        }
                        else if (ListaNumeros.contains(Token)) 
                        {
                            CalcularFilaColumna(cont);
                            error = "Token id already taken. Fila: " + filaError + " Columna: " + columnaError;
                        }
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
        parentesis = 0;
        long aux = 0;
        for (String caracter: CaracteresTokens) 
        {
            if(caracter.equals("("))
            {
                aux--;
            }
            else if(caracter.equals(")"))
            {
                aux++;
            }
        }
        while(cont < fin)
        {
            if(parentesis < 0)
            {
                if(aux > 0)
                {
                    parentesis++;
                    aux--;
                }
                else
                {
                    CalcularFilaColumna(cont);
                    error = "( Missing. Fila: " + filaError + " Columna: " + columnaError;
                    break;
                }
            }
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer).toLowerCase();
            switch(caracterA)
            {
                case "(":
                {
                    parentesis++;
                    break;
                } 
                case ")":
                {
                    parentesis--;
                    break;
                } 
                default:
                    break;
            }
            cont++;
        }
        parentesis = parentesis + aux;
        if(parentesis > 0)
        {
            CalcularFilaColumna(cont);
            error = ") Expected. Fila: " + filaError + " Columna: " + columnaError;
        }
        return parentesis;
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
                        break;
                    }
                    case "'":
                    {
                        Leer(nombreArchivo, 1, cont + 2);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("'"))
                        {
                            Leer(nombreArchivo, 1, cont + 1);
                            String caracterT = new String(buffer).toLowerCase();
                            CaracteresTokens.add(caracterT);
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
                            Leer(nombreArchivo, 1, cont + 1);
                            String caracterT = new String(buffer).toLowerCase();
                            CaracteresTokens.add(caracterT);
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
                            long auxiliar;
                            long temporal;
                            auxiliar= cont + 1;
                            Leer(nombreArchivo, 1, auxiliar);
                            String cerradura = new String(buffer).toLowerCase();
                            while(cerradura.equals("*") || cerradura.equals("+") || cerradura.equals("?"))
                            {
                                auxiliar++;
                                auxiliar = SaltarEspacios(auxiliar);
                                Leer(nombreArchivo, 1, auxiliar);
                                cerradura = new String(buffer).toLowerCase();
                            }
                            temporal = EvaluarExpresion(inicio, cont);
                            cont = auxiliar -1;
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
                            long auxiliar;
                            long temporal;
                            auxiliar= cont + 1;
                            Leer(nombreArchivo, 1, auxiliar);
                            String cerradura = new String(buffer).toLowerCase();
                            while(cerradura.equals("*") || cerradura.equals("+") || cerradura.equals("?"))
                            {
                                auxiliar++;
                                auxiliar = SaltarEspacios(auxiliar);
                                Leer(nombreArchivo, 1, auxiliar);
                                cerradura = new String(buffer).toLowerCase();
                            }
                            temporal = EvaluarExpresion(inicio, cont);
                            cont = auxiliar -1;
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
                    default:
                    {
                        long aux = cont;
                        int tam = 0;
                        if(Character.isLetter(caracterA.charAt(0)))
                        {
                            conjus: while(cont < posBandera)
                            {
                                switch(caracterA)
                                {
                                    case "a":
                                    {
                                        cont++;
                                        Leer(nombreArchivo, 8, cont);
                                        caracterA = new String(buffer).toLowerCase();
                                        if (caracterA.equals("cciones ")||caracterA.equals("cciones\t")||caracterA.equals("cciones\n")||caracterA.equals("cciones\r")) 
                                        {
                                            Leer(nombreArchivo, 1, posBandera);
                                            String caracterBandera = new String(buffer).toLowerCase();
                                            CalcularFilaColumna(cont);
                                            error = "'" + caracterBandera + "'" + "expected. Fila: " + filaError + " Columna: " + columnaError;
                                            break loop;
                                        }
                                        else
                                        {
                                            tam++;
                                        }
                                        break;
                                    }
                                    case "t":
                                    {
                                        cont++;
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
                                        else
                                        {
                                            tam++;
                                        }
                                        break;
                                    }
                                    case "e":
                                    {
                                        cont++;
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
                                        else
                                        {
                                            tam++;
                                        }
                                        break;
                                    }
                                    default:
                                    {
                                        //cont++;
                                        Leer(nombreArchivo, 1, cont);
                                        caracterA = new String(buffer).toLowerCase();
                                        while(Character.isLetter(caracterA.charAt(0)) || Character.isDigit(caracterA.charAt(0)))
                                        {
                                            cont++;
                                            Leer(nombreArchivo, 1, cont);
                                            caracterA = new String(buffer).toLowerCase();
                                            tam++;
                                        }
                                        //cont++;
                                        String conjunto;
                                        Leer(nombreArchivo, tam, aux);
                                        conjunto = new String(buffer).toLowerCase();
                                        //tam = 0;
                                        if(!GuardarConjuntoEXP(conjunto, 0, conjunto.length()))
                                        {
                                            CalcularFilaColumna(cont);
                                            error = "Undefined group name \" " + conjunto + "\". Row: " + filaError + " Column: " + columnaError;
                                        }
                                        break conjus;
                                    }
                                }
                            }
                            cont--;
                        }
                        break;
                    }
                        
                }
                bandera = true;
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
            }
        }
        return cont;
    }
    private boolean GuardarConjuntoEXP(String nombre, int comienzo, int fin) throws IOException
    {
        if(comienzo == fin)
        {
            return true;
        }
        else if(ConjuntosDeclarados.contains(nombre.substring(comienzo, fin)))
        {
            if (!ConjuntosLlamados.contains(nombre.substring(comienzo, fin))) 
            {
                ConjuntosLlamados.add(nombre.substring(comienzo, fin));
            }
            return GuardarConjuntoEXP(nombre, fin, nombre.length());
        }
        else
        {
            if(fin - 1 == comienzo)
            {
                return false;
            }
            else
            {
                return GuardarConjuntoEXP(nombre, comienzo, fin - 1);
            }
        }
        /*boolean Declarado = false;
        int indice_i = 0;
        int indice_f = nombre.length();
        String aux;
        for (int i = indice_f; i > indice_i; i--) 
        {
            aux = nombre.substring(indice_i, i);
            if(ConjuntosDeclarados.contains(aux))
            {
                Declarado = true;
                if (!ConjuntosLlamados.contains(aux)) 
                {
                    ConjuntosLlamados.add(aux);
                }
                indice_i = i;
                i = indice_f;
            }
            else
            {
                Declarado = false;
            }
        }
        return Declarado;*/
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
            cont = SaltarEspacios(cont);
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if(!caracterA.equals("]"))
            {
                CalcularFilaColumna(cont);
                error = "] Expected. Fila: " + filaError + " Columna: " + columnaError;
            }
        }

        return cont + 1;
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
    
// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Logica">
    public void ValidacionRangos(String L1, String L2)
    {       
        int Asccii1=0, Asccii2=0;  
        if (!Character.isDigit(L1.charAt(0))) {
            Asccii1 = (int)L1.charAt(0);
            if (!Character.isDigit(L2.charAt(0))) {                
                Asccii2 = (int)L2.charAt(0);
                if (Asccii1>Asccii2) {
                    error="Invalid range declaration.";
                }
            }
            else
            {
                Asccii2 = Integer.parseInt(L2);
                if (Asccii1>Asccii2) {
                    error="Invalid range declaration.";
                }
            }
        }
        else
        {
            Asccii1 = Integer.parseInt(L1);//comentar
            if (!Character.isDigit(L2.charAt(0))) {                
                Asccii2 = (int)L2.charAt(0);
                if (Asccii1>Asccii2) {
                    error="Invalid range declaration.";
                }
            }
            else
            {
                Asccii2 = Integer.parseInt(L2);                
                if (Asccii1>Asccii2) {
                    error="Invalid range declaration.";
                }
            } 
        }
        
        if (Asccii1>255||Asccii1<0) {
            error="Unsupported range limit.";
        }
        if (Asccii2>255||Asccii2<0) {
            error="Unsupported range limit.";
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Automata">
    List<NodoExpresion> FirstLast = new ArrayList();
    Stack<String> Operador = new Stack<String>();
    Stack<NodoExpresion> Hoja = new Stack<NodoExpresion>();
    int contadorHoja=0;
    public void FirstLastFollow()
    {
        String TokenActual="";
        int NTokenActual = 0;
        for (int i = 0; i < Tokens.size(); i++) {
            TokenActual = Tokens.get(i);
            NTokenActual = Integer.parseInt(ListaNumeros.get(i).toString());
            char Elemento = ' ';
            for (int j = 0; j < TokenActual.length(); j++) 
            {
                Elemento = TokenActual.charAt(j);
                    if(Elemento == '\'')
                    {
                        //Agregar concatenacion si hay un ')', caracter, cerradura o conjunto.
                        if (EsConcatenacion(TokenActual,j)) {
                            Operador.push(".");
                        }
                        //Agregar elemento.
                        contadorHoja++;
                        j++;
                        Elemento = TokenActual.charAt(j);
                        Hoja.push(Agregar("'"+Elemento+"'",NTokenActual));
                        FirstLast.add(Agregar("'"+Elemento+"'",NTokenActual));
                        j++;
                    }
                    else if(Elemento == '"')
                    {
                        //Agregar concatenacion si hay un ')', caracter, cerradura o conjunto.
                        if (EsConcatenacion(TokenActual,j)) 
                        {
                            Operador.push(".");
                        }
                        //Agregar elemento.
                        contadorHoja++;
                        j++;
                        Elemento = TokenActual.charAt(j);
                        Hoja.push(Agregar("'"+Elemento+"'",NTokenActual));
                        FirstLast.add(Agregar("'"+Elemento+"'",NTokenActual));
                        j++;
                    }
                    else if(Character.isLetter(Elemento)) //Es un conjunto
                    {
                        //Agregar concatenacion si hay un ')', caracter, cerradura o conjunto.
                        if (EsConcatenacion(TokenActual,j)) {
                            Operador.push(".");
                        }
                        //Agregar elemento.
                        //Armar el nombre del conjunto
                        contadorHoja++;
                        String Cojnuto = ArmarNombreConjunto(Elemento+"", TokenActual,j);
                        Hoja.push(Agregar(Cojnuto,NTokenActual));
                        FirstLast.add(Agregar(Cojnuto,NTokenActual));
                        j=j+Cojnuto.length();
                        j--;
                    }
                    else if (Elemento == '|') 
                    {
                        //Si viene | debe operar todas las concatenaciones anteriores y luego agregarse en la pila.
                        if (!Operador.isEmpty()) {
                            if (Operador.peek().equals(".")) 
                            {
                                boolean BanderaConcat=false;
                                while(!BanderaConcat)
                                {
                                    //Concatenar
                                    Operador.pop();
                                    OperarConcatenacion();
                                    if (Operador.isEmpty()) {
                                        BanderaConcat= true;
                                    }
                                    else if (!Operador.peek().equals(".")) {
                                        BanderaConcat= true;
                                    }
                                }
                            }
                        }
                        
                        Operador.push("|");
                    }
                    else if(Elemento == '(')
                    {
                        //Agregar concatenacion si hay un ')', caracter, cerradura o conjunto.
                        if (EsConcatenacion(TokenActual,j)) {
                            Operador.push(".");
                        }
                        //Agregar operador
                        Operador.push("(");
                    }
                    else if(Elemento == ')')
                    {
                        //Operar todo lo de adentro.
                        while(!Operador.peek().equals("("))
                        {
                            if (Operador.peek().equals(".")) {
                                OperarConcatenacion();
                                Operador.pop();
                            }
                            else  if (Operador.peek().equals("|")) {
                                OperarO();
                                Operador.pop();
                            }
                        }
                        Operador.pop();
                    }
                    else if(Elemento == '*')
                    {
                        //Operar de inmediato
                        OperarCerradura("*");
                    }
                    else if(Elemento == '+')
                    {
                        //Operar de inmediato
                        OperarCerradura("+");
                    }
                    else if(Elemento == '?')
                    {
                        //Operar de inmediato
                        OperarCerradura("?");
                    }
                    else if(Elemento == ';')
                    {
                        //Operar todo
                        while(!Operador.isEmpty())
                        {
                            if (Operador.peek().equals(".")) {
                                OperarConcatenacion();
                                Operador.pop();
                            }
                            else  if (Operador.peek().equals("|")) {
                                OperarO();
                                Operador.pop();
                            }
                        }
                        
                        if (i==Tokens.size()-1) {
                            //Operar todo
                            while(!Operador.isEmpty())
                            {
                                if (Operador.peek().equals(".")) {
                                    OperarConcatenacion();
                                    Operador.pop();
                                }
                                else  if (Operador.peek().equals("|")) {
                                    OperarO();
                                    Operador.pop();
                                }
                            }
                            Operador.push(".");
                            
                            //Agregar asterisco.
                            contadorHoja++;
                            NodoExpresion Asterisco = new NodoExpresion();
                            Asterisco.Elemento="#";
                            Asterisco.First.add(contadorHoja+"");
                            Asterisco.Last.add(contadorHoja+"");
                            Asterisco.bNulable = false;
                            Hoja.add(Asterisco);
                            FirstLast.add(Asterisco);
                            
                            Operador.pop();
                            OperarConcatenacion();
                        }
                        else
                        {
                            Operador.push("|");
                        }
                        break;
                    }
            }
        }
    }
    
    public NodoExpresion Agregar(String Element, int T)
    {
        NodoExpresion E = new NodoExpresion();
        E.Elemento = Element;
        E.Token = T;
        E.First.add(contadorHoja+"");
        E.Last.add(contadorHoja+"");
        E.bNulable = false;
        return E;
    }
    
    public String ArmarNombreConjunto(String Element, String Expresion, int cont)
    {
        boolean bConjunto = false;
        boolean posibleconjunto= false;
        String Nombre = Element;
        while(!bConjunto)
        {
            if (!ConjuntosDeclarados.contains(Nombre)) 
            {
                if (Nombre.endsWith(";")) {
                    bConjunto = true;
                    break;
                }
                cont++;
                Nombre = Nombre + Expresion.charAt(cont);
                
            }
            else if(ConjuntosDeclarados.contains(Nombre))
            {
                bConjunto=true;
                //Verificar si no puede ser otro conjunto.
                String Aux = Nombre;
                cont++;
                Element = Nombre + Expresion.charAt(cont);
                Nombre = ArmarNombreConjunto(Element,Expresion,cont);
                if (Nombre.endsWith(";")) {
                    Nombre =Aux;
                }
            }
        }
        return Nombre;
    }
    
    public boolean EsConcatenacion(String Expresion, int cont)
    {
        if (cont==0) {
            return false;
        }
        boolean Bandera=false;
        char CaracterAnterior = Expresion.charAt(cont-1);
        if (CaracterAnterior == '*'||CaracterAnterior == '+'||CaracterAnterior == '?'||CaracterAnterior == '\''||CaracterAnterior == '"'||CaracterAnterior == ')')
        {
             Bandera=true;
        }
        else if (Character.isLetter(CaracterAnterior)||Character.isDigit(CaracterAnterior)||CaracterAnterior == '_') 
        {
             Bandera=true;
        }
        else if (CaracterAnterior == ' ') {
            Bandera = EsConcatenacion(Expresion,cont-1);
        }
        else
        {
            Bandera=false;
        }
        return Bandera;
    }
    
    public void OperarConcatenacion()
    {
        NodoExpresion Elemento1;
        NodoExpresion Elemento2;
        NodoExpresion Final = new NodoExpresion();
        Elemento2 = Hoja.peek();
        Hoja.pop();
        Elemento1 = Hoja.peek();
        Hoja.pop();
        Final.Elemento = Elemento1.Elemento+Elemento2.Elemento;
        //Calcular first
        if (Elemento1.bNulable) {
            for (int i = 0; i < Elemento1.First.size(); i++) {
                Final.First.add(Elemento1.First.get(i));
            }
            for (int i = 0; i < Elemento2.First.size(); i++) {
                Final.First.add(Elemento2.First.get(i));
            }
        }
        else if (!Elemento1.bNulable) {
            for (int i = 0; i < Elemento1.First.size(); i++) {
                Final.First.add(Elemento1.First.get(i));
            }
        }
        //Calcular last
        if (Elemento2.bNulable) {
            for (int i = 0; i < Elemento1.Last.size(); i++) {
                Final.Last.add(Elemento1.Last.get(i));
            }
            for (int i = 0; i < Elemento2.Last.size(); i++) {
                Final.Last.add(Elemento2.Last.get(i));
            }
        }
        else if (!Elemento2.bNulable) {
            for (int i = 0; i < Elemento2.Last.size(); i++) {
                Final.Last.add(Elemento2.Last.get(i));
            }
        }
        //Calcular nulabilidad
        if (Elemento1.bNulable&&Elemento2.bNulable) {
            Final.bNulable = true;
        }
        else
        {
            Final.bNulable = false;
        }
        //Datos para el follow
        Final.LastIzq=Elemento1.Last;
        Final.FirstDer=Elemento2.First;
        //Calcular follow
        //CalcularFollow();
        //Meter El ultimo
        Hoja.push(Final);
        FirstLast.add(Final);
    }
    
    public void OperarO()
    {
        NodoExpresion Elemento1;
        NodoExpresion Elemento2;
        NodoExpresion Final = new NodoExpresion();
        Elemento2 = Hoja.peek();
        Hoja.pop();
        Elemento1 = Hoja.peek();
        Hoja.pop();
        Final.Elemento = Elemento1.Elemento+"|"+Elemento2.Elemento;
        //Calcular first
        for (int i = 0; i < Elemento1.First.size(); i++) {
            Final.First.add(Elemento1.First.get(i));
        }
        for (int i = 0; i < Elemento2.First.size(); i++) {
            Final.First.add(Elemento2.First.get(i));
        }
        //Calcular last
        for (int i = 0; i < Elemento1.Last.size(); i++) {
            Final.Last.add(Elemento1.Last.get(i));
        }
        for (int i = 0; i < Elemento2.Last.size(); i++) {
            Final.Last.add(Elemento2.Last.get(i));
        }
        //Calcular nulabilidad
        if (!Elemento1.bNulable&&!Elemento2.bNulable) {
            Final.bNulable = false;
        }
        else
        {
            Final.bNulable = true;
        }
        //Meter El ultimo
        Hoja.push(Final);
        FirstLast.add(Final);
    }
    
    public void OperarCerradura(String Cerradura)
    {
        NodoExpresion Evaluar;
        NodoExpresion Final = new NodoExpresion();
        Evaluar = Hoja.peek();
        Hoja.pop();
        Final.Elemento = "("+Evaluar.Elemento+")"+Cerradura;
        Final.First = Evaluar.First;
        Final.Last = Evaluar.Last;
        if (Cerradura.equals("*")) {
            Final.bNulable= true;
            Final.FirstDer = Evaluar.First;
            Final.LastIzq = Evaluar.Last;
        }
        else if (Cerradura.equals("?")) {
            Final.bNulable= true;
        }
        else if (Cerradura.equals("+")) {
            Final.bNulable= false;
            Final.FirstDer = Evaluar.First;
            Final.LastIzq = Evaluar.Last;
        }
        Hoja.push(Final);
        FirstLast.add(Final);
    }
    
    public List<String> TablaFollow = new ArrayList();    
    public void CalcularFollow()
    {
        List<String> hojas = new ArrayList();
        List<List<String>> follows = new ArrayList();
        int contador = 0;
        for (NodoExpresion nodo : FirstLast) 
        {
            //NodoExpresion nodo = ne;
            if(!(nodo.LastIzq == null) || !(nodo.FirstDer == null))
            {
               for(String hoja : nodo.LastIzq)
               {
                   if(!hojas.contains(hoja))
                   {
                       hojas.add(hoja);
                       follows.add(nodo.FirstDer);
                   }
                   else
                   {
                       int indice = hojas.indexOf(hoja);
                       List<String> auxiliar = new ArrayList();
                       int cont = 0;
                       for(String s : follows.get(indice))
                       {
                           auxiliar.add(s);
                           cont++;
                       }
                       for(String f : nodo.FirstDer)
                       {
                           if(!auxiliar.contains(f))
                           {
                               auxiliar.add(f);
                           }
                       }
                       follows.set(indice, auxiliar);
                   }
               }
            }
        }
        for(String element : hojas)
        {
            String nex = element + ": " + follows.get(contador).toString();
            if(!TablaFollow.contains(nex))
            {
                TablaFollow.add(element + ": " + follows.get(contador).toString());
            }
            contador++;
        }
    }
    public void OrdenarLista(List<String> lista) 
    {
      boolean cambiado = true;
      int j = 0;
      String tmp;
      while (cambiado) 
      {
            cambiado = false;
            j++;
            for (int i = 0; i < lista.size() - j; i++) 
            {
                int num1 = Integer.parseInt(lista.get(i).substring(0, lista.get(i).indexOf(":")));
                int num2 = Integer.parseInt(lista.get(i+1).substring(0, lista.get(i+1).indexOf(":")));
                  if (num1 > num2) 
                  {                          
                        tmp = lista.get(i);
                        lista.set(i, lista.get(i + 1));
                        lista.set(i + 1, tmp);
                        cambiado = true;
                  }
            }                
      }
    }
    
    List<Hoja> LHojas = new ArrayList();
    List<Follow> LFollow = new ArrayList();
    List<List<String>> Estados = new ArrayList();
    List<Transicion> LTransicion =  new ArrayList();
    List<Transicion> LTransicionM =  new ArrayList();
    int HojaAceptacion=0;
    List<String> ListaTrans = new ArrayList();
    public void TablaTransiciones()
    {
        //Crear Lista de Hojas
        for (int i = 0; i < FirstLast.size(); i++) {
            if (EsHoja(FirstLast.get(i))) {
                Hoja H = new Hoja();
                H.Elemento = FirstLast.get(i).Elemento;
                H.Token = FirstLast.get(i).Token;
                H.First = FirstLast.get(i).First.get(0);
                LHojas.add(H);
            }
        }
        HojaAceptacion = Integer.parseInt(LHojas.get(LHojas.size()-1).First);
        //Crear Lista de Follows
        List<String> FollowLimpio = new ArrayList(); 
        for (int i = 0; i < TablaFollow.size(); i++) {
            FollowLimpio.add(TablaFollow.get(i).replace("[", "").replace("]", ""));
        }
        for (int i = 0; i < FollowLimpio.size(); i++) {
            Follow F = new Follow();
            F.Numero = FollowLimpio.get(i).split(":")[0];
            String[] Aux = new String[FollowLimpio.get(i).split(":")[1].split(",").length];
            Aux = FollowLimpio.get(i).split(":")[1].split(",");
            for (int j = 0; j < Aux.length; j++) {
                F.Follow.add(Aux[j].trim());
            }
            LFollow.add(F);
        }
        
        List<String> EstadoCero =  new ArrayList();
        //Obtener first de la raiz
        EstadoCero = FirstLast.get(FirstLast.size()-1).First;
        Estados.add(EstadoCero);
        for (int i = 0; i < LHojas.size(); i++) {
            for (int j = 0; j < EstadoCero.size(); j++) {
                if (LHojas.get(i).First.equals(EstadoCero.get(j))) {
                    Transicion T = new Transicion();
                    T.Elemento = LHojas.get(i).Elemento;
                    T.Transicion.add(LHojas.get(i).First);
                    T.Token = LHojas.get(i).Token;
                    LTransicion.add(T);
                }
            }
        }
        
        boolean Segura0=false;
        while(!Segura0)
        {
            for (int i = 0; i < LTransicion.size(); i++) {
                for (int j = 0; j < LTransicion.size(); j++) {
                    if (i!=j) {
                        if (LTransicion.get(i).Elemento.equals(LTransicion.get(j).Elemento)) {//revisar esto
                            LTransicion.get(i).Transicion.add(LTransicion.get(j).Transicion.get(0));
                            LTransicion.remove(j);
                            break;
                        }
                    }
                }
            }

            if (LTransicion.size()>1) {
                for (int i = 0; i < LTransicion.size(); i++) {
                    for (int j = 0; j < LTransicion.size(); j++) {
                        if (i!=j) {
                            if (LTransicion.get(i).Elemento.equals(LTransicion.get(j).Elemento)) {
                                Segura0 = false;
                                break;
                            }
                            else
                            {
                                Segura0 = true;
                            }
                        }
                    }
                    if (!Segura0) {
                        break;
                    }
                }
            }
            else
            {
                Segura0 = true;
            }

        }
        
        for (int i = 0; i < LTransicion.size() ; i++) {
            for (int j = 0; j < LTransicion.get(i).Transicion.size(); j++) {            
                for (int k = 0; k < LFollow.size(); k++) {
                    if (LTransicion.get(i).Transicion.get(j).equals(LFollow.get(k).Numero)) {                        
                        for (int l = 0; l < LFollow.get(k).Follow.size(); l++) {
                            if (!LTransicion.get(i).TransicionFollow.contains(LFollow.get(k).Follow.get(l))) {
                                LTransicion.get(i).TransicionFollow.add(LFollow.get(k).Follow.get(l)); 
                            }
                        }
                    }
                }
            }
        }
       
        //if (EstadoCero.contains(HojaAceptacion+"")) { 
        //    for (int i = 0; i < LTransicion.size(); i++) {
        //    LTransicion.get(i).Aceptacion=true;
        //    LTransicionM.add(LTransicion.get(i));
        //}
        //}
        
        for (int i = 0; i < LTransicion.size(); i++) {
            LTransicion.get(i).EstadoInicial = "S0";
            if (EstadoCero.contains(HojaAceptacion+"")) {
                LTransicion.get(i).Aceptacion=true;
            }
            LTransicionM.add(LTransicion.get(i));
        }
                
        for (int i = 0; i < LTransicion.size(); i++) {
            if (!Estados.contains(LTransicion.get(i).TransicionFollow)) {
                if (!LTransicion.get(i).TransicionFollow.isEmpty()) {
                    Estados.add(LTransicion.get(i).TransicionFollow);  
                }
            }
            
        }
        LTransicion.clear();
        
        //S1...Sn
        for (int m = 1; m < Estados.size(); m++) {
            EstadoCero = Estados.get(m);
            
            for (int i = 0; i < LHojas.size(); i++) {
                for (int j = 0; j < EstadoCero.size(); j++) {
                    if (LHojas.get(i).First.equals(EstadoCero.get(j))) {
                        Transicion T = new Transicion();
                        T.Elemento = LHojas.get(i).Elemento;
                        T.Transicion.add(LHojas.get(i).First);
                        T.Token = LHojas.get(i).Token;
                        LTransicion.add(T);
                    }
                }
            }

            boolean Segura=false;
            while(!Segura)
            {
                for (int i = 0; i < LTransicion.size(); i++) {
                    for (int j = 0; j < LTransicion.size(); j++) {
                        if (i!=j) {
                            if (LTransicion.get(i).Elemento.equals(LTransicion.get(j).Elemento)) {//revisar esto
                                LTransicion.get(i).Transicion.add(LTransicion.get(j).Transicion.get(0));
                                LTransicion.remove(j);
                                break;
                            }
                        }
                    }
                }
                
                if (LTransicion.size()>1) {
                    for (int i = 0; i < LTransicion.size(); i++) {
                        for (int j = 0; j < LTransicion.size(); j++) {
                            if (i!=j) {
                                if (LTransicion.get(i).Elemento.equals(LTransicion.get(j).Elemento)) {
                                    Segura = false;
                                    break;
                                }
                                else
                                {
                                    Segura = true;
                                }
                            }
                        }
                        if (!Segura) {
                            break;
                        }
                    }
                }
                else
                {
                    Segura = true;
                }
                
            }
            

            for (int i = 0; i < LTransicion.size() ; i++) {
                for (int j = 0; j < LTransicion.get(i).Transicion.size(); j++) {            
                    for (int k = 0; k < LFollow.size(); k++) {
                        if (LTransicion.get(i).Transicion.get(j).equals(LFollow.get(k).Numero)) {                        
                            for (int l = 0; l < LFollow.get(k).Follow.size(); l++) {
                                if (!LTransicion.get(i).TransicionFollow.contains(LFollow.get(k).Follow.get(l))) {
                                    LTransicion.get(i).TransicionFollow.add(LFollow.get(k).Follow.get(l)); 
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < LTransicion.size(); i++) {
                LTransicion.get(i).EstadoInicial = "S"+m;
                
                if (EstadoCero.contains(HojaAceptacion+"")) {
                    LTransicion.get(i).Aceptacion=true;
                }
                LTransicionM.add(LTransicion.get(i));
            }
            
            //agregar estados
            for (int i = 0; i < LTransicion.size(); i++) {
                if (!Estados.contains(LTransicion.get(i).TransicionFollow)) {
                    if (!LTransicion.get(i).TransicionFollow.isEmpty()) {
                        Estados.add(LTransicion.get(i).TransicionFollow);  
                    }
                }
            }
            LTransicion.clear();
        }
        
        List<Transicion> LTransicionAux =  new ArrayList();
        for (int i = 0; i < LTransicionM.size(); i++) {
            LTransicionAux.add(LTransicionM.get(i));
        }
        
        for (int i = 0; i < LTransicionM.size(); i++) {
            for (int j = 0; j < Estados.size(); j++) {
                boolean Banderita=false;
                int k=0;
                int TT=LTransicionM.get(i).TransicionFollow.size();
                int TE=Estados.get(j).size();
                while(!Banderita)
                {
                    if (TE==0) {
                        Banderita=true;
                    }
                    if (k>TT-1) {
                        break;
                    }
                    else if (k>TE-1) {
                        break;
                    }
                    if (Integer.parseInt(LTransicionM.get(i).TransicionFollow.get(k)) != Integer.parseInt(Estados.get(j).get(k))) {
                        Banderita=true;
                    }      
                    k++;
                }
                if (!Banderita) {
                    LTransicionM.get(i).EstadoFinal="S"+j;
                }
            }
        }
        LlenarListaTrans();
    }
    private void LlenarListaTrans()
    {
        for (int i = 0; i < LTransicionM.size(); i++) 
        {
            String transi = LTransicionM.get(i).EstadoInicial+"~"+ LTransicionM.get(i).Elemento+"_"+ LTransicionM.get(i).Token+"~"+ LTransicionM.get(i).EstadoFinal + "~" + LTransicionM.get(i).Aceptacion;
            if(!ListaTrans.contains(transi))
            {
                ListaTrans.add(transi);
            }
        }
    }
    public boolean EsHoja(NodoExpresion Nodo)
    {
        boolean bandera = false;
        if (Nodo.First.size() == 1) 
        {
            if (Nodo.Last.size() == 1) 
            {
                if (Nodo.FirstDer.isEmpty()) 
                {
                    if (Nodo.LastIzq.isEmpty()) 
                    {
                        if (!Nodo.Elemento.contains("+")&&!Nodo.Elemento.contains("*")&&!Nodo.Elemento.contains("?")) {
                            bandera = true;   
                        }
                    }
                }
            }
        }
        return bandera;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Codigo">
    List<String> TAcciones = new ArrayList();
    public void AccionesNecesarias()
    {
        for (int i = 0; i < ContenidoAcciones.size(); i++) {
            boolean Bandera = false;
            if (ContenidoAcciones.isEmpty()) {
                break;
            }
            if (Character.isLetter(ContenidoAcciones.get(i).toString().charAt(0))) // Si es letra que agregue todo los elementos siguientes
            {
                for (int j = 0; j < AccionesTokens.size(); j++) {
                    if (ContenidoAcciones.get(i).toString().equals(AccionesTokens.get(j)+":")) {
                        while(!Bandera)
                        {
                            i++; 
                            if (i>ContenidoAcciones.size()-1) {
                                break;
                            }                         
                            if (Character.isLetter(ContenidoAcciones.get(i).toString().charAt(0))) // Si es letra que agregue todo los elementos siguientes
                            {
                                Bandera = true;
                            }
                            else
                            {
                                TAcciones.add(ContenidoAcciones.get(i).toString());
                            }
                        }
                        i++;
                    }
                }
            }
        }
    }
    
    public String CodigoConjuntos()
    { 
        String Code="";
        for (int i = 0; i < ConjuntosDeclarados.size(); i++) {
            String[] Contenido = new String[1];
            String Cont="";
            if (!Elementos.get(i).toString().contains("+")) {
                Contenido[0] = Elementos.get(i).toString();
            }
            else
            {
                //String usefulData = Elementos.get(i).toString();
                //String[] list = null;
                //String token = "+";
                //list = usefulData.split(token);
                Cont = Elementos.get(i);
                Contenido = new String[Cont.split("\\+").length];
                Contenido = Cont.split("\\+");                
            }
            Code+="public List<int> "+ConjuntosDeclarados.get(i)+" = new List<int>();\n";
            Code+="public void Llenar"+ConjuntosDeclarados.get(i)+"()\n";
            Code+="{\n";
            for (int j = 0; j < Contenido.length; j++) {
                if (Contenido[j].contains("..")) {
                    String puntos="";
                    String[] Limites = new String[2];
                    int Asccii1=0, Asccii2=0;
                    //Limites =Contenido[j].split("..");
                    Limites[0]=Contenido[j].substring(0,Contenido[j].indexOf("."));
                    Limites[1]=Contenido[j].substring(Contenido[j].indexOf(".")+2,Contenido[j].length());
                    //Limite 1
                    if (Limites[0].startsWith("'")) {
                        Asccii1 = (int)Limites[0].charAt(1);
                    }
                    else if (Limites[0].startsWith("\"")) {
                        Asccii1 = (int)Limites[0].charAt(1);
                    }
                    else if (Limites[0].startsWith("c")) {
                        String num="";
                        //num = Limites[0].split("(")[1].split(")")[0];
                        num = Limites[0].substring( Limites[0].indexOf("(")+1, Limites[0].indexOf(")"));
                        Asccii1 = Integer.parseInt(num);
                    }
                    //Limite 2
                    if (Limites[1].startsWith("'")) {
                        Asccii2 = (int)Limites[1].charAt(1);
                    }
                    else if (Limites[1].startsWith("\"")) {
                        Asccii2 = (int)Limites[1].charAt(1);
                    }
                    else if (Limites[1].startsWith("c")) {
                        String num="";
                        num = Limites[1].substring( Limites[1].indexOf("(")+1,  Limites[1].indexOf(")"));
                        Asccii2 = Integer.parseInt(num);
                    }
                    for (int k = Asccii1; k < Asccii2+1; k++) {
                        Code+="\t"+ConjuntosDeclarados.get(i)+".Add("+k+");\n";
                    }
                }
                else
                {
                    int Asccii=0;
                    if (Contenido[j].startsWith("'")||Contenido[j].startsWith("\"")) 
                    {
                        Asccii = (int)Contenido[j].charAt(1);
                        Code+="\t"+ConjuntosDeclarados.get(i)+".Add("+Asccii+");\n";
                    }
                    else if (Contenido[j].startsWith("c")) 
                    {
                        String num="";
                        num = Contenido[j].substring(Contenido[j].indexOf("(")+1, Contenido[j].indexOf(")"));
                        Asccii = Integer.parseInt(num);
                    }
                    
                    
                    
                    Code+="\t"+ConjuntosDeclarados.get(i)+".Add("+Asccii+");\n";
                }
            }
            Code+="}\n";
        }
        return Code;
    }
    public void GenerarCodigoC() throws IOException
    {
        Automata generadorC = new Automata(ListaTrans, TAcciones, ConjuntosDeclarados, Elementos, nombreArchivo.substring(0, nombreArchivo.indexOf(".")), ListaNumeros.get(ListaNumeros.size()-1).toString());
        generadorC.EsbribirCodigoCS();
        generadorC.EsbribirCodigoCPP();
    }
    // </editor-fold>
}