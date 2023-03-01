package br.jus.trt12.paulopinheiro.ldap2ad.view;

import br.jus.trt12.paulopinheiro.ldap2ad.control.compare.CompareGroupListService;
import br.jus.trt12.paulopinheiro.ldap2ad.control.compare.CompareUserService;
import br.jus.trt12.paulopinheiro.ldap2ad.control.compare.DefaultCompareGroupListService;
import br.jus.trt12.paulopinheiro.ldap2ad.control.compare.DefaultCompareUserService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.awt.Event;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author 2360
 */
public class ComparaUsuarioFrame extends javax.swing.JFrame {
    private SearchService adService;
    private SearchService ldapService;
    private CompareUserService compareService;

    public ComparaUsuarioFrame() {
        initComponents();
    }

    public ComparaUsuarioFrame(SearchService adService, SearchService ldapService) {
        this.adService = adService;
        this.ldapService = ldapService;

//        CompareGroupListService cglService = new DefaultCompareGroupListService(ldapService, adService);
//        for (String msgAlerta:cglService.getMensagensAlerta()) System.out.println(msgAlerta);
//        for (String msgErro:cglService.getMensagensErro()) System.out.println(msgErro);

        initComponents();
        limparCampos();
    }

    private void pesquisar(String usuarioPesquisa) {
        limparCampos();
        try {
            if ((usuarioPesquisa==null)||(usuarioPesquisa.trim().isEmpty())) throw new InvalidParameterException("Informe o nome/matrícula do usuário a ser pesquisado");
            
            compareService = new DefaultCompareUserService(ldapService,adService,usuarioPesquisa);
            if (compareService.getUsuario()==null) throw new InvalidParameterException("Usuário " + usuarioPesquisa + " não encontrado na pesquisa.");

            preencherCampos(compareService);
            if ((compareService.getAcoesAutomatizaveis()!=null)&&(compareService.getAcoesAutomatizaveis().size()>0)) jbtGerarScript.setEnabled(true);

        } catch (InvalidParameterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de pesquisa", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de pesquisa", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preencherCampos(CompareUserService compareService) {
        preencherDadosUsuario(compareService.getUsuario());
        preencherListaProvidencias(compareService.getMensagensAlerta());
        preencherListaComandos(new ArrayList(compareService.getAcoesAutomatizaveis().keySet()));
    }

    private void preencherDadosUsuario(Usuario usuario) {
        this.jlbNomeUsuario.setText("Nome: " + usuario.getCn());
        this.jlbGrupoPrimario.setText("Grupo primário: " + usuario.getGrupoPrimario());
        this.jlbDn.setText("DN: " + usuario.getDn());
    }

    private void preencherListaProvidencias(List<String> providencias) {
        DefaultListModel modeloProvidencias = new DefaultListModel();
        for (String p:providencias) {
            modeloProvidencias.addElement(p);
        }
        this.jlstProvidencias.setModel(modeloProvidencias);
    }

    private void preencherListaComandos(List<String> comandos) {
        DefaultListModel modeloComandos = new DefaultListModel();
        for (String c:comandos) {
            modeloComandos.addElement(c);
        }
        this.jlstComandos.setModel(modeloComandos);
    }

    private void gerarScript(CompareUserService compareService, File arquivo) {
        try {
            compareService.criarScript(arquivo);
            JOptionPane.showMessageDialog(this, "Arquivo gerado com sucesso", "Informação", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de criação de script", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limparCampos() {
        this.jtfPesquisaUsuario.setText("");
        this.jlbNomeUsuario.setText("Nome:");
        this.jlbGrupoPrimario.setText("Grupo primário:");
        this.jlbDn.setText("DN:");
        this.jlstProvidencias.setModel(new DefaultListModel());
        this.jlstComandos.setModel(new DefaultListModel());
        this.jbtGerarScript.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jfcCriarScript = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jtfPesquisaUsuario = new javax.swing.JTextField();
        jbtPesquisar = new javax.swing.JButton();
        jpnDadosUsuario = new javax.swing.JPanel();
        jlbNomeUsuario = new javax.swing.JLabel();
        jlbGrupoPrimario = new javax.swing.JLabel();
        jlbDn = new javax.swing.JLabel();
        jpnProvidencias = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlstProvidencias = new javax.swing.JList<>();
        jpnComandosScripts = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlstComandos = new javax.swing.JList<>();
        jbtGerarScript = new javax.swing.JButton();
        jbtLimpar = new javax.swing.JButton();

        jfcCriarScript.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        jfcCriarScript.setDialogTitle("Escolha o local e nome do arquivo");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Atualizar Usuário no Active Directory");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()+2f));
        jLabel4.setText("Nome/matrícula do usuário:");

        jtfPesquisaUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtfPesquisaUsuarioKeyPressed(evt);
            }
        });

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

        jpnDadosUsuario.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados do usuário:"));

        jlbNomeUsuario.setFont(jlbNomeUsuario.getFont().deriveFont(jlbNomeUsuario.getFont().getSize()+3f));
        jlbNomeUsuario.setText("Nome:");

        jlbGrupoPrimario.setFont(jlbGrupoPrimario.getFont().deriveFont(jlbGrupoPrimario.getFont().getSize()+3f));
        jlbGrupoPrimario.setText("Grupo Primário:");

        jlbDn.setFont(jlbDn.getFont().deriveFont(jlbDn.getFont().getSize()+3f));
        jlbDn.setText("DN:");

        javax.swing.GroupLayout jpnDadosUsuarioLayout = new javax.swing.GroupLayout(jpnDadosUsuario);
        jpnDadosUsuario.setLayout(jpnDadosUsuarioLayout);
        jpnDadosUsuarioLayout.setHorizontalGroup(
            jpnDadosUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDadosUsuarioLayout.createSequentialGroup()
                .addGroup(jpnDadosUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbNomeUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnDadosUsuarioLayout.createSequentialGroup()
                        .addComponent(jlbGrupoPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbDn, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnDadosUsuarioLayout.setVerticalGroup(
            jpnDadosUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnDadosUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbNomeUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnDadosUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbGrupoPrimario)
                    .addComponent(jlbDn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnProvidencias.setBorder(javax.swing.BorderFactory.createTitledBorder("Providências a serem tomadas manualmente"));

        jScrollPane1.setViewportView(jlstProvidencias);

        javax.swing.GroupLayout jpnProvidenciasLayout = new javax.swing.GroupLayout(jpnProvidencias);
        jpnProvidencias.setLayout(jpnProvidenciasLayout);
        jpnProvidenciasLayout.setHorizontalGroup(
            jpnProvidenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnProvidenciasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jpnProvidenciasLayout.setVerticalGroup(
            jpnProvidenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnProvidenciasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpnComandosScripts.setBorder(javax.swing.BorderFactory.createTitledBorder("Comandos a serem incluídos no script"));

        jScrollPane2.setViewportView(jlstComandos);

        javax.swing.GroupLayout jpnComandosScriptsLayout = new javax.swing.GroupLayout(jpnComandosScripts);
        jpnComandosScripts.setLayout(jpnComandosScriptsLayout);
        jpnComandosScriptsLayout.setHorizontalGroup(
            jpnComandosScriptsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnComandosScriptsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jpnComandosScriptsLayout.setVerticalGroup(
            jpnComandosScriptsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnComandosScriptsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
        );

        jbtGerarScript.setFont(jbtGerarScript.getFont().deriveFont(jbtGerarScript.getFont().getStyle() | java.awt.Font.BOLD, jbtGerarScript.getFont().getSize()+2));
        jbtGerarScript.setText("Gerar Script");
        jbtGerarScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGerarScriptActionPerformed(evt);
            }
        });

        jbtLimpar.setText("Limpar");
        jbtLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnDadosUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnProvidencias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnComandosScripts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtGerarScript)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtLimpar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jpnDadosUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jpnProvidencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpnComandosScripts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtGerarScript)
                    .addComponent(jbtLimpar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPesquisarActionPerformed
        pesquisar(this.jtfPesquisaUsuario.getText());
    }//GEN-LAST:event_jbtPesquisarActionPerformed

    private void jbtLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLimparActionPerformed
        this.limparCampos();
    }//GEN-LAST:event_jbtLimparActionPerformed

    private void jbtGerarScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGerarScriptActionPerformed
        if (jfcCriarScript.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            this.gerarScript(compareService, jfcCriarScript.getSelectedFile());
        }
    }//GEN-LAST:event_jbtGerarScriptActionPerformed

    private void jtfPesquisaUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPesquisaUsuarioKeyPressed
        if (evt.getKeyCode()==Event.ENTER) {
            pesquisar(this.jtfPesquisaUsuario.getText());
        }
    }//GEN-LAST:event_jtfPesquisaUsuarioKeyPressed

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
            java.util.logging.Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ComparaUsuarioFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ComparaUsuarioFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtGerarScript;
    private javax.swing.JButton jbtLimpar;
    private javax.swing.JButton jbtPesquisar;
    private javax.swing.JFileChooser jfcCriarScript;
    private javax.swing.JLabel jlbDn;
    private javax.swing.JLabel jlbGrupoPrimario;
    private javax.swing.JLabel jlbNomeUsuario;
    private javax.swing.JList<String> jlstComandos;
    private javax.swing.JList<String> jlstProvidencias;
    private javax.swing.JPanel jpnComandosScripts;
    private javax.swing.JPanel jpnDadosUsuario;
    private javax.swing.JPanel jpnProvidencias;
    private javax.swing.JTextField jtfPesquisaUsuario;
    // End of variables declaration//GEN-END:variables

}
