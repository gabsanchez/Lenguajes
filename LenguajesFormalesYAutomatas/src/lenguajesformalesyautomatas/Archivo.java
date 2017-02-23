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
                    //Leer(nombreArchivo, 1, cont);
                    //caracterA = new String(buffer).toLowerCase();
                    if (caracterA.equals("r")) 
                    {
                        cont++;
                        Leer(nombreArchivo, 9, cont);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("eservadas"))
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
        while(!caracterA.equals(";"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer).toLowerCase();
            if (Character.isDigit(caracterA.charAt(0))) {
                while(Character.isDigit(caracterA.charAt(0)))
                {
                    cont++; 
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer).toLowerCase();
                }                       
                banderaNumero = true;
            }
            else if (banderaNumero) 
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
                    //Analizar Expresiones.
                    if (caracterA.equals("'")) {
                        Leer(nombreArchivo, 1, cont+2);
                        caracterA = new String(buffer).toLowerCase();
                        if (caracterA.equals("'")) {
                            cont = cont+3;
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
                        }
                        else
                        {
                            error = "\" expected";
                            break;
                        }
                    }
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
}
