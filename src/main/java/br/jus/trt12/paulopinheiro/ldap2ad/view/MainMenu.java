/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.jus.trt12.paulopinheiro.ldap2ad.view;

import br.jus.trt12.paulopinheiro.ldap2ad.model.SearchService;
import javax.swing.JFrame;

/**
 *
 * @author 2360
 * Menu principal do sistema. Recebe objetos do tipo SearchService para LDAP e
 * para AD e repassa aos programas chamados no menu.
 * 
 */
public class MainMenu extends javax.swing.JFrame {
    private SearchService adService;
    private SearchService ldapService;
    /**
     * Creates new form MainMenu
     */
    public MainMenu() {
        initComponents();
    }

    public MainMenu(SearchService adService, SearchService ldapService) {
        this.adService = adService;
        this.ldapService = ldapService;

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

        jmbMenuPrincipal = new javax.swing.JMenuBar();
        jmiUsuarios = new javax.swing.JMenu();
        jmiComparaUsuario = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Apoio a manutenção do Gabinete Virtual TRT 12");

        jmiUsuarios.setText("Usuários");

        jmiComparaUsuario.setText("Sincronizar");
        jmiComparaUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiComparaUsuarioActionPerformed(evt);
            }
        });
        jmiUsuarios.add(jmiComparaUsuario);

        jmbMenuPrincipal.add(jmiUsuarios);

        setJMenuBar(jmbMenuPrincipal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 646, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Escolha de menu: Usuários - Sincronizar
    // Invoca o frame de comparação dos dados do usuário no LDAP com os do AD
    // E oferece sugestões de atualização, além de oferecer serviço de geração
    // de script em PowerShell para atualizar o AD.
    private void jmiComparaUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiComparaUsuarioActionPerformed
        JFrame frame = new ComparaUsuarioFrame(adService,ldapService);
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_jmiComparaUsuarioActionPerformed

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
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jmbMenuPrincipal;
    private javax.swing.JMenuItem jmiComparaUsuario;
    private javax.swing.JMenu jmiUsuarios;
    // End of variables declaration//GEN-END:variables
}
