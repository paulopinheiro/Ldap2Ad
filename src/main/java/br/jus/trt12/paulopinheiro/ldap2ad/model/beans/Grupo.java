package br.jus.trt12.paulopinheiro.ldap2ad.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class Grupo implements Serializable, Comparable {
    private final String sigla;
    private final String descricao;
    private final String dn;

    public Grupo(String sigla, String descricao, String dn) {
        this.sigla = sigla;
        this.descricao = descricao;
        this.dn = dn;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDn() {
        return dn;
    }

    public String getPath() {
        return getDn().replace("CN=","").replace("cn=","").replace(this.getSigla()+",", "").replace("DC=gov", "DC=jus");
    }

    @Override
    public String toString() {
        String resposta = this.getSigla();
//        if (this.getDescricao()!=null&&!this.getDescricao().isEmpty())
//            resposta = resposta + "(" + this.getDescricao() + ")";
        return resposta;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.sigla);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grupo other = (Grupo) obj;
        if (!Objects.equals(this.sigla, other.sigla)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        Grupo g = (Grupo) o;
        return this.getSigla().compareTo(g.getSigla());
    }
}
