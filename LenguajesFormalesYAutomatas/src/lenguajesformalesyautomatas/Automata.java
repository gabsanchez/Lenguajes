/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajesformalesyautomatas;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gabriel
 */
public class Automata {
    List<String> Estados = new ArrayList();
    List<String> Transiciones = new ArrayList();
    FileWriter fwCS;
    FileWriter fwCPP;
    String codigo;
    public Automata(List<String> estados, String ruta) throws IOException
    {
        for(String s : estados)
        {
            String[] aux = s.split("|");
            Estados.add(aux[0]);
            Transiciones.add(aux[1] + "|" + aux[2]);
        }
        fwCS = new FileWriter(ruta + ".cs");
        fwCPP = new FileWriter(ruta + ".cpp");
    }
}
