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
    RandomAccessFile lector1 = null;
    RandomAccessFile lector2 = null;
    Principal form = new Principal();
    String nombreArchivo;
    long tamArchivo;
    byte[] buffer1 = null;
    byte[] buffer2 = null;
    
    String error = "";
    
    public Archivo()
    {
        fd = new FileDialog(form, "Abrir archivo", FileDialog.LOAD);
    }
    public void Cargar() throws IOException
    {
        fd.setVisible(true);
        nombreArchivo = fd.getDirectory() + fd.getFile();
        //Leer(nombreArchivo, 15);
        Analizar();
    }
    public void Leer(String nombreA, int size, long pos) throws FileNotFoundException, IOException
    {
        lector1 = new RandomAccessFile(nombreA, "r");
        lector2 = new RandomAccessFile(nombreA, "r");
        buffer1 = new byte[size];
        buffer2 = new byte[size];
        tamArchivo = lector1.length();
        lector1.seek(pos);
        lector2.seek(pos);
        lector1.read(buffer1);
        lector2.read(buffer2);
        lector1.close();
        lector2.close();
        System.out.println(new String(buffer1));
        System.out.println(new String(buffer2));
    }
    public void Analizar() throws IOException
    {
        long cont = 0;
        boolean banderaInicial = false; //Determina cuando llegamos al inicio de lo que nos interesa.
        boolean banderaFinal = false;
        while(!banderaFinal)
        {
            Leer(nombreArchivo, 1, cont);
            String caracterA = new String(buffer1).toLowerCase();
            if(caracterA.equals("t"))
            {
                cont++;
                Leer(nombreArchivo, 1, cont);
                caracterA = new String(buffer1).toLowerCase();
                if(caracterA.equals("o"))
                {
                    cont++;
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer1).toLowerCase();
                    if(caracterA.equals("k"))
                    {
                        cont++;
                        Leer(nombreArchivo, 1, cont);
                        caracterA = new String(buffer1).toLowerCase();
                        if(caracterA.equals("e"))
                        {
                            cont++;
                            Leer(nombreArchivo, 1, cont);
                            caracterA = new String(buffer1).toLowerCase();
                            if(caracterA.equals("n"))
                            {
                                if (banderaInicial) 
                                {
                                    //Analizar token
                                    cont++;
                                    Leer(nombreArchivo, 1, cont);
                                    caracterA = new String(buffer1).toLowerCase();
                                    if(caracterA.equals(" "))
                                    {
                                        AnalizarToken(cont);
                                    }
                                }
                                else
                                {
                                    cont++;
                                    Leer(nombreArchivo, 1, cont);
                                    caracterA = new String(buffer1).toLowerCase();
                                    if(caracterA.equals("s"))
                                    {
                                        banderaInicial = true;
                                    
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(cont == tamArchivo)
            {
                error = "TOKENS expected.";
                banderaFinal = true;
            }
            else
            {
                //Es conjunto.
                cont++;
            }
            
        }
    }
    public void AnalizarToken(long cont) throws IOException
    {
        String caracterA = "";
        boolean banderaNumero = false, banderaIgual = false;
        while(!caracterA.equals(";"))
        {
            Leer(nombreArchivo, 1, cont);
            caracterA = new String(buffer1).toLowerCase();
            if (Character.isDigit(caracterA.charAt(0))) {
                while(Character.isDigit(caracterA.charAt(0)))
                {
                    cont++; 
                    Leer(nombreArchivo, 1, cont);
                    caracterA = new String(buffer1).toLowerCase();
                }                       
                banderaNumero = true;
            }
            else if (banderaNumero) {
                if (caracterA.equals("=")) {
                    cont++;
                    banderaIgual = true;
                }
                if (banderaIgual) {
                    //Analizar Expresiones.
                }
                else{
                cont = ComerEspacios(cont);
            }
            }
            else{
                cont = ComerEspacios(cont);
            }
            
        }
    }
    
    public long ComerEspacios(long cont) throws IOException
    {
        Leer(nombreArchivo, 1, cont);
        String caracterA = new String(buffer1).toLowerCase();
        if(caracterA.equals(" ")) {
            cont++;
        }
        else if(caracterA.equals("\t")) {
            cont++;
        }
        else if(caracterA.equals("\n")) {
            cont++;
        }
        return cont;
    }
}
