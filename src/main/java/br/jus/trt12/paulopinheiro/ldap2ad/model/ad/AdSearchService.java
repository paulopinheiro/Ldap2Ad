package br.jus.trt12.paulopinheiro.ldap2ad.model.ad;

import br.jus.trt12.paulopinheiro.ldap2ad.util.Util;
import br.jus.trt12.paulopinheiro.ldap2ad.control.search.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import br.jus.trt12.paulopinheiro.ldap2ad.util.ad.UtilAd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class AdSearchService implements SearchService {
    private static final String BASE_DN = "DC=trt12,DC=jus,DC=br";
    private final AdDirContext ctx;
    private HashMap<String,Grupo> mapaGrupos;
    private List<Grupo> listaGrupos;
    private HashMap<Grupo,List<Usuario>> membramentos;
    private List<Usuario> listaUsuarios;

    public AdSearchService(String usuario, String senha) {
        this.ctx = new AdDirContext(usuario,senha);
        inicializarListas();
        this.ctx.close();
    }

    private void inicializarListas() {
        try {
            buscarGrupos();
            buscarMembros();
            //Testes.testarCargaDados(this.getListaUsuarios());
        } catch (NamingException ex) {
            Logger.getLogger(AdSearchService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buscarGrupos() throws NamingException {
        Logger.getLogger(AdSearchService.class.getName()).log(Level.INFO, "Buscando grupos do Active Directory");

        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(&(objectCategory=Group)(!(isCriticalSystemObject=TRUE)))";

        NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

        while (cursor.hasMoreElements()) {
            SearchResult result = (SearchResult) cursor.nextElement();
            Attributes att = result.getAttributes();
            processarGrupo(att);
        }
        this.listaGrupos = new ArrayList<>(this.getMapaGrupos().values());
    }

    private void processarGrupo(Attributes att) throws NamingException {
        String sigla = "";
        String descricao = "";
        String dn="";
        if (att.get("name")!=null) sigla = (String) att.get("name").get();
        if (att.get("description")!=null) descricao = (String) att.get("description").get();
        if (att.get("distinguishedname")!=null) dn = (String) att.get("distinguishedname").get();

        // Grupos contidos em "Users" e "Builtin" são padrão do Active Directory e não devem entrar na lista
        // Infelizmente não é possível filtrar atributos do tipo DN no Active Directory
        if (!dn.contains("CN=Users,DC=trt12,DC=jus,DC=br") && !dn.contains("CN=Builtin,DC=trt12,DC=jus,DC=br")) {
            String objectSid = Util.convertSidToStringSid((byte[]) att.get("objectSid").get());
            Grupo g = new Grupo(sigla,descricao,dn);
            this.getMapaGrupos().put(objectSid, g);
        }
    }

    private void buscarMembros() throws NamingException {
        Logger.getLogger(AdSearchService.class.getName()).log(Level.INFO, "Buscando usuários do Active Directory");

        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        String filtro = "(objectCategory=user)";

        NamingEnumeration<? extends SearchResult> cursor = ctx.getDirContext().search(BASE_DN, filtro, sc);

        while (cursor.hasMoreElements()) {
            SearchResult result = (SearchResult) cursor.nextElement();
            Attributes att = result.getAttributes();
            if (!UtilAd.isCriticalSystemObject(att)) {
                processarUsuario(att);
            }
        }
    }

    private void processarUsuario(Attributes att) throws NamingException {
        // busca todos os usuários
        // para cada usuário adicionar membramento da seguinte forma:
        // adicionar usuario , grupo_primario
        // pesquisar nos grupos quem tem (substring de "member") a cn do usuário
        // para cada grupo achado adicionar usuario_membro , grupo
        String uid = "";
        String mail = "";
        String dn="";
        if (att.get("samaccountname")!=null) uid = (String) att.get("samaccountname").get();
        if (att.get("mail")!=null) mail = (String) att.get("mail").get();
        if (att.get("distinguishedname")!=null) dn = (String) att.get("distinguishedname").get();

        // campo primarygroupid só traz os 4 últimos algarismos do objectSid.
        // para compor o sid é adiconado o mesmo prefixo de autoridades do usuário
        String sidGrupoPrimario = Util.getPrefixoAutoridades((byte[]) att.get("objectSid").get()) + "-" + (String) att.get("primarygroupid").get();
        Grupo primaryGroup = (Grupo) this.getMapaGrupos().get(sidGrupoPrimario);

        List<Grupo> outrosGrupos = new ArrayList<>();
        if (att.get("memberof")!=null) {
            List<String> listaDn = Util.parseListaString(att.get("memberof").getAll());
            outrosGrupos.addAll(parseOutrosGrupos(listaDn));
        }

        Usuario usuario = new Usuario(uid,dn,mail,primaryGroup,outrosGrupos);

        //Carrega o usuário na lista
        this.getListaUsuarios().add(usuario);

        //Atualiza o hashset de membramentos para todos os grupos (incluindo o primário)
        usuario.getTodosGrupos().forEach(grupo -> this.adicionaMembramento(grupo, usuario));
        
    }

    private List<Grupo> parseOutrosGrupos(List<String> listaDn) {
        // o campo "memberof", do AD só traz uma lista de DNs dos grupos
        // é necessário varrer a lista de grupos pra ver se a string contém
        // as DNs deles
        List<Grupo> resposta = new ArrayList<>();

        for (String dn:listaDn) {
            for (Grupo g:getListaGrupos()) {
                if (g.getDn().equals(dn)) {
                    resposta.add(g);
                    break;
                }
            }
        }
        return resposta;
    }

    /**
     * Retorna um objeto Usuario para o uid informado
     * @param uid do usuário (identificação no sistema)
     * @return Um objeto usuário
     */
    @Override
    public Usuario getUsuarioByUid(String uid) {
        Usuario search = new Usuario(uid,null,null,null,null);
        int i = this.getListaUsuarios().indexOf(search);
        if (i != -1) return this.getListaUsuarios().get(i);
        else return null;
    }

    @Override
    public Grupo getGrupoBySigla(String sigla) {
        for (Grupo g:this.getListaGrupos()) {
            if (g.getSigla().equalsIgnoreCase(sigla)) {
                return g;
            }
        }
        return null;
    }

    private HashMap getMapaGrupos() {
        if (this.mapaGrupos==null) this.mapaGrupos = new HashMap<>();
        return this.mapaGrupos;
    }

    private List<Grupo> getListaGrupos() {
        if (this.listaGrupos==null) this.listaGrupos = new ArrayList<>();
        return this.listaGrupos;
    }

    private List<Usuario> getListaUsuarios() {
        if (this.listaUsuarios==null) this.listaUsuarios = new ArrayList<>();
        return this.listaUsuarios;
    }

    private HashMap<Grupo,List<Usuario>> getMembramentos() {
        if (this.membramentos==null) this.membramentos = new HashMap<>();
        return this.membramentos;
    }

    private void adicionaMembramento(Grupo grupo,Usuario membro) {
        List<Usuario> membros;
        membros = this.getMembramentos().get(grupo);
        if (membros==null) membros = new ArrayList<>();
        membros.add(membro);

        this.getMembramentos().put(grupo, membros);
    }

    @Override
    public List<Usuario> getMembrosGrupo(Grupo g) {
        List<Usuario> resposta = this.getMembramentos().get(g);
        if (resposta==null) resposta = new ArrayList<>();

        return resposta;
    }

    @Override
    public List<Grupo> getAllGrupos() {
        return this.getListaGrupos();
    }
}
