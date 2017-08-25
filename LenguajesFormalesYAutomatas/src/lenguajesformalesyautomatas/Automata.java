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
    List<String> Aceptaciones = new ArrayList();
    List<String> Reservadas = new ArrayList();
    List<String> ReservadasPlus = new ArrayList();
    List<String> numReservadas = new ArrayList();
    List<String> Conjuntos = new ArrayList();
    List<String> Elementos = new ArrayList();
    List<String> MetodosLlamar = new ArrayList();
    FileWriter fwCS;
    FileWriter fwCPP;
    String codigo;
    String Error;
    public Automata(List<String> estados, List<String> reservadas, List<String> conjuntos, List<String> elementos, String nombre, String error) throws IOException
    {
        for(String s : estados)
        {
            String[] aux = s.split("~");
            if(aux.length > 4)
            {
                String[] _aux = aux;
                aux = new String[4];
                aux[0] = _aux[0];
                aux[1] = "";
                for (int i = 1; i < _aux.length - 3; i++)
                {
                    aux[1] = aux[1] + _aux[i] + "~";
                }
                aux[1] = aux[1] + _aux[_aux.length - 3];
                aux[2] = _aux[_aux.length - 2];
                aux[3] = _aux[_aux.length - 1];
            }
            Estados.add(aux[0].substring(1));//Los estados están escritos como "S0, S1..." entonces obtenemos solamente el número sin la "S"
            List<String> temporal = new ArrayList();
            for(String e : estados)
            {
                String[] aux1 = e.split("~");
                if(aux1.length > 4)
                {
                    String[] _aux1 = aux1;
                    aux1 = new String[4];
                    aux1[0] = _aux1[0];
                    aux1[1] = "";
                    for (int i = 1; i < _aux1.length - 3; i++)
                    {
                        aux1[1] = aux1[1] + _aux1[i] + "~";
                    }
                    aux1[1] = aux1[1] + _aux1[_aux1.length - 3];
                    aux1[2] = _aux1[_aux1.length - 2];
                    aux1[3] = _aux1[_aux1.length - 1];
                }
                if(aux1[0].equals(aux[0]))
                {
                    temporal.add(aux1[1] + "~" + aux1[2]);
                }
            }
            Transiciones.add(temporal);
            Aceptaciones.add(aux[3]);
        }
        for(String c : conjuntos)
        {
            Conjuntos.add(c);
        }
        for(String e : elementos)
        {
            Elementos.add(e);
        }
        for(String r : reservadas)
        {
            String rsharp = r.replace("\"","-");
            String rplus = r.replace("\"","");
            String[] partes = rplus.split("="); 
            numReservadas.add(partes[0]);
            ReservadasPlus.add(partes[1]);
            Reservadas.add(r);
        }
        
        Error = error;
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
        "        string[] palabras = linea.Split(vacios);\n" +
        "        foreach (string s in palabras)\n" +
        "        {\n" +
        "            string pertenencia = s + \" = \" + Tomatoken(s);\n" +
        "            Console.WriteLine(pertenencia);\n" +
        "        }\n" +
        "    }\n" +
        "}");
        fwCS.write(CodigoConjuntos());
        fwCS.write("private int Tomatoken(string palabra)\n" +
        "{\n" +
        "    int estado = 0;\n" +
        "    int contador = 0;\n" +
        "    int tacos = 0; //token\n" +
        "    bool aceptacion = false;\n" +
        "    bool reserved = false;\n" +
        "    string[] Reservadas = " + ListaCadena(Reservadas) + ";\n" +
        "    int[] numReservadas = new int[Reservadas.Length];\n" +
        "    //string salida = \"\";\n" +
        "    " + LlamarMetodos() + "\n" +
        "    //Verificar si la palabara es reservada\n" +
        "    for (int i = 0; i < Reservadas.Length; i++)\n" +
        "    {\n" +
        "        Reservadas[i] = Reservadas[i].Replace(\'-\',\'\"\');\n" +
        "        string[] aux = Reservadas[i].Split('=');\n" +
        "        numReservadas[i] = Convert.ToInt32(aux[0]);\n" +
        "        if(aux.Length > 2)\n" +
        "        {\n" +
        "            string pal = \"\";\n" +
        "            for(int j = 1; j < aux.Length; j++)\n" +
        "            {\n" +
        "                pal = pal + aux[j] + \"=\";\n" +
        "            }\n" +
        "            //pal = pal.Substring(1, pal.Length - 2);\n" +
        "            Reservadas[i] = pal;\n" +
        "        }\n" +
        "        else\n" +
        "        {\n" +
        "            Reservadas[i] = aux[1].Substring(1, aux[1].Length - 2);\n" +
        "        }\n" +
        "    }\n" +
        "    for (int i = 0; i < Reservadas.Length; i++)\n" +
        "    {\n" +
        "        if(palabra == Reservadas[i])\n" +
        "        {\n" +
        "            tacos = numReservadas[i];\n" +
        "            aceptacion = true;\n" +
        "            reserved = true;\n" +
        "            break;\n" +
        "        }\n" +
        "    }\n" +
        "    if(!reserved)\n" +
        "    {\n" +
        "       palabra += \"#\";\n" +
        "       while(contador < palabra.Length)\n" +
        "       {\n" +
        "           switch(estado)\n" +
        "           {\n" +
        "               " + CasosEstados() + "\n" +
        "            \n" +
        "               default:\n" +
        "               {\n" +
        "                   break;\n" +
        "               }\n" +
        "           }\n" +
        "           contador++;\n" +
        "       }\n" +
        "    }\n" +
        "  if(!aceptacion)\n" +
        "  {\n" +
        "     return " + Error + ";\n" +
        "  }\n" +
        "  return tacos;\n" +
        "}");
        fwCS.close();
    }
    private String CasosEstados()
    {
        String salida = "";
        int contador = 0;
        for(String s : Estados)
        {
            if(contador == 0 || !Estados.get(contador).equals(Estados.get(contador-1)))
            {
                salida = salida + "case " + s + ":\n" +
                        "            {\n" +
                        "                switch(palabra[contador])\n" +
                        "                {\n" +
                        "                    " + CasosTokens(contador) + "\n" +
                        "                }\n" +
                        "                aceptacion = " + Aceptaciones.get(contador) + ";\n" +
                        "                break;\n" +
                        "            }\n";
            }
            contador++;
        }
        return salida;
    }
    private String CasosEstadosCPP()
    {
        String salida = "";
        int contador = 0;
        for(String s : Estados)
        {
            if(contador == 0 || !Estados.get(contador).equals(Estados.get(contador-1)))
            {
                salida = salida + "case " + s + ":\n" +
                        "            {\n" +
                        "                switch(palabra[contador])\n" +
                        "                {\n" +
                        "                    " + CasosTokensCPP(contador) + "\n" +
                        "                }\n" +
                        "                aceptacion = " + Aceptaciones.get(contador) + ";\n" +
                        "                break;\n" +
                        "            }\n";
            }
            contador++;
        }
        return salida;
    }
    private String CasosTokens(int cont)
    {
        String subcaso = "";
        String difolt = "";
        for(String trans : Transiciones.get(cont))
        {
            String[] aux = trans.split("~");
            if(aux.length > 2)
            {
                String[] _aux = aux;
                aux = new String[2];
                aux[0] = "";
                for (int i = 0; i < _aux.length - 2; i++)
                {
                    aux[0] = aux[0] + _aux[i] + "~";
                }
                aux[0] = aux[0] + _aux[_aux.length - 2];
                aux[1] = _aux[_aux.length - 1];
            }
            if(aux[0].startsWith("\"") || aux[0].startsWith("\'"))
            {
                String car = aux[0].split("_")[0];
                String comilla = car.substring(1, 2);
                if(comilla.equals("\'"))
                {
                    car = "\'" + "\\" + "\'" + "\'";
                }
                subcaso = subcaso + "case " + car + ":\n" + //se evaluan los casos en que el caracter de la palabra pertenezca al lenguaje
    "                    {\n" +
    "                        tacos = " + aux[0].split("_")[1] + ";\n" +
    "                        estado = " + aux[1].substring(1) + ";\n" +
    "                        break;\n" +
    "                    }\n" +
    "                    ";
            }
            else
            {
                if(!aux[0].split("_")[0].equals("#"))
                {
                    if(difolt.equals(""))
                    {
                        difolt = difolt + "if(" + aux[0].split("_")[0] + ".Contains((int)palabra[contador]))\n" +
    "                         {\n" +
    "                             tacos = " + aux[0].split("_")[1] + ";\n" +
    "                             estado = " + aux[1].substring(1) + ";\n" +
    "                         }\n";
                    }
                    else
                    {
                        difolt = difolt +
    "                         else if(" + aux[0].split("_")[0] + ".Contains((int)palabra[contador]))\n" +
    "                         {\n" +
    "                             tacos = " + aux[0].split("_")[1] + ";\n" +
    "                             estado = " + aux[1].substring(1) + ";\n" +
    "                         }\n";
                    }
                }
            }
        }
        difolt = "default:\n" +
"                     {\n" +
"                         " + difolt + "\n" +
"                         break;\n" +
"                     }\n";
        return subcaso + difolt;
    }
    private String CasosTokensCPP(int cont)
    {
        String subcaso = "";
        String difolt = "";
        for(String trans : Transiciones.get(cont))
        {
            String[] aux = trans.split("~");
            if(aux.length > 2)
            {
                String[] _aux = aux;
                aux = new String[2];
                aux[0] = "";
                for (int i = 0; i < _aux.length - 2; i++)
                {
                    aux[0] = aux[0] + _aux[i] + "~";
                }
                aux[0] = aux[0] + _aux[_aux.length - 2];
                aux[1] = _aux[_aux.length - 1];
            }
            if(aux[0].startsWith("\"") || aux[0].startsWith("\'"))
            {
                String car = aux[0].split("_")[0];
                String comilla = car.substring(1, 2);
                if(comilla.equals("\'"))
                {
                    car = "\'" + "\\" + "\'" + "\'";
                }
                subcaso = subcaso + "case " + car + ":\n" + //se evaluan los casos en que el caracter de la palabra pertenezca al lenguaje
    "                    {\n" +
    "                        tacos = " + aux[0].split("_")[1] + ";\n" +
    "                        estado = " + aux[1].substring(1) + ";\n" +
    "                        break;\n" +
    "                    }\n" +
    "                    ";
            }
            else
            {
                if(!aux[0].split("_")[0].equals("#"))
                {
                    if(difolt.equals(""))
                    {
                        difolt = difolt + "if(find(" + aux[0].split("_")[0] + ".begin(), " + aux[0].split(",")[0] + ".end(), (int)palabra[contador]) != " + aux[0].split("_")[0] + ".end())\n" +
    "                         {\n" +
    "                             tacos = " + aux[0].split("_")[1] + ";\n" +
    "                             estado = " + aux[1].substring(1) + ";\n" +
    "                         }\n";
                    }
                    else
                    {
                        difolt = difolt +
    "                         else if(find(" + aux[0].split("_")[0] + ".begin(), " + aux[0].split("_")[0] + ".end(), (int)palabra[contador]) != " + aux[0].split("_")[0] + ".end())\n" +
    "                         {\n" +
    "                             tacos = " + aux[0].split("_")[1] + ";\n" +
    "                             estado = " + aux[1].substring(1) + ";\n" +
    "                         }\n";
                    }
                }
            }
        }
        difolt = "default:\n" +
"                     {\n" +
"                         " + difolt + "\n" +
"                         break;\n" +
"                     }\n";
        return subcaso + difolt;
    }
    private String ListaCadena(List<String> lista)//se convierte una lista a un string
    {
        String salida = "";
        if(lista.isEmpty())
        {
            return "null";
        }
        else
        {
            if(lista.size() == 1)
            {
                salida = "\"" + lista.get(0) + "\"";
            }
            else
            {
                for(String s : lista)
                {
                    s = "\"" + s + "\"";
                    if(salida.equals(""))
                    {
                        salida = s;
                    }
                    else
                    {
                        salida = salida + ", " + s;
                    }
                }
            }
            salida = "{" + salida + "}";
            return salida;
        }
    }
    public String CodigoConjuntos()
    { 
        String Code="";
        for (int i = 0; i < Conjuntos.size(); i++) {
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
            String met = "Llenar"+Conjuntos.get(i)+"()";
            MetodosLlamar.add(met + ";");
            Code+="List<int> "+Conjuntos.get(i)+" = new List<int>();\n";
            Code+="public void " + met+"\n";
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
                        Code+="\t"+Conjuntos.get(i)+".Add("+k+");\n";
                    }
                }
                else
                {
                    int Asccii=0;
                    if (Contenido[j].startsWith("'")||Contenido[j].startsWith("\"")) 
                    {
                        Asccii = (int)Contenido[j].charAt(1);
                        Code+="\t"+Conjuntos.get(i)+".Add("+Asccii+");\n";
                    }
                    else if (Contenido[j].startsWith("c")) 
                    {
                        String num="";
                        num = Contenido[j].substring(Contenido[j].indexOf("(")+1, Contenido[j].indexOf(")"));
                        Asccii = Integer.parseInt(num);
                    }
                    
                    
                    
                    Code+="\t"+Conjuntos.get(i)+".Add("+Asccii+");\n";
                }
            }
            Code+="}\n";
        }
        return Code;
    }
    public String CodigoConjuntosCPP()
    { 
        String Code="";
        for (int i = 0; i < Conjuntos.size(); i++) {
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
            String met = "Llenar"+Conjuntos.get(i)+"()";
            MetodosLlamar.add(met + ";");
            Code+="list<int> "+Conjuntos.get(i)+";\n";
            Code+="void " + met+"\n";
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
                        Code+="\t"+Conjuntos.get(i)+".push_back("+k+");\n";
                    }
                }
                else
                {
                    int Asccii=0;
                    if (Contenido[j].startsWith("'")||Contenido[j].startsWith("\"")) 
                    {
                        Asccii = (int)Contenido[j].charAt(1);
                        Code+="\t"+Conjuntos.get(i)+".push_back("+Asccii+");\n";
                    }
                    else if (Contenido[j].startsWith("c")) 
                    {
                        String num="";
                        num = Contenido[j].substring(Contenido[j].indexOf("(")+1, Contenido[j].indexOf(")"));
                        Asccii = Integer.parseInt(num);
                    }
                    
                    
                    
                    Code+="\t"+Conjuntos.get(i)+".push_back("+Asccii+");\n";
                }
            }
            Code+="}\n";
        }
        return Code;
    }
    private String LlamarMetodos()
    {
        String salida = "";
        for(String m : MetodosLlamar)
        {
            salida = salida + m + "\n";
        }
        return salida;
    }
    private String DeclararMetodos()
    {
        String salida = "";
        for(String m : MetodosLlamar)
        {
            salida = salida +"void "+ m + "\n";
        }
        return salida;
    }
    public void EsbribirCodigoCPP() throws IOException
    {
        String temp = CodigoConjuntosCPP();
        fwCPP.write(DeclararMetodos());
        fwCPP.write(temp);
        fwCPP.write("int Tomatoken(string palabra)\n" +
        "{\n" +
        "    int estado = 0;\n" +
        "    int contador = 0;\n" +
        "    int tacos = 0; //token\n" +
        "    bool aceptacion = false;\n" +
        "    bool reserved = false;\n" +
        "    string Reservadas [" + Reservadas.size() + "] = " + ListaCadena(ReservadasPlus) + ";\n" +
        "    int numReservadas [" + Reservadas.size() + "];\n" +
        "    " + LlamarMetodos() + "\n" +
        "    //Verificar si la palabara es reservada\n" +
        "    for (int i = 0; i < " + Reservadas.size() + "; i++)\n" +
        "    {\n" +
        "        if(palabra == Reservadas[i])\n" +
        "        {\n" +
        "            tacos = numReservadas[i];\n" +
        "            aceptacion = true;\n" +
        "            reserved = true;\n" +
        "            break;\n" +
        "        }\n" +
        "    }\n" +
        "    if(!reserved)\n" +
        "    {\n" +
        "       palabra += \"#\";\n" +
        "       while(contador < palabra.length())\n" +
        "       {\n" +
        "           switch(estado)\n" +
        "           {\n" +
        "               " + CasosEstadosCPP() + "\n" +
        "            \n" +
        "               default:\n" +
        "               {\n" +
        "                   break;\n" +
        "               }\n" +
        "           }\n" +
        "           contador++;\n" +
        "       }\n" +
        "    }\n" +
        "  if(!aceptacion)\n" +
        "  {\n" +
        "     return " + Error + ";\n" +
        "  }\n" +
        "  else\n" +
        "  {\n" +
        "     return tacos;\n" +
        "  }\n"+
        "}");
        fwCPP.close();
    }
}
