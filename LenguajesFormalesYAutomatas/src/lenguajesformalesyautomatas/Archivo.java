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
    
    public String error = "";
    
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
                    if (caracterA.equals("a")) 
                    {
                        cont++;
                        Leer(nombreArchivo, 7, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("cciones"))
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
            }
            else
            {
                cont++;
            }
        }
        
    }
    public long AnalizarToken(long cont) throws IOException
    {
        String caracterA = "";
        boolean banderaNumero = false;
        boolean banderaIgual = false;
        coma: while(!caracterA.equals(";"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (Character.isDigit(caracterA.charAt(0))) //Encontrar numero
            {
                while(Character.isDigit(caracterA.charAt(0)))
                {
                    cont++; 
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                }                       
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
                    cont = EvaluarExpresion(cont, flag);//Se evalua la expresion regular
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
        return !caracterA.equals(" ") && !caracterA.equals("\t") && !caracterA.equals("\n");
    }
    public long AnalizarConjunto(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        boolean banderaID = false;
        while(!caracterA.equals("{"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            
            if (Character.isDigit(caracterA.charAt(0)) && !banderaID)
            {
                error = "Invalid group name";
                break;
            }
            else
            {
               if(EsCaracter(cont)) 
               {
                   cont++;
                   banderaID = true;
               }
               else
               {
                   cont = ComerEspacio(cont);
               }
            } 
        }
        return cont;
    }
    private long ValidarParentesis(long cont, long fin, boolean banderaO, boolean banderaC) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer).toLowerCase();
        loop: while(cont < fin)
        {
            switch (caracterA) 
            {
                case "(":
                    error = ") Expected";
                    cont = ValidarParentesis(cont + 1, fin, true, false);
                    break;
                case ")":
                    banderaC = true;
                    if(banderaO && banderaC)
                    {
                        error = "";
                        cont++;
                        break;
                    }
                    else
                    {
                        error = "( Missing";
                        break loop;
                    }
                default:
                    cont = ValidarParentesis(cont + 1, fin, banderaO, banderaC);
                    break;
            }
        }
        return cont;
    }
    private long EvaluarExpresion(long cont, long posBandera) throws IOException
    {
        boolean bandera = false; //Valida que una expresion sea correcta para no volverla a evaluar
        long inicio = cont;
        cont = ValidarParentesis(cont, posBandera, false, false);
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
                        error = "Closing ' expected " + cont;
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
        
        return cont;
    }
}
