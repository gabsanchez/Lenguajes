/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajesformalesyautomatas;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author gabriel
 */
public class Principal extends javax.swing.JFrame {

    /**
     * Creates new form Principal
     */
    public Principal() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCargarArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_salida = new javax.swing.JTextArea();
        btn_clear = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        ta_FirstLast = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);

        btnCargarArchivo.setText("Cargar Archivo");
        btnCargarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarArchivoActionPerformed(evt);
            }
        });

        ta_salida.setEditable(false);
        ta_salida.setColumns(20);
        ta_salida.setRows(5);
        ta_salida.setFocusable(false);
        jScrollPane1.setViewportView(ta_salida);

        btn_clear.setText("Clear");
        btn_clear.setToolTipText("");
        btn_clear.setEnabled(false);
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        ta_FirstLast.setColumns(20);
        ta_FirstLast.setRows(5);
        jScrollPane2.setViewportView(ta_FirstLast);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(193, 193, 193)
                        .addComponent(btnCargarArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnCargarArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_clear)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnCargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarArchivoActionPerformed
        // TODO add your handling code here:
        ta_salida.setText("");
        ta_FirstLast.setText("");
        Archivo entrada = new Archivo();
        try 
        {
            entrada.Cargar();
            if(entrada.error.equals("File read successfully."))
            {
                ta_salida.append("CONJUNTOS");
                ta_salida.append("\n");
                ta_salida.append("\n");
                for (int i = 0; i < entrada.ConjuntosDeclarados.size(); i++) 
                {
                    ta_salida.append(entrada.ConjuntosDeclarados.get(i) + "{" + entrada.Elementos.get(i));
                    ta_salida.append("\n");
                }
                ta_salida.append("\n");
                ta_salida.append("TOKENS");
                ta_salida.append("\n");
                ta_salida.append("\n");
                for (int i = 0; i < entrada.Tokens.size(); i++) 
                {
                    ta_salida.append(entrada.ListaNumeros.get(i) + ": " + entrada.Tokens.get(i));
                    ta_salida.append("\n");
                }
                ta_salida.append("\n");
                ta_salida.append("ACCIONES");
                ta_salida.append("\n");
                ta_salida.append("\n");
                for (int i = 0; i < entrada.ContenidoAcciones.size(); i++) 
                {
                    ta_salida.append(entrada.ContenidoAcciones.get(i)+"");
                    ta_salida.append("\n");
                }
                ta_salida.append("\n");
                ta_salida.append("ERROR");
                ta_salida.append("\n");
                ta_salida.append("\n");
                ta_salida.append(entrada.ListaNumeros.get(entrada.ListaNumeros.size()-1).toString());
                btn_clear.setEnabled(true);
                

                for (int i = 0; i < entrada.FirstLast.size(); i++) 
                {
                    ta_FirstLast.append(entrada.FirstLast.get(i).Elemento);
                    ta_FirstLast.append("\n");
                    ta_FirstLast.append("   First:" +entrada.FirstLast.get(i).First);
                    ta_FirstLast.append("\n");
                    ta_FirstLast.append("   Last:"+entrada.FirstLast.get(i).Last);
                    ta_FirstLast.append("\n");
                    ta_FirstLast.append("   Nulable:"+entrada.FirstLast.get(i).bNulable);
                    ta_FirstLast.append("\n");
                }
                entrada.CalcularFollow();
                entrada.OrdenarLista(entrada.TablaFollow);
                ta_FirstLast.append("\n");
                ta_FirstLast.append("FOLLOW");
                ta_FirstLast.append("\n");
                ta_FirstLast.append("\n");
                for(String follow : entrada.TablaFollow)
                {
                    
                    ta_FirstLast.append(follow);
                    ta_FirstLast.append("\n");
                }
                
                entrada.TablaTransiciones();
                ta_FirstLast.append("\n");
                ta_FirstLast.append("ESTADOS");
                ta_FirstLast.append("\n");
                ta_FirstLast.append("\n");
                for (int i = 0; i < entrada.LTransicionM.size(); i++) 
                {
                    if (!entrada.LTransicionM.get(i).Elemento.equals("#")) {
                        ta_FirstLast.append(entrada.LTransicionM.get(i).EstadoInicial+"|"+entrada.LTransicionM.get(i).Elemento+","+entrada.LTransicionM.get(i).Token+"|"+entrada.LTransicionM.get(i).EstadoFinal+"|"+entrada.LTransicionM.get(i).Aceptacion);
                        ta_FirstLast.append("\n");
                    }
                    
                }
                entrada.AccionesNecesarias();
                //entrada.CodigoConjuntos();
                entrada.GenerarCodigoC();
            }
            JOptionPane.showMessageDialog(rootPane, entrada.error);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(rootPane, entrada.error);
        }
        //hola sanchez
    }//GEN-LAST:event_btnCargarArchivoActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        // TODO add your handling code here:
        ta_salida.setText("");
        ta_FirstLast.setText("");
        btn_clear.setEnabled(false);
    }//GEN-LAST:event_btn_clearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargarArchivo;
    private javax.swing.JButton btn_clear;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea ta_FirstLast;
    private javax.swing.JTextArea ta_salida;
    // End of variables declaration//GEN-END:variables
}
