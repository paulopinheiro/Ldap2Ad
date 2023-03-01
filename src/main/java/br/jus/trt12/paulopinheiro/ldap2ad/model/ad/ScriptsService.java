/**
 * Métodos estáticos que formam comandos de Power Shell, pacote para Active
 * Directory.
 */
package br.jus.trt12.paulopinheiro.ldap2ad.model.ad;

import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;

public class ScriptsService {
    public static String comandoCriacaoUsuario(Usuario usuario) {
        String command = "New-ADUser -SamAccountName \"" + usuario.getSAMAccountName() + "\" -Name \"" + usuario.getCn()
                       + "\" -Path \"" + usuario.getPath() + "\" -GivenName \"" + usuario.getGivenName()
                       + "\" -EmailAddress \"" + usuario.getMail() + "\" -UserPrincipalName \"" + usuario.getUserPrincipalName()
                       + "\" -AccountPassword $(ConvertTo-SecureString -AsPlainText \"trt12\" -Force) "
                       + " -ChangePasswordAtLogon $True -Enabled $True";
        return command;
    }

    public static String comandoCriacaoGrupo(Grupo g) {
        String command = "New-ADGroup -Name \"" + g.getSigla() + "\" -Description \""+ g.getDescricao() + "\" -SAmAccountName " + g.getSigla() + " -GroupScope Global -Path \"" + g.getPath() +"\"";
        return command;
    }

    public static String comandoMembramento(String siglaGrupo, String uidUsuario) {
        String command = "Add-ADGroupMember -Identity \"" + siglaGrupo + "\" -members \"" + uidUsuario + "\"";
        return command;
    }

    public static String comandoGrupoPrimario(String siglaGrupo, String uidUsuario) {
        String command = "Set-ADUser -Identity \"" + uidUsuario + "\" -Replace @{primaryGroupID=(Get-ADGroup \"" + siglaGrupo + "\" -Properties primaryGroupToken).primaryGroupToken}";
        return command;
    }

}
