package br.jus.trt12.paulopinheiro.ldap2ad.tests;

import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Testes {
    public static void testarCargaDados(List<Usuario> usuarios) {
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------");
        usuarios.forEach(usuario -> {
            System.out.println(usuario.getCn() + "-> " + usuario.getGrupoPrimario());
            System.out.println("Outros grupos:");
            usuario.getOutrosGrupos().forEach(grupo -> System.out.println("\t" + grupo));
            System.out.println();
        });
        System.out.println("Total: " + usuarios.size() + " usuários");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public static void testarMapa(HashMap mapa) {
        Iterator it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
