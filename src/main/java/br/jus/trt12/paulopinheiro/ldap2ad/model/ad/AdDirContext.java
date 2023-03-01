package br.jus.trt12.paulopinheiro.ldap2ad.model.ad;

import java.security.InvalidParameterException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import br.jus.trt12.paulopinheiro.ldap2ad.model.GeneralDirContext;

public class AdDirContext implements GeneralDirContext {
    private static final String INITIAL_CTX = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String SERVIDOR = "ldap://citrix-ad.trt12.jus.br:389";
    private static final String CONNECTION_TYPE = "simple";

    private final String server;
    private final String usuarioConsulta;
    private final String senhaConsulta;
    private DirContext dirContext;

    public AdDirContext(String usuario, String senha) {
        this.server = SERVIDOR;
        this.usuarioConsulta = usuario;
        this.senhaConsulta = senha;
    }

    @Override
    public DirContext getDirContext() {
        if (this.dirContext==null) conexaoAD();
        return this.dirContext;
    }

    private void conexaoAD() {
        Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Iniciando conexão com Active Directory...", "conexaoAD");
        dirContext = null;
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CTX);
        env.put(Context.PROVIDER_URL, this.server);
        env.put(Context.SECURITY_PRINCIPAL, this.usuarioConsulta);
        env.put(Context.SECURITY_CREDENTIALS, this.senhaConsulta);
        env.put(Context.SECURITY_AUTHENTICATION, CONNECTION_TYPE);
        
        env.put("java.naming.ldap.attributes.binary", "objectSID"); // obrigatorio para que o objectSID seja retornado como byte[] e não como String

        try {
            // Cria um Initial Context  
            dirContext = new InitialDirContext(env);
            Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Conexão com Active Directory aberta com sucesso", "conexaoAD");
        } catch (AuthenticationException ex) {
            close();
            throw new InvalidParameterException("Erro de autenticação. " + ex.toString());
        } catch (NamingException e) {
            close();
            throw new InvalidParameterException(e.toString());
        }
    }

    @Override
    public void close() {
        try {
            if (this.dirContext != null) {
                this.dirContext.close();
            }
            Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Conexão com Ad fechada com sucesso", "fecharConexao");
        } catch (NamingException ex) {
            throw new RuntimeException("Erro ao fechar conexão com AD: " + ex.toString());
        }
    }
}
