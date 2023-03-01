/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trt12.paulopinheiro.ldap2ad.util.ad;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 *
 * @author 2360
 */
public class UtilAd {
    /**
     * Verifica se uma entrada de atributos corresponde a um objeto crítico de
     * sistema do Active Directory. Objetos críticos são usuários, grupos e outros
     * artefatos que já existem por padrão de instalação do Active Directory
     * (Ex: grupo Administradores)
     * @param atts O iterador de atributos correspondente ao objeto
     * @return A informação se se trata de um objeto crítico do Active Directory
     * @throws NamingException 
     */
    public static boolean isCriticalSystemObject(Attributes atts) throws NamingException {
        if (atts == null) return false;
        Attribute att = atts.get("isCriticalSystemObject");
        if (att==null) return false;
        String booleanValue = (String) att.get();
        return booleanValue != null && !booleanValue.isEmpty() && booleanValue.equalsIgnoreCase("true");
    }
}
