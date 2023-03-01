/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trt12.paulopinheiro.ldap2ad.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author 2360
 */
public class Util {
    public static String convertSidToStringSid(byte[] sid) {
        int offset, size;

        // sid[0] is the Revision, we allow only version 1, because it's the
        // only that exists right now.
        if (sid[0] != 1) {
            throw new IllegalArgumentException("SID revision must be 1");
        }

        StringBuilder stringSidBuilder = new StringBuilder("S-1-");

        // The next byte specifies the numbers of sub authorities (number of
        // dashes minus two)
        int subAuthorityCount = sid[1] & 0xFF;

        // IdentifierAuthority (6 bytes starting from the second) (big endian)
        long identifierAuthority = 0;
        offset = 2;
        size = 6;
        for (int i = 0; i < size; i++) {
            identifierAuthority |= (long) (sid[offset + i] & 0xFF) << (8 * (size - 1 - i));
            // The & 0xFF is necessary because byte is signed in Java
        }
        if (identifierAuthority < Math.pow(2, 32)) {
            stringSidBuilder.append(Long.toString(identifierAuthority));
        } else {
            stringSidBuilder.append("0x").append(
                    Long.toHexString(identifierAuthority).toUpperCase());
        }

        // Iterate all the SubAuthority (little-endian)
        offset = 8;
        size = 4; // 32-bits (4 bytes) for each SubAuthority
        for (int i = 0; i < subAuthorityCount; i++, offset += size) {
            long subAuthority = 0;
            for (int j = 0; j < size; j++) {
                subAuthority |= (long) (sid[offset + j] & 0xFF) << (8 * j);
                // The & 0xFF is necessary because byte is signed in Java
            }
            stringSidBuilder.append("-").append(subAuthority);
        }

        return stringSidBuilder.toString();
    }

    public static String getFinalObjectSid(byte[] objectSid) {
        String sid = convertSidToStringSid(objectSid);
        return sid.substring(sid.length()-4, sid.length());
    }

    public static String getPrefixoAutoridades(byte[] objectSid) {
        String sid = convertSidToStringSid(objectSid);
        return sid.substring(0,sid.length() - 5);
    }

    private static Map<String, String> getUserAttributes(DirContext ctx, String searchBase, String userName,
            String principalDomain, String... attributeNames)
            throws NamingException {
        if (StringUtils.isBlank(userName)) {
            throw new IllegalArgumentException("Username and password can not be blank.");
        }

        if (attributeNames.length == 0) {
            return Collections.emptyMap();
        }

        Attributes matchAttr = new BasicAttributes(true);
        BasicAttribute basicAttr = new BasicAttribute("userPrincipalName", userName + principalDomain);
        matchAttr.put(basicAttr);

        NamingEnumeration<? extends SearchResult> searchResult = ctx.search(searchBase, matchAttr, attributeNames);

        Map<String, String> result = new HashMap<>();

        if (searchResult.hasMore()) {
            NamingEnumeration<? extends Attribute> attributes = searchResult.next().getAttributes().getAll();

            while (attributes.hasMore()) {
                Attribute attr = attributes.next();
                String attrId = attr.getID();
                String attrValue = (String) attr.get();

                result.put(attrId, attrValue);
            }
        }
        return result;
    }
    /**
     * Converte um cursor NamingEnumeration para lista de Strings
     * @param cursor Cursor (NamingEnumeration) que deve necessariamente
     * corresponder a uma lista de strings, caso contrário será criada uma
     * exception de ClassCast
     * @return Lista de string correspondente ao cursor
     */
    public static List<String> parseListaString(NamingEnumeration<?> cursor) {
        List<String> resposta = new ArrayList<>();
        while (cursor.hasMoreElements()) {
            resposta.add((String) cursor.nextElement());
        }
        return resposta;
    }
}
