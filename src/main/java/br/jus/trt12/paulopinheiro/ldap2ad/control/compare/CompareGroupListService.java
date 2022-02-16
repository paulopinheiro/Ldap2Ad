package br.jus.trt12.paulopinheiro.ldap2ad.control.compare;

import java.util.List;

public interface CompareGroupListService {
    /**
     * Lista das mensagens de erro após operação de comparação
     * Erros são detectados quando são encontrados:
     * - Grupos que existem no AD, mas não no LDAP
     * - Grupos no AD que possuem usuários que não existem no LDAP
     * - Grupos no ad que possuem usuários que existem no LDAP, mas não são
     * membros.
     * @return Lista de mensagens de alerta geradas durante o processamento
     */
    public List<String> getMensagensErro();
    /**
     * Lista das mensagens de alerta após operação de comparação
     * Esta lista será esvaziada na inicialização do serviço e no início
     * de cada operação de comparação do grupo (método compararGrupo)
     * Se estiver vazia após a utilização do método é porque nenhum alerta foi
     * gerado.
     * Alertas são gerados quando são encontrados:
     * - Grupos que existem no LDAP, mas não no AD
     * - Grupos do LDAP que esão vazios no AD
     * - Grupos no LDAP que possuem usuários que não existem no AD
     * - Grupos no LDAP que possuem usuários que existem no AD, mas não são 
     * membros.
     * @return Lista de mensagens de alerta geradas durante o processamento
     */
    public List<String> getMensagensAlerta();
}
