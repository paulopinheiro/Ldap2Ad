package br.jus.trt12.paulopinheiro.ldap2ad.model;

import javax.naming.directory.DirContext;

public interface GeneralDirContext {
    public DirContext getDirContext();
    public void close();
}
