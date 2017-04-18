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
    List<List<String>> Transiciones = new ArrayList();
    List<String> Reservadas = new ArrayList();
    FileWriter fwCS;
    FileWriter fwCPP;
    String codigo;
    public Automata(List<String> estados, List<String> res, String nombre) throws IOException
    {
        for(String s : estados)
        {
            String[] aux = s.split("|");
            Estados.add(aux[0].substring(1));//Los estados están escritos como "S0, S1..." entonces obtenemos solamente el número sin la "S"
            List<String> temporal = new ArrayList();
            for(String e : estados)
            {
                String[] aux1 = e.split("|");
                if(aux1[0].equals(aux[0]))
                {
                    temporal.add(aux1[1] + "|" + aux1[2]);
                }
            }
            Transiciones.add(temporal);
        }
        fwCS = new FileWriter(nombre + ".cs");
        fwCPP = new FileWriter(nombre + ".cpp");
        fwCS.write("");
    }
    public void EsbribirCodigoCS() throws IOException
    {
        fwCS.write("public void CargarArchivo(string nombreArchivo)\n" +
        "{\n" +
        "    // Variables locales\n" +
        "    FileInfo informacionArchivo = null;\n" +
        "    StreamReader lectorArchivo = null;\n" +
        "\n" +
        "    string linea = \"\";\n" +
        "    \n" +
        "    //Se quitan los espacios del nombre del archivo\n" +
        "    nombreArchivo = nombreArchivo.Trim();\n" +
        "\n" +
        "    //Se valida que se ingrese un nombre de archivo\n" +
        "    if (nombreArchivo.CompareTo(\"\") == 0)\n" +
        "    {\n" +
        "        throw new Exception(\"Se debe ingresar un nombre de archivo.\");\n" +
        "    }\n" +
        "\n" +
        "    //Se valida que el archivo exista\n" +
        "    informacionArchivo = new FileInfo(nombreArchivo);\n" +
        "    \n" +
        "    if (!informacionArchivo.Exists)\n" +
        "    {\n" +
        "        throw new Exception(\"Debe ingresar un nombre de archivo existente.\");\n" +
        "    }\n" +
        "\n" +
        "    //Se abre el archivo para lectura\n" +
        "    lectorArchivo = new StreamReader(nombreArchivo);\n" +
        "\n" +
        "    //Se recorre el archivo guardando su contenido en memoria\n" +
        "    char[] vacios = {' ', '\\t'};\n" +
        "    while (linea != null)\n" +
        "    {\n" +
        "        // Se obtiene la siguiente linea\n" +
        "        linea = lectorArchivo.ReadLine();\n" +
        "        //Separamos cada palabra por caracteres vacíos (espacios, saltos de línea y tabulaciones)\n" +
        "        string[] palabras = linea.split(vacios);\n" +
        "        foreach (string s in palabras)\n" +
        "        {\n" +
        "            Tomatoken(s);\n" +
        "        }\n" +
        "    }\n" +
        "}");
        fwCS.write("private int Tomatoken(string palabra)\n" +
        "{\n" +
        "    int estado = 0;\n" +
        "    int contador = 0;\n" +
        "    int tacos = 0; //token\n" +
        "    string[] Reservadas = {" + ListaCadena(Reservadas) + "};\n" +
        "    long[] numReservadas = new long[Reservadas.Count];\n" +
        "    string salida = \"\";\n" +
        "    //Verificar si la palabara es reservada\n" +
        "    for (int i = 0; i < Reservadas.Count; i++)\n" +
        "    {\n" +
        "        string[] aux = Reservadas[i].split('=');\n" +
        "        numReservadas[i] = Convert.ToInt64(aux[0]);\n" +
        "        if(aux.Count > 2)\n" +
        "        {\n" +
        "            string pal = \"\";\n" +
        "            for(int j = 1; j < aux.Count; j++)\n" +
        "            {\n" +
        "                pal = pal + aux[j] + \"=\";\n" +
        "            }\n" +
        "            pal = pal.Substring(1, pal.Length - 2);\n" +
        "            Reservadas[i] = pal;\n" +
        "        }\n" +
        "        else\n" +
        "        {\n" +
        "            Reservadas[i] = aux[1].Substring(1, aux[1].Length - 1);\n" +
        "        }\n" +
        "    }\n" +
        "    for (int i = 0; i < Reservadas.Count; i++)\n" +
        "    {\n" +
        "        if(palabra = Reservadas[i])\n" +
        "        {\n" +
        "            salida = salida + palabra + \"=\" + numReservadas[i] + \"\\n\";\n" +
        "        }\n" +
        "    }\n" +
        "    while(contador < palabra.length)\n" +
        "    {\n" +
        "        switch(estado)\n" +
        "        {\n" +
        "            " + CasosEstados() + "\n" +
        "            \n" +
        "            default:\n" +
        "            {\n" +
        "                break;\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}");
    }
    private String CasosEstados()
    {
        String salida = "";
        int contador = 0;
        for(String s : Estados)
        {
            salida = salida + "case " + s + ":\n" +
                    "            {\n" +
                    "                switch(palabra[contador])\n" +
                    "                {\n" +
                    "                    " + CasosTokens(contador) + "\n" +
                    "                }\n" +
                    "                break;\n" +
                    "            }\n" +
                    "           default :\n" +
                    "               break;";
            contador++;
        }
        return salida;
    }
    private String CasosTokens(int cont)
    {
        String subcaso = "";
        int contador = 0;
        for(String trans : Transiciones.get(cont))
        {
            String[] aux = trans.split("|");
            if(aux[0].startsWith("\"") || aux[0].startsWith("\'"))
            {
                subcaso = subcaso + "case \"" + aux[0].split(",")[0] + "\":\n" + //se evaluan los casos en que el caracter de la palabra pertenezca al lenguaje
    "                    {\n" +
    "                        tacos = " + aux[0].split(",")[1] + ";\n" +
    "                        estado = " + aux[1].substring(1) + ";\n" +
    "                        break;\n" +
    "                    }\n" +
    "                    ";
            }
        }
        return subcaso;
    }
    /*private String Condicion(String t)
    {
        String condicion = "";
        String[] aux = t.split("|");
        if(condicion.equals(""))
        {
            condicion = "Token == " + aux[0].split(",")[1].split("|")[0];
        }
        else
        {
            condicion = " || Token == " + aux[0].split(",")[1];
        }
        return condicion;
    }*/

    private String ListaCadena(List<String> lista)
    {
        String salida = "";
        for(String s : lista)
        {
            salida = salida + ",";
        }
        salida = salida.substring(0, salida.length() - 1);
        return salida;
    }
}
