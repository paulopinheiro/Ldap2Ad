/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trt12.paulopinheiro.ldap2ad.view;

import br.jus.trt12.paulopinheiro.ldap2ad.control.search.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ad.AdSearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ldap.OpenLdapSearchService;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author 2360
 */
public class MainFrame extends javax.swing.JFrame {
    private AdSearchService adService;
    private OpenLdapSearchService ldapService;

    public MainFrame() {
        initComponents();
        loginAD();
    }

    private void loginAD() {
        jdlgLoginAD.setVisible(true);
        jdlgLoginAD.pack();
    }

    private void buscarServicos(String usuario, String senha) {
        try {
            this.adService = new AdSearchService(usuario,senha);
            this.ldapService = new OpenLdapSearchService();
        } catch (InvalidParameterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de conexão", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preencherCampos(String uid) {
        preencherPainel(uid, ldapService, jlbLdapUsuarioNome,jlbLdapGrupoPrimario,jlbLdapDN,jlstLdapGrupos,jlstLdapUsuariosGrupo, "LDAP");
        preencherPainel(uid, adService, jlbAdUsuarioNome,jlbAdGrupoPrimario,jlbAdDN,jlstAdGrupos,jlstAdUsuariosGrupo,"Active Directory");
    }

    private void preencherPainel(String uid, SearchService service, JLabel labelNome, JLabel labelGrupo, JLabel labelDn, JList listaGrupos, JList listaUsuarios, String diretorio) {
        try {
            Usuario u = service.getUsuarioByUid(uid);
            if (u==null) throw new InvalidParameterException("Usuário " + uid + " não encontrado no " + diretorio);
            labelNome.setText("Nome: " + u.getCn());
            labelGrupo.setText("Grupo Primário: " + u.getGrupoPrimario());
            labelDn.setText("DN: " + u.getDn());
            preencherListaGrupos(listaGrupos,u.getTodosGrupos());
            preencherListaUsuariosGrupo(listaUsuarios,service,u.getGrupoPrimario());
        } catch (InvalidParameterException ex) {
            limparCampos(labelNome,labelGrupo,labelDn,listaGrupos,listaUsuarios);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void preencherListaGrupos(JList lista, List<Grupo> grupos) {
        DefaultListModel model = new DefaultListModel();
        grupos.forEach((g) -> model.addElement(g.getSigla()));
        lista.setModel(model);
    }

    private void preencherListaUsuariosGrupo(JList lista, SearchService service, Grupo grupo) {
        List<Usuario> usuariosGrupo = service.getMembrosGrupo(grupo);
        if (usuariosGrupo!=null) {
            Collections.sort(usuariosGrupo);
            DefaultListModel model = new DefaultListModel();
            usuariosGrupo.forEach((u) -> model.addElement(u.getCn()));
            lista.setModel(model);
        }
    }

    private void limparCampos(JLabel labelNome, JLabel labelGrupo, JLabel labelDn, JList listaGrupos, JList listaUsuarios) {
        labelNome.setText("Nome:");
        labelGrupo.setText("Grupo primário:");
        labelDn.setText("DN:");
        listaGrupos.setModel(new DefaultListModel());
        listaUsuarios.setModel(new DefaultListModel());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jdlgLoginAD = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfLoginUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jpfLoginSenha = new javax.swing.JPasswordField();
        jbtLoginConectar = new javax.swing.JButton();
        jbtLoginCancelar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jtfPesquisaUsuario = new javax.swing.JTextField();
        jbtPesquisar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlstLdapGrupos = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlstLdapUsuariosGrupo = new javax.swing.JList<>();
        jlbLdapGrupoPrimario = new javax.swing.JLabel();
        jlbLdapDN = new javax.swing.JLabel();
        jlbLdapUsuarioNome = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlbAdGrupoPrimario = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlstAdGrupos = new javax.swing.JList<>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jlstAdUsuariosGrupo = new javax.swing.JList<>();
        jlbAdDN = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jlbAdUsuarioNome = new javax.swing.JLabel();

        jdlgLoginAD.setTitle("Login Active Directory");
        jdlgLoginAD.setModal(true);
        jdlgLoginAD.setResizable(false);
        jdlgLoginAD.setSize(new java.awt.Dimension(400, 160));

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+4));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Fazer Login no Active Directory");

        jLabel2.setText("Usuario:");

        jtfLoginUsuario.setText("trt12\\");

            jLabel3.setText("Senha:");

            jbtLoginConectar.setText("Conectar");
            jbtLoginConectar.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtLoginConectarActionPerformed(evt);
                }
            });

            jbtLoginCancelar.setText("Cancelar");

            javax.swing.GroupLayout jdlgLoginADLayout = new javax.swing.GroupLayout(jdlgLoginAD.getContentPane());
            jdlgLoginAD.getContentPane().setLayout(jdlgLoginADLayout);
            jdlgLoginADLayout.setHorizontalGroup(
                jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jdlgLoginADLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addGroup(jdlgLoginADLayout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jtfLoginUsuario))
                        .addGroup(jdlgLoginADLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jdlgLoginADLayout.createSequentialGroup()
                                    .addComponent(jbtLoginConectar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtLoginCancelar)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(jpfLoginSenha))))
                    .addContainerGap())
            );
            jdlgLoginADLayout.setVerticalGroup(
                jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jdlgLoginADLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addGap(18, 18, 18)
                    .addGroup(jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jtfLoginUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jpfLoginSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jdlgLoginADLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtLoginConectar)
                        .addComponent(jbtLoginCancelar))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            jdlgLoginAD.getAccessibleContext().setAccessibleParent(this);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("Migração LDAP x Active Directory");

            jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

            jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()+2f));
            jLabel4.setText("Nome do usuário para pesquisa:");

            jbtPesquisar.setFont(jbtPesquisar.getFont().deriveFont(jbtPesquisar.getFont().getStyle() | java.awt.Font.BOLD));
            jbtPesquisar.setText("Pesquisar");
            jbtPesquisar.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtPesquisarActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jtfPesquisaUsuario)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jbtPesquisar)
                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jtfPesquisaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtPesquisar))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados no LDAP"));

            jLabel6.setText("Grupos:");

            jScrollPane1.setViewportView(jlstLdapGrupos);

            jLabel8.setText("Usuários do grupo principal:");

            jScrollPane2.setViewportView(jlstLdapUsuariosGrupo);

            jlbLdapGrupoPrimario.setText("Grupo Primário:");

            jlbLdapDN.setText("DN:");

            jlbLdapUsuarioNome.setFont(jlbLdapUsuarioNome.getFont().deriveFont(jlbLdapUsuarioNome.getFont().getStyle() | java.awt.Font.BOLD));
            jlbLdapUsuarioNome.setText("Nome:");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbLdapUsuarioNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(304, 304, 304))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbLdapGrupoPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jlbLdapDN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jlbLdapUsuarioNome)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbLdapGrupoPrimario)
                        .addComponent(jlbLdapDN))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                        .addComponent(jScrollPane2))
                    .addContainerGap())
            );

            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados no Active Directory"));

            jlbAdGrupoPrimario.setText("Grupo Primário:");

            jScrollPane3.setViewportView(jlstAdGrupos);

            jLabel11.setText("Grupos:");

            jScrollPane4.setViewportView(jlstAdUsuariosGrupo);

            jlbAdDN.setText("DN:");

            jLabel13.setText("Usuários do grupo principal:");

            jlbAdUsuarioNome.setFont(jlbAdUsuarioNome.getFont().deriveFont(jlbAdUsuarioNome.getFont().getStyle() | java.awt.Font.BOLD));
            jlbAdUsuarioNome.setText("Nome:");

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel13)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(jScrollPane4)))
                        .addComponent(jlbAdUsuarioNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jlbAdGrupoPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jlbAdDN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jlbAdUsuarioNome)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlbAdGrupoPrimario)
                        .addComponent(jlbAdDN))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel13))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                        .addComponent(jScrollPane4))
                    .addContainerGap())
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jbtLoginConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLoginConectarActionPerformed
        this.buscarServicos(this.jtfLoginUsuario.getText(), String.valueOf(this.jpfLoginSenha.getPassword()));
        jdlgLoginAD.dispose();
    }//GEN-LAST:event_jbtLoginConectarActionPerformed

    private void jbtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPesquisarActionPerformed
        preencherCampos(this.jtfPesquisaUsuario.getText());
    }//GEN-LAST:event_jbtPesquisarActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton jbtLoginCancelar;
    private javax.swing.JButton jbtLoginConectar;
    private javax.swing.JButton jbtPesquisar;
    private javax.swing.JDialog jdlgLoginAD;
    private javax.swing.JLabel jlbAdDN;
    private javax.swing.JLabel jlbAdGrupoPrimario;
    private javax.swing.JLabel jlbAdUsuarioNome;
    private javax.swing.JLabel jlbLdapDN;
    private javax.swing.JLabel jlbLdapGrupoPrimario;
    private javax.swing.JLabel jlbLdapUsuarioNome;
    private javax.swing.JList<String> jlstAdGrupos;
    private javax.swing.JList<String> jlstAdUsuariosGrupo;
    private javax.swing.JList<String> jlstLdapGrupos;
    private javax.swing.JList<String> jlstLdapUsuariosGrupo;
    private javax.swing.JPasswordField jpfLoginSenha;
    private javax.swing.JTextField jtfLoginUsuario;
    private javax.swing.JTextField jtfPesquisaUsuario;
    // End of variables declaration//GEN-END:variables

}