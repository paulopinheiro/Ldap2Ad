package br.jus.trt12.paulopinheiro.ldap2ad.control.search;

import javax.naming.directory.DirContext;

public interface GeneralDirContext {
    public DirContext getDirContext();
    public void close();
}
