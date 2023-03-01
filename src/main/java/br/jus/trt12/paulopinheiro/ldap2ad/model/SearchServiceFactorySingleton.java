package br.jus.trt12.paulopinheiro.ldap2ad.model;

import br.jus.trt12.paulopinheiro.ldap2ad.model.ad.AdSearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ldap.OpenLdapSearchService;

public class SearchServiceFactorySingleton {
    private static SearchServiceFactorySingleton instance;

    private String usuario;
    private String senha;

    private SearchService adSearchService;
    private SearchService openLdapSearchService;

    private SearchServiceFactorySingleton() {
    }

    public static SearchServiceFactorySingleton getInstance() {
        if (instance==null) instance = new SearchServiceFactorySingleton();
        return instance;
    }

    public void setCredentials(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public SearchService getAdSearchService() {
        if (this.adSearchService==null) {
            if ((this.usuario==null)||(this.usuario.isEmpty())||(this.senha==null)||(this.senha.isEmpty())) throw new IllegalStateException("Credenciais não foram informadas");
            this.adSearchService = new AdSearchService(usuario,senha);
        }
        return this.adSearchService;
    }

    public SearchService getOpenLdapSearchService() {
        if (this.openLdapSearchService==null) this.openLdapSearchService = new OpenLdapSearchService();
        return this.openLdapSearchService;
    }

    public void reloadAdCache(String usuario, String senha) {
        this.adSearchService = null;
    }
}
