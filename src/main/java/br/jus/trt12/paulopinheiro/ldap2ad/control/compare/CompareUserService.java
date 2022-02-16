package br.jus.trt12.paulopinheiro.ldap2ad.control.compare;

import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 2360
 */
public interface CompareUserService {
    /**
     * Efetua comparação de usuário no OpenLDAP com seus dados no Active Directory.
     * O fluxo é interrompido a priori se o usuário não existe no OpenLDAP, que
     * é a referência oficial para a comparação.
     * @param uid A identificação única (geralmente a matrícula)
     * @throws InvalidParameterException Se o usuário não existe no OpenLDAP
     */
    public void compararUsuario(String uid) throws InvalidParameterException;

    /**
     * Gera arquivo texto (conforme parâmetro fornecido), cujo conteúdo será o
     * despejo dos comandos de scripts levantado pela operação de comparação.
     * Se a lista estiver vazia nenhuma ação será realizada.
     * @param arquivo Local e nome do arquivo a ser gerado.
     *
     * @throws IOException Se houver algum erro de I/O.
     */
    public void criarScript(File arquivo) throws IOException;

    /**
     * Gera arquivo texto (conforme parâmetro <i>arquivo</i>), cujo conteúdo será o
     * despejo dos comandos de scripts do parâmetro <i>acoesAutomatizaveis</i>
     * Se o HashMap estiver vazio nenhuma ação será realizada.
     * @param arquivo Local e nome do arquivo a ser gerado.
     * @param acoesAutomatizaveis Lista de ações (descricao,comando)
     * @throws IOException Se houver algum erro de I/O.
     */
    public void criarScript(File arquivo, Map<String, String> acoesAutomatizaveis) throws IOException;

    /**
     * Retorna a HashMap(String,String) de ações automatizáveis em scripts que
     * devem ser tomadas para regularizar a situação do usuário (e seus grupos)
     * no Active Directory.Esta lista será esvaziada na inicialização do serviço
     * e no início de cada operação de comparação do usuário
     * (método compararUsuario)
     * Se estiver vazia após a utilização do método é porque nenhuma ação precisa
     * ser tomada
     * @return Hashmap de ações que serão transformadas em arquivos no script
     */
    public Map<String, String> getAcoesAutomatizaveis();

    /**
     * Lista das mensagens de alerta após operação de comparação
     * Esta lista será esvaziada na inicialização do serviço e no início
     * de cada operação de comparação do usuário (método compararUsuario)
     * Se estiver vazia após a utilização do método é porque nenhum alerta foi
     * gerado
     * @return Lista de mensagens de alerta geradas durante
     */
    public List<String> getMensagensAlerta();

    /**
     * Informa os dados do usuário encontrado pela pesquisa
     * @return Usuário encontrado pela pesquisa
     */
    public Usuario getUsuario();
}
