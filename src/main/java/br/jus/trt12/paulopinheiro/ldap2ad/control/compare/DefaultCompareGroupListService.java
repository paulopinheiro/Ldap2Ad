package br.jus.trt12.paulopinheiro.ldap2ad.control.compare;

import br.jus.trt12.paulopinheiro.ldap2ad.model.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.util.ArrayList;
import java.util.List;

public class DefaultCompareGroupListService implements CompareGroupListService {

    private final SearchService ldapService;
    private final SearchService adService;
    private final List<Grupo> gruposLdap;
    private final List<Grupo> gruposAd;

    private List<String> mensagensErro;
    private List<String> mensagensAlerta;

    public DefaultCompareGroupListService(SearchService ldapService, SearchService adService) {
        this.adService = adService;
        this.ldapService = ldapService;
        this.gruposAd = this.getAdService().getAllGrupos();
        this.gruposLdap = this.getLdapService().getAllGrupos();
        inicializarListas();
        compararListasGrupos();
    }

    private void inicializarListas() {
        this.mensagensAlerta = null;
        this.mensagensErro = null;
    }

    private void compararListasGrupos() {
        for (Grupo g : this.getGruposLdap()) comparaGrupoLdapComAd(g);
        for (Grupo g : this.getGruposAd()) comparaGrupoAdComLdap(g);
    }

    private void comparaGrupoLdapComAd(Grupo g) {
        List<Usuario> membrosGrupoLdap = this.getLdapService().getMembrosGrupo(g);
        if (!this.getGruposAd().contains(g)) adicionaAlerta("Grupo " + g + " não está no AD");
        else {
            List<Usuario> membrosGrupoAd = this.getAdService().getMembrosGrupo(g);
            //Considerar um método local pra substituir null por grupo vazio pra evitar repetição de código
            if (membrosGrupoAd == null || membrosGrupoAd.isEmpty()) adicionaAlerta("Grupo " + g + " não tem membros no AD e pode ser removido");
            else {
                for (Usuario u : membrosGrupoLdap) {
                    if (!membrosGrupoAd.contains(u)) {
                        adicionaAlerta("Usuário " + u + " está no grupo " + g + " no LDAP, mas não no AD");
                    }
                }
            }
        }
    }

    private void comparaGrupoAdComLdap(Grupo g) {
        List<Usuario> membrosGrupoAd = this.getAdService().getMembrosGrupo(g);
        if (!this.getGruposLdap().contains(g)) adicionaErro("Grupo " + g + " existe no AD, mas não no LDAP.");
        else {
            List<Usuario> membrosGrupoLdap = this.getLdapService().getMembrosGrupo(g);
            //Considerar um método local pra substituir null por grupo vazio pra evitar repetição de código
            if ((membrosGrupoLdap==null)||membrosGrupoLdap.isEmpty()) {
                if ((membrosGrupoAd!=null)&&!membrosGrupoAd.isEmpty()) adicionaErro("Grupo " + g + " tem membros no AD, mas não no LDAP.");
            } else {
                for (Usuario u: membrosGrupoAd) {
                    if (!membrosGrupoLdap.contains(u)) adicionaErro("Usuário " + u + " está no grupo " + g + " no AD, mas não no LDAP.");
                }
            }
        }
    }

    private void adicionaErro(String mensagem) {
        this.getMensagensErro().add(mensagem);
    }

    private void adicionaAlerta(String mensagem) {
        this.getMensagensAlerta().add(mensagem);
    }

    @Override
    public List<String> getMensagensErro() {
        if (this.mensagensErro == null) {
            this.mensagensErro = new ArrayList<>();
        }
        return this.mensagensErro;
    }

    @Override
    public List<String> getMensagensAlerta() {
        if (this.mensagensAlerta == null) {
            this.mensagensAlerta = new ArrayList<>();
        }
        return this.mensagensAlerta;
    }

    private List<Grupo> getGruposLdap() {
        return this.gruposLdap;
    }

    private List<Grupo> getGruposAd() {
        return this.gruposAd;
    }

    /**
     * @return the ldapService
     */
    private SearchService getLdapService() {
        return ldapService;
    }

    /**
     * @return the adService
     */
    private SearchService getAdService() {
        return adService;
    }
}
