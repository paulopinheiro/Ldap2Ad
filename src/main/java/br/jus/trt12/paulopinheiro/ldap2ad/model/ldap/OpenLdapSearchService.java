package br.jus.trt12.paulopinheiro.ldap2ad.model.ldap;

import br.jus.trt12.paulopinheiro.ldap2ad.model.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import br.jus.trt12.paulopinheiro.ldap2ad.util.Util;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import br.jus.trt12.paulopinheiro.ldap2ad.model.GeneralDirContext;

public class OpenLdapSearchService implements SearchService {
    private static final String BASE_DN = "DC=trt12,DC=gov,DC=br";
    private final GeneralDirContext ctx;

    // Verificar se erros não estão morrendo no "finally" dos métodos
    // Quando tem comando "return" no "finally" ele engole exceptions

    public OpenLdapSearchService() {
        this.ctx = new OpenLdapDirContext();
    }

    @Override
    public Usuario getUsuarioByUid(String uid) {
        return getUsuarioByUid(uid,true);
    }

    private Usuario getUsuarioByUid(String uid, boolean closeCtx) {
        Usuario resposta = null;
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(uid="+uid+")";
        try {
            NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

            if (cursor.hasMoreElements()) {
                SearchResult result = (SearchResult) cursor.nextElement();
                Attributes att = result.getAttributes();
                resposta = processarUsuario(att,result.getNameInNamespace());
            }
        } catch (NamingException ex) {
            Logger.getLogger(OpenLdapSearchService.class.getName()).log(Level.WARNING, "Usuário " + uid + " não encontrado", ex);
        } finally {
            if (closeCtx) ctx.close();
            return resposta;
        }
    }

    private Usuario processarUsuario(Attributes att, String dn) throws NamingException {
        String uid = "";
        String mail = "";
        Grupo grupoPrimario = null;
        String dnAdaptada = "";

        if (att.get("uid")!=null) uid = (String) att.get("uid").get();
        if (att.get("mail")!=null) mail = (String) att.get("mail").get();
        if (att.get("gidNumber")!=null) grupoPrimario = getGrupoByGidNumber((String) att.get("gidNumber").get());
        //Adaptando as diferenças de DN entre o Active Directory e o OpenLDAP
        //Também é trocado o DC=gov por DC=jus
        if (att.get("cn")!=null) dnAdaptada = dn.replace("uid="+uid, "CN="+att.get("cn").get()).replace("dc=gov", "dc=jus");

        List<Grupo> outrosGrupos = getOutrosGrupos(uid,grupoPrimario);

        return (new Usuario(uid,dnAdaptada,mail,grupoPrimario,outrosGrupos));
    }

    private List<Grupo> getOutrosGrupos(String uid, Grupo grupoPrimario) {
        List<Grupo> resposta = new ArrayList<>();

        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(&(objectClass=posixGroup)(memberUid="+uid+"))";
        try {
            NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

            while (cursor.hasMoreElements()) {
                SearchResult result = (SearchResult) cursor.nextElement();
                Attributes att = result.getAttributes();
                if (att.get("gidNumber")!=null) {
                    Grupo g = getGrupoByGidNumber((String) att.get("gidNumber").get());
                    //Se não for o grupo primario adicionar à lista
                    if (!g.equals(grupoPrimario)) resposta.add(g);
                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(OpenLdapSearchService.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            return resposta;
        }
    }

    private Grupo getGrupoByGidNumber(String gidNumber) {
        Grupo resposta = null;
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(&(objectClass=posixGroup)(gidNumber="+gidNumber+"))";
        try {
            NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

            while (cursor.hasMoreElements()) {
                SearchResult result = (SearchResult) cursor.nextElement();
                Attributes att = result.getAttributes();
                resposta = processarGrupo(att,result.getNameInNamespace());
            }
        } catch (NamingException ex) {
            Logger.getLogger(OpenLdapSearchService.class.getName()).log(Level.WARNING, "Grupo " + gidNumber + " não encontrado", ex);
        } finally {
            return resposta;
        }
    }

    @Override
    public Grupo getGrupoBySigla(String sigla) {
        Grupo resposta = null;
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(&(objectClass=posixGroup)(cn="+sigla+"))";
        try {
            NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

            if (cursor.hasMoreElements()) {
                SearchResult result = (SearchResult) cursor.nextElement();
                Attributes att = result.getAttributes();

                resposta = processarGrupo(att,result.getNameInNamespace());
            }
        } catch (NamingException ex) {
            Logger.getLogger(OpenLdapSearchService.class.getName()).log(Level.WARNING, "Grupo " + sigla + " não encontrado", ex);
        } finally {
            ctx.close();
            return resposta;
        }
    }

    private Grupo processarGrupo(Attributes att, String dn) throws NamingException {
        String sigla = "";
        String descricao = "";
        if (att.get("cn")!=null) sigla = (String) att.get("cn").get();
        if (att.get("description")!=null) descricao = (String) att.get("description").get();

        return new Grupo(sigla,descricao,dn);
    }

    @Override
    public List<Usuario> getMembrosGrupo(Grupo g) {
        if (g==null||g.getSigla()==null||g.getSigla().isEmpty()) throw new InvalidParameterException("Informe um grupo válido");
        List<Usuario> resposta = new ArrayList<>();

        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(&(objectClass=posixGroup)(cn="+g.getSigla()+"))";
        try {
            NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

            if (cursor.hasMoreElements()) {
                SearchResult result = (SearchResult) cursor.nextElement();
                Attributes att = result.getAttributes();
                List<String> lista = Util.parseListaString(att.get("memberUid").getAll());
                lista.forEach(uid -> resposta.add(this.getUsuarioByUid(uid,false)));
            }
        } catch (NamingException ex) {
            Logger.getLogger(OpenLdapSearchService.class.getName()).log(Level.WARNING, "Grupo " + g.getSigla() + " não encontrado", ex);
        } finally {
            ctx.close();
            return resposta;
        }
    }

    @Override
    public List<Grupo> getAllGrupos() {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
