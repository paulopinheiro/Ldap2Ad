package br.jus.trt12.paulopinheiro.ldap2ad.tests;

import br.jus.trt12.paulopinheiro.ldap2ad.model.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ad.AdSearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ldap.OpenLdapSearchService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;

public class TesteSearch {
    public static void teste(String usuario, String senha) throws LdapException, IOException, CursorException {        
        String grupoTeste = "GD_RBL_grp";
        String usuarioTeste = "2666";

        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Testando busca no Active Directory");
        System.out.println("-----------------------------------------------------------------------------");
        SearchService searchService = new AdSearchService(usuario,senha);
        exibir(searchService, grupoTeste,usuarioTeste);
        System.out.println("-----------------------------------------------------------------------------");

        System.out.println();

        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Testando busca no OpenLDAP");
        System.out.println("-----------------------------------------------------------------------------");
        searchService = new OpenLdapSearchService();
        exibir(searchService, grupoTeste, usuarioTeste);
        System.out.println("-----------------------------------------------------------------------------");
    }

    private static void exibir(SearchService search, String grupoTeste, String usuarioTeste) {
        Grupo g = search.getGrupoBySigla(grupoTeste);
        List<Usuario> membrosGrupo = search.getMembrosGrupo(g);
        Collections.sort(membrosGrupo);
        if (g==null) {
            System.out.println("Grupo " + grupoTeste + " não encontrado!");
        } else {
            System.out.println("Total: " + membrosGrupo.size() + " membros no grupo " + g);
            membrosGrupo.forEach(usuario -> System.out.println("\t" + usuario));
        }

        Usuario u = search.getUsuarioByUid(usuarioTeste);
        if (u==null) {
            System.out.println("Não encontrou usuário");
        } else {
            System.out.println("O(A) Usuário(a) " + u.getCn() + " é membro de " + u.getTodosGrupos().size() + " grupos.");
            u.getTodosGrupos().forEach(grupo -> System.out.println("\t" + grupo));
            System.out.println("Seu grupo primário é " + u.getGrupoPrimario());
        }
    }
}