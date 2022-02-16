package br.jus.trt12.paulopinheiro.ldap2ad.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Usuario implements Serializable, Comparable {
    private final String uid;
    private final String dn;
    private final String mail;
    private final Grupo grupoPrimario;
    private final List<Grupo> outrosGrupos;

    public Usuario(String uid, String dn, String mail, Grupo grupoPrimario, List<Grupo> outrosGrupos) {
        this.uid = uid;
        this.dn = dn;
        this.mail = mail;
        this.grupoPrimario = grupoPrimario;
        if (outrosGrupos==null) {
            this.outrosGrupos = new ArrayList<>();
        } else {
            this.outrosGrupos = outrosGrupos;
        }
    }
    // Getters derivados: getSAMAccountName, getGivenName, getCn,
    // getUserPrincipalName

    public String getSAMAccountName() {
        return this.uid;
    }

    public String getUserPrincipalName() {
        return this.uid + "@trt12.jus.br";
    }

    public String getCn() {
        return getDn().split("\\,")[0].replace("CN=", "");
    }

    public String getPath() {
        return getDn().replace("CN=","").replace("cn=","").replace(this.getCn()+",", "").replace("gov", "jus");
    }

    public String getGivenName() {
        return getCn().split("\\s")[0];
    }

    public String getDn() {
        return dn;
    }

    public String getMail() {
        return mail;
    }

    public Grupo getGrupoPrimario() {
        return grupoPrimario;
    }

    public List<Grupo> getOutrosGrupos() {
        return Collections.unmodifiableList(outrosGrupos);
    }

    public List<Grupo> getTodosGrupos() {
        List<Grupo> todos = new ArrayList<Grupo>();
        todos.add(getGrupoPrimario());
        todos.addAll(this.getOutrosGrupos());

        return Collections.unmodifiableList(todos);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.uid);
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
        final Usuario other = (Usuario) obj;
        if (!Objects.equals(this.uid, other.uid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getCn();
    }

    @Override
    public int compareTo(Object o) {
        Usuario u = (Usuario) o;
        return this.getCn().compareTo(u.getCn());
    }
}
