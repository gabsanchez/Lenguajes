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
    byte[] buffer1 = null;
    byte[] buffer2 = null;
    
    public Archivo()
    {
        fd = new FileDialog(form, "Abrir archivo", FileDialog.LOAD);
    }
    public void Cargar() throws IOException
    {
        fd.setVisible(true);
        nombreArchivo = fd.getDirectory() + fd.getFile();
        Leer(nombreArchivo, 1);
    }
    public void Leer(String nombreA, int size) throws FileNotFoundException, IOException
    {
        lector1 = new RandomAccessFile(nombreA, "r");
        lector2 = new RandomAccessFile(nombreA, "r");
        buffer1 = new byte[size];
        buffer2 = new byte[size];
        System.out.println(lector1.length());
        lector1.seek(25);
        lector2.seek(26);
        lector1.read(buffer1);
        lector2.read(buffer2);
        lector1.close();
        lector2.close();
        System.out.println(new String(buffer1));
        System.out.println(new String(buffer2));
    }
}
