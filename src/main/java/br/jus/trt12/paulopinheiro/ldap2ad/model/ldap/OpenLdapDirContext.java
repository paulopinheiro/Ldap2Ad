package br.jus.trt12.paulopinheiro.ldap2ad.model.ldap;

import br.jus.trt12.paulopinheiro.ldap2ad.model.ad.AdDirContext;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import br.jus.trt12.paulopinheiro.ldap2ad.model.GeneralDirContext;

@SuppressWarnings("rawtypes")
public class OpenLdapDirContext implements GeneralDirContext {  
    private static final String INITIAL_CTX = "com.sun.jndi.ldap.LdapCtxFactory";  
    private static final String SERVIDOR = "ldap://ldap.trt12.jus.br:389"; //Defina aqui o ip onde está o ldap. o que vem após os ":" é a porta do ldap 
    private static final String CONNECTION_TYPE = "simple";  
    private static final String ADMIN_DN = "uid=consulta,dc=trt12,dc=gov,dc=br";
    private static final String ADMIN_PW = "consulta";
    //private final static String BASE_DN = "dc=trt12,dc=gov,dc=br";
    private static final String MSG_ERROR_LDAP_CONNECTION = "Não foi possível obter um contexto LDAP";
    private static final String MSG_ERROR_LDAP_VALIDATION_USER = "Username ou Password Inválida";

    private static DirContext ctx;

    @Override
    public DirContext getDirContext() {
        if (ctx==null) ctx = provideDirContext();
        return ctx;
    }
    /** 
     * Método responsável por realizar a conexão padrão com o Ldap. 
     *  
     * @author Adriano Anderson 
     */ 

    @SuppressWarnings("unchecked")
    private static DirContext provideDirContext() {  
        DirContext dirCtx = null;  
        Hashtable env = new Hashtable();  

        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CTX);  
        env.put(Context.PROVIDER_URL, SERVIDOR);  
        env.put(Context.SECURITY_PRINCIPAL, ADMIN_DN);  
        env.put(Context.SECURITY_CREDENTIALS, ADMIN_PW);  
        env.put(Context.SECURITY_AUTHENTICATION, CONNECTION_TYPE);

        Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Iniciando conexão com OpenLDAP...", "provideDirContext");
        try {  
            // Cria um Initial Context  
            dirCtx = new InitialDirContext(env);  
            Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Conexão com OpenLDAP aberta com sucesso", "provideDirContext");
        } catch (javax.naming.AuthenticationException e) {
            Logger.getLogger(AdDirContext.class.getName()).log(Level.SEVERE, MSG_ERROR_LDAP_VALIDATION_USER, "provideDirContext");
            Logger.getLogger(AdDirContext.class.getName()).log(Level.SEVERE, e.getMessage(), "provideDirContext");
        } catch (NamingException e) {
            Logger.getLogger(AdDirContext.class.getName()).log(Level.SEVERE, MSG_ERROR_LDAP_CONNECTION, "provideDirContext");
            Logger.getLogger(AdDirContext.class.getName()).log(Level.SEVERE, e.getMessage(), "provideDirContext");
        }
        return dirCtx;
    }

    @Override
    public void close() {
        try {
            if (getDirContext() != null) {
                getDirContext().close();
            }
            Logger.getLogger(AdDirContext.class.getName()).log(Level.INFO, "Conexão com OpenLDAP fechada com sucesso", "fecharConexao");
        } catch (NamingException ex) {
            System.out.println("Erro ao fechar conexão com OpenLdap: " + ex.getExplanation());
        } finally {
            ctx = null;
        }
    }
}  