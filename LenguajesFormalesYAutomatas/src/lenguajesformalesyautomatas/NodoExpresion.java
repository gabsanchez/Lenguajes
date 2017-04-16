/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajesformalesyautomatas;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
/**
 *
 * @author williams
 */
public class NodoExpresion 
{
    public String Elemento;
    public int Token;
    public List<String> First = new ArrayList();
    public List<String> Last = new ArrayList();
    public boolean bNulable;
    public List<String> LastIzq = new ArrayList();
    public List<String> FirstDer = new ArrayList();
}
