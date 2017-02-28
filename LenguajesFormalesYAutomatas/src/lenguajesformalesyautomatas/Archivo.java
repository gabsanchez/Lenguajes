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
    int fila = 0, columna = 0;
    
    long inicioExp;
    long finExp;
    long parentesis;
    
    List ListaNumeros = new ArrayList();
    public String error = "";
    
    List Acciones = new ArrayList();
    
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
                    if (caracterA.equals("oken "))
                    {
                        cont = cont + 5;
                        cont = AnalizarToken(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    if(banderaInicial)
                    {
                        //es conjunto
                        cont = AnalizarConjunto(cont);
                        if(!error.equals(""))
                        {
                            break;
                        }
                    }
                    else
                    {
                        error = "TOKENS expected.";
                        break;
                    }
                }
            }
            else if(cont == tamArchivo)
            {
                error = "TOKENS expected.";
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
                            break;
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
                    error = "TOKENS expected.";
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
    private void AgregarNumero(long num)
    {
        boolean NoExiste = true;
        for (Object ListaNumero : ListaNumeros) 
        {
            if(ListaNumero.equals(num))
            {
                NoExiste = false;
                error = "Number already taken";
                break;
            }
        }
        if(NoExiste)
        {
            ListaNumeros.add(num);
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
                AgregarNumero(numero);
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
                    cont = ComerEspacio(cont);
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
                                    error = "';' expected";
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
                                    error = "';' expected";
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
                                    error = "';' expected";
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
                    cont = ComerEspacio(cont);
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
                    error = "= expected";
                    break;
                }
            }
            else
            {
                if (EsCaracter(cont)) 
                {
                    error = "Token number expected";
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
        if (caracterA.equals("{")) {
            error = "Invalid group name.";
            return cont;
        }
        while(!caracterA.equals("{"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            
            if (Character.isDigit(caracterA.charAt(0)) && !banderaID)
            {
                if (banderaNombre) {
                    error = "{ expected.";
                    break;
                }
                else
                {
                    error = "Invalid group name.";
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
                else if (EsCaracter(cont) && !caracterA.equals("{")) {
                    error = "Invalid group name.";
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
                    error = "{ expected.";
                    break;
                }
            }
            else
            {
                error = "Invalid group name.";
                break;
            }
        }
        
        if (error == "") //si no hay error evaluar el contenido
        {
            cont++;
            
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
                        error = "' expected";
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
                        error = "\" expected";
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
                        error = ". expected";
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
                        error = "Invalid gropu element.";
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
                            error = "Bad use +.";
                            break;
                        }
                    }
                    else if(EsCaracter(cont)&&!caracterA.equals("}"))
                    {
                        error = "Invalid group element.";
                        break;
                    }

                }
                
                if (contElementos > 1) {
                    error = "+ expected.";
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
                            else if (EsCaracter(Aux)) {
                                error = "Range definition error.";
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
                            if (caracterA.equals("\"")) {
                                PrimerElemento=true;
                            }
                            else if (EsCaracter(Aux)) {
                                error = "Range definition error.";
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
                            if (caracterA.equals(")")) {
                                PrimerElemento=true;
                            }
                            else if (EsCaracter(Aux)) {
                                error = "Range definition error.";
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
                            contElementos++;
                        }
                        else
                        {
                            error = "Invalid gropu element.";
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
                                    error = "Invalid gropu element.";
                                    break;
                                }
                            }
                            else if(EsCaracter(cont)) {
                                error = "Range definition error.";
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
                                error = "' expected";
                                return cont;
                            }
                        }
                        else if (caracterA.equals("\"")) 
                        {
                            Leer(nombreArchivo, 1, cont+2);
                            caracterA = new String(buffer).toLowerCase();
                            if (caracterA.equals("\"")) {
                                cont = cont+3;                                
                            }
                            else
                            {
                                error = "\" expected";
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
                else if (EsCaracter(Aux)) {
                    error = "Range definition error.";
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
                else if (!caracterA.equals("(")) {
                    error = "( expected.";
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
            else if (!caracterA.equals(")")) {
                error = ") expected.";
                break;
            }
        }
        if (!banderaNum&&!banderaYano) {
            error = "number expected.";
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
                            if (banderaNombre) {
                                error = "( expected.";
                                break;
                            }
                            else
                            {
                                error = "Invalid action name.";
                                break;
                            }
                        }
                        else if (Character.isLetter(caracterA.charAt(0)) && !banderaID)
                        {
                            if (banderaNombre) {
                                error = "( expected.";
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
                                error = "Invalid action name.";
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
                                    error = ") expected.";
                                    break;
                                }
                            }
                            else if (caracterA.equals("{") && banderaParentesis) {
                                cont++;
                            }
                            else
                            {
                                if (!banderaParentesis) {
                                    error = "( expected.";
                                    break;
                                }
                                else
                                {
                                    error = "{ expected.";
                                    break;
                                }
                            }
                        }
                        else
                        {
                            error = "Invalid action name.";
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
                        if (banderaNombre) {
                            error = "( expected.";
                            break;
                        }
                        else
                        {
                            error = "Invalid action name.";
                            break;
                        }
                    }
                    else if (Character.isLetter(caracterA.charAt(0)) && !banderaID)
                    {
                        if (banderaNombre) {
                            error = "( expected.";
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
                            error = "Invalid action name.";
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
                                error = ") expected.";
                                break;
                            }
                        }
                        else if (caracterA.equals("{") && banderaParentesis) {
                            cont++;
                        }
                        else
                        {
                            if (!banderaParentesis) {
                                error = "( expected.";
                                break;
                            }
                            else
                            {
                                error = "{ expected.";
                                break;
                            }
                        }
                    }
                    else
                    {
                        error = "Invalid action name.";
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
        return cont;
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
                if (banderaIgual) {
                    error = "' or \" expected.";
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
                        error = "= expected.";
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
                    else if (banderaIgual) {
                        error = "' or \" expected.";
                        break;
                    }

                }
                else if (!banderaNum) {
                    error = "number expected.";
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
                            else if (caracterA.equals("")) {
                                error="' expected.";
                                break;
                            }
                        }
                        cont++;
                    }
                    else if (banderaComilla) {
                        error="number or } expected.";
                        break;
                    }
                    
                }
                else if (!banderaIgual) {
                    error = "= expected.";
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
                            else if (caracterA.equals("")) {
                                error="\" expected.";
                                break;
                            }
                        }
                        cont++;
                    }
                    else if (banderaComilla) {
                        error="number or } expected.";
                        break;
                    }
                    
                }
                else if (!banderaIgual) {
                    error = "= expected.";
                    break;
                }
            }
            else if (!EsCaracter(cont)) {
                cont++;
                if (banderaComilla) {
                    banderaNum=false;
                    banderaIgual=false;
                    banderaComilla=false;
                    Token="";
                }
            }
            else if (EsCaracter(cont)) {
                if (!caracterA.equals("}")) {
                    error = "Invalid char.";
                    break;
                }
                else if(caracterA.equals("}"))
                { 
                    if (banderaComilla) {
                        banderaNum=false;
                        banderaIgual=false;
                        banderaComilla=false;
                        Token="";
                    }
                    if (banderaComilla || banderaIgual || banderaNum) 
                    {
                        error = "Invalid char.";
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
                            bandera = true;
                        }
                        else
                        {
                            error = "Closing ' expected";
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
                            bandera = true;
                        }
                        else
                        {
                            error = "Closing \" expected";
                            break loop;
                        }
                        break;
                    }
                    case "*":
                    {
                        if(bandera)
                        {
                            cont = EvaluarExpresion(inicio, cont);
                            cont--;
                        }
                        else
                        {
                            error = "Invalid regex symbol '*'";
                        }
                        break;
                    }
                    case "+":
                    {
                        cont = EvaluarExpresion(inicio, cont);
                        Leer(nombreArchivo, 1, cont - 1);
                        caracterA = new String(buffer).toLowerCase();
                        if(!caracterA.equals(")") && !caracterA.equals("'") && !caracterA.equals("\""))
                        {
                            error = "Invalid regex symbol '+'";
                        }
                        break;
                    }
                    case "?":
                    {
                        cont = EvaluarExpresion(inicio, cont);
                        Leer(nombreArchivo, 1, cont - 1);
                        caracterA = new String(buffer).toLowerCase();
                        if(!caracterA.equals(")") && !caracterA.equals("'") && !caracterA.equals("\"") /*&& nombre de conjunto invalido*/)
                        {
                            error = "Invalid regex symbol '?'";
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
                            error = "'" + caracterBandera + "'" + "expected";
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
                            error = "'" + caracterBandera + "'" + "expected";
                            break loop;
                        }
                        break;
                    }
                    case "[":
                    {
                        Leer(nombreArchivo, 1, posBandera);
                        String caracterBandera = new String(buffer).toLowerCase();
                        error = "'" + caracterBandera + "'" + " expected";
                        break loop;
                    }
                    case ";":
                    {
                        Leer(nombreArchivo, 7, posBandera);
                        String caracterBandera = new String(buffer).toLowerCase();
                        error = "'" + caracterBandera + "'" + " expected";
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
                            error = "'" + caracterBandera + "'" + "expected";
                            break loop;
                        }
                        break;
                    }
                }
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
            }
        }
        else if(parentesis < 0)
        {
            error = "( Missing";
        }
        else
        {
            error = ") Expected";
        }
        return cont;
    }
    private long TokensEspeciales(long cont) throws IOException
    {
        cont = ComerEspacio(cont);
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        if(Character.isDigit(caracterA.charAt(0)))
        {
            error = "Invalid action name";
        }
        else
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            while(!caracterA.equals("("))
            {
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer).toLowerCase();
                if(!caracterA.equals(")"))
                {
                    error = ") Expected";
                    break;
                }
                else
                {
                    cont = cont + 2;
                }
            }
            cont = ComerEspacio(cont);
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if(!caracterA.equals("]"))
            {
                error = "] Expected";
            }
        }
        return cont;
    }
    
    public long AnalizarError(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        boolean banderaFinal=false,banderaIgual=false, banderaNum=false;
        while(!banderaFinal)
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (!EsCaracter(cont)) {
                cont++;
                if (banderaIgual && banderaNum) {
                    
                }
            }
            else if (caracterA.equals("=")) {
                banderaIgual=true;
                cont++;
            }
            else if (Character.isDigit(caracterA.charAt(0))) {
                if (banderaIgual) {
                    if (!banderaNum) {
                        banderaNum=true;
                        cont++;
                    }
                    else if (banderaNum) {
                        
                    }
                }
                else if (!banderaIgual) {
                    error="= expected.";
                    break;
                }
            }
            else if (EsCaracter(cont)) {
                if (banderaNum) {
                    
                }
                else if (!banderaNum) {
                    error="invalid character.";
                    break;
                }
                else if (!banderaIgual) {
                    error="invalid character.";
                    break;
                }
            }
        }
        return cont;
    }
}
