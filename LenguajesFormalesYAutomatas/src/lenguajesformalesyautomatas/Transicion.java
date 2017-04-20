/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajesformalesyautomatas;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author williams
 */
public class Transicion {
    public String Elemento;
    public int Token;
    public String EstadoInicial;
    public boolean Aceptacion;
    public List<String> Transicion = new ArrayList();
    public List<String> TransicionFollow = new ArrayList();
    public String EstadoFinal;
}
