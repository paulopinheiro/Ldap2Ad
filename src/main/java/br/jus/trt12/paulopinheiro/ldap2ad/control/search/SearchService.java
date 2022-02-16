package br.jus.trt12.paulopinheiro.ldap2ad.control.search;

import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.util.List;

public interface SearchService {
    public Usuario getUsuarioByUid(String uid);
    public Grupo getGrupoBySigla(String sigla);
    public List<Usuario> getMembrosGrupo(Grupo g);
    public List<Grupo> getAllGrupos();
}
