package br.jus.trt12.paulopinheiro.ldap2ad.control.compare;

import br.jus.trt12.paulopinheiro.ldap2ad.control.search.SearchService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.ad.ScriptsService;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Grupo;
import br.jus.trt12.paulopinheiro.ldap2ad.model.beans.Usuario;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DefaultCompareUserService implements CompareUserService {
    private static final String[] EXCECOES_GRUPOS_LDAP = {"TESTE_grp",
                                                          "intranet",
                                                          "aarh_oj_grp",
                                                          "informatica_grp",
                                                          "Grp_peticionamento",
                                                          "wiki_grp",
                                                          "programador",
                                                          "CASAN_grp"};
    private static final String[] EXCECOES_GRUPOS_AD = {"Usuários do domínio",
                                                        "admins",
                                                        "Administradores",
                                                        "Usuários"};
    private final SearchService adService;
    private final SearchService ldapService;

    // Usados para termos de comparação
    private Usuario usuarioLdap;
    private Usuario usuarioAd;

    private List<String> mensagensAlerta;
    private Map<String,String> acoesAutomatizaveis;

    private DefaultCompareUserService(SearchService ldapService, SearchService adService) {
        this.ldapService = ldapService;
        this.adService = adService;
        inicializarDados();
    }

    /**
     * Inicializa o serviço de comparação
     * @param ldapService Serviço de busca do OpenLDAP
     * @param adService Serviço de busca do ActiveDirectory
     * @param nomeUsuario Usuário sendo pesquisado
     */
    public DefaultCompareUserService(SearchService ldapService, SearchService adService, String nomeUsuario) {
        this(ldapService,adService);
        compararUsuario(nomeUsuario);
    }

    /**
     * Efetua comparação de usuário no OpenLDAP com seus dados no Active Directory.
     * O fluxo é interrompido a priori se o usuário não existe no OpenLDAP, que
     * é a referência oficial para a comparação.
     * @param uid A identificação única (geralmente a matrícula)
     * @throws InvalidParameterException Se o usuário não existe no OpenLDAP
     */
    @Override
    public void compararUsuario(String uid) throws InvalidParameterException {
        inicializarDados();
        conferirExistenciaUsuarioLdap(uid);
        conferirUsuarioCadastradoAD(uid);
        conferirDadosUsuario(uid);
        conferirExistenciaGruposUsuario();
        conferirMembramentoLdapAd();
        conferirGrupoPrimario();
        conferirMembramentoAdLdap();
    }

    /**
     * Confere a existência do usuário no OpenLDAP.
     * @throws InvalidParameterException Se o usuário não foi encontrado
     */
    private void conferirExistenciaUsuarioLdap(String uid) throws InvalidParameterException {
        this.usuarioLdap = ldapService.getUsuarioByUid(uid);
        if (usuarioLdap==null) 
            throw new InvalidParameterException("Usuário com identificação " + uid + " não encontrado no OpenLDAP.");
    }

    /**
     * Consulta o Active Directory pela existência do usuário com o referido uid
     * Caso o usuário ainda não exista uma ação automatizável de geração de
     * usuário será tomada (HashMap de ações será atualizado)
     * @param uid 
     */
    private void conferirUsuarioCadastradoAD(String uid) {
        this.usuarioAd = adService.getUsuarioByUid(uid);
        if (this.usuarioAd == null) {
            String mensagem = "# Criar usuário " + this.usuarioLdap.toString();
            String comando = ScriptsService.comandoCriacaoUsuario(usuarioLdap);
            this.novaAcao(mensagem,comando);
        }         //TODO: Verificar se usuário está habilitado no AD
    }

    /**
     * Consulta usuário no OpenLDAP e no Active Directory e compara os dados.
     * Até esta versão os dados comparados são: o nome completo(CN), o DN e o
     * email.
     * Em caso de alguma diferença uma mensagem de alerta será adicionada à lista
     * Caso o usuário não exista no Active Directory nenhuma ação será tomada
     * @param uid O UID o do usuário
     */
    private void conferirDadosUsuario(String uid) {
        if (this.usuarioAd==null) return;

        if (!this.usuarioAd.getCn().equals(this.usuarioLdap.getCn()))
            novaMensagemAlerta(mensagemInconsistenciaDados(uid, "nome completo", usuarioAd.getCn(), usuarioLdap.getCn()));
        if (!this.usuarioAd.getDn().equalsIgnoreCase(usuarioLdap.getDn()))
            novaMensagemAlerta(mensagemInconsistenciaDados(uid,"DN", usuarioAd.getDn(), usuarioLdap.getDn()));
        if (!this.usuarioAd.getMail().equals(usuarioLdap.getMail()))
            novaMensagemAlerta(mensagemInconsistenciaDados(uid,"e-mail", usuarioAd.getMail(),usuarioLdap.getMail()));
    }

    /**
     * Consulta lista de grupos aos quais o usuário pertence no OpenLDAP e 
     * verifica a existência dos mesmos no Active Directory
     * Em caso de algum grupo do OpenLDAP não existir no Active Directory ações
     * automatizáveis serão tomadas (HashMap de ações será atualizado)
     */
    private void conferirExistenciaGruposUsuario() {
        for (Grupo g:getListaGruposFiltrada(usuarioLdap.getTodosGrupos(),EXCECOES_GRUPOS_LDAP)) {
            if (adService.getGrupoBySigla(g.getSigla())==null) {
                novaAcao("# Criar grupo " + g,ScriptsService.comandoCriacaoGrupo(g));
            }
        }
    }

    /**
     * Consulta lista de grupos aos quais o usuário pertence no OpenLDAP e 
     * compara com a lista dos quais pertence no Active Directory. Ações 
     * automatizáveis serão tomadas (Hashmap de ações será atualizado) caso o 
     * usuário pertença a algum grupo no OpenLDAP e não no Active Directory.
     */
    private void conferirMembramentoLdapAd() {
        List<Grupo> listaFinal = new ArrayList<>();
        List<Grupo> listaLdapFiltrada = this.getListaGruposFiltrada(usuarioLdap.getTodosGrupos(), EXCECOES_GRUPOS_LDAP);
        if (usuarioAd==null) {
            listaFinal = listaLdapFiltrada;
        } else {
            for (Grupo g:listaLdapFiltrada) {
                if ((adService.getGrupoBySigla(g.getSigla())==null)||(!usuarioAd.getTodosGrupos().contains(g))) {
                    listaFinal.add(g);
                }
            }
        }
        for (Grupo g:listaFinal) {
            novaAcao("# Adicionar " + usuarioLdap + " ao grupo " + g, ScriptsService.comandoMembramento(g.getSigla(),usuarioLdap.getSAMAccountName()));
        }
    }

    /**
     * Consulta grupo primário do usuário no OpenLDAP e compara com o seu grupo
     * primário no Active Directory. Caso não seja o mesmo grupo uma ação
     * automatizável será tomada (HashMap de ações será atualizado)
     * @param uid 
     */
    private void conferirGrupoPrimario() {
        if ((usuarioAd==null)||(!usuarioAd.getGrupoPrimario().equals(usuarioLdap.getGrupoPrimario()))) {
            novaAcao("# Definir " + usuarioLdap.getGrupoPrimario() + " como grupo primário do usuário " + usuarioLdap, ScriptsService.comandoGrupoPrimario(usuarioLdap.getGrupoPrimario().getSigla(),usuarioLdap.getSAMAccountName()));
        }
    }

    /**
     * Consulta lista de grupos aos quais o usuário pertence no Active Directory
     * e compara com a lista dos quais pertence no OpenLDAP. Mensagens de alerta
     * serão geradas caso o usuário pertença a algum grupo do Active Directory
     * e não no OpenLDAP (desde que seja um grupo <b>existente</b> neste).
     * @param uid 
     */
    private void conferirMembramentoAdLdap() {
        if (usuarioAd==null) return;
        List<Grupo> listaAdFiltrada = this.getListaGruposFiltrada(usuarioAd.getTodosGrupos(), EXCECOES_GRUPOS_AD);
        List<Grupo> listaFinal = new ArrayList<>();
        for (Grupo g : listaAdFiltrada) {
            if ((this.ldapService.getGrupoBySigla(g.getSigla()) == null) || (!usuarioLdap.getTodosGrupos().contains(g))) {
                listaFinal.add(g);
            }
        }
        for (Grupo g:listaFinal) {
            novaMensagemAlerta("Usuário " + usuarioAd + " pertence ao grupo " + g + " no Active Directory, mas não no OpenLDAP");
        }
    }

    /**
     * Método para adicionar ações automatizáveis ao HashMap
     * @param mensagem Descrição da ação sendo tomada
     * @param comando Comando do pacote AD do PowerShell
     */
    private void novaAcao(String mensagem, String comando) {
        this.getAcoesAutomatizaveis().put(mensagem, comando);
    }

    private void novaMensagemAlerta(String mensagem) {
        this.getMensagensAlerta().add(mensagem);
    }

    /**
     * Lista das mensagens de alerta após operação de comparação
     * Esta lista será esvaziada na inicialização do serviço e no início
     * de cada operação de comparação do usuário (método compararUsuario)
     * Se estiver vazia após a utilização do método é porque nenhum alerta foi
     * gerado
     * @return Lista de mensagens de alerta geradas durante
     */
    @Override
    public List<String> getMensagensAlerta() {
        if (this.mensagensAlerta==null) this.mensagensAlerta = new ArrayList<>();
        return this.mensagensAlerta;
    }

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
    @Override
    public Map<String,String> getAcoesAutomatizaveis() {
        if (this.acoesAutomatizaveis==null) this.acoesAutomatizaveis = new LinkedHashMap<>();
        return this.acoesAutomatizaveis;
    }

    /**
     * Gera arquivo texto (conforme parâmetro fornecido), cujo conteúdo será o
     * despejo dos comandos de scripts levantado pela operação de comparação.
     * Se a lista estiver vazia nenhuma ação será realizada.
     * @param arquivo Local e nome do arquivo a ser gerado.
     * 
     * @throws IOException Se houver algum erro de I/O.
     */
    @Override
    public void criarScript(File arquivo) throws IOException {
        this.criarScript(arquivo,this.getAcoesAutomatizaveis());
    }

    /**
     * Gera arquivo texto (conforme parâmetro <i>arquivo</i>), cujo conteúdo será o
     * despejo dos comandos de scripts do parâmetro <i>acoesAutomatizaveis</i>
     * Se o HashMap estiver vazio nenhuma ação será realizada.
     * @param arquivo Local e nome do arquivo a ser gerado.
     * @param acoesAutomatizaveis Lista de ações (descricao,comando)
     * @throws IOException Se houver algum erro de I/O.
     */
    @Override
    public void criarScript(File arquivo, Map<String,String> acoesAutomatizaveis) throws IOException {
        if ((arquivo==null)||(arquivo.getName()==null)||(arquivo.getName().isEmpty())) throw new InvalidParameterException("Informe o local e nome do arquivo a ser salvo.");
        if (arquivo.isDirectory()) throw new InvalidParameterException("Informe o nome do arquivo");
        if (!arquivo.getParentFile().canWrite()) throw new InvalidParameterException("Não é possível gravar na pasta " + arquivo.getParent());

        //garantir extensão .PS1
        File script;
        if (!arquivo.getName().toUpperCase().endsWith(".PS1")) {
            script = new File(arquivo.getAbsolutePath() + ".PS1");
        } else script = arquivo;

        String conteudo = "#Script para sincronizar dados do LDAP para o AD\n";
        conteudo = conteudo + "#Usuario: " + this.getUsuario() + "\n\n\n";

        if (!script.exists()) script.createNewFile();

        //navegar no Map, gravando o comentário e o comando para cada laço
        for (Map.Entry<String, String> entry : acoesAutomatizaveis.entrySet()) {
            conteudo = conteudo + entry.getKey() + "\n" + entry.getValue() + "\n\n";
        }
        FileOutputStream stream = new FileOutputStream(script);
        byte[] bytesConteudo = conteudo.getBytes();
        stream.write(bytesConteudo);
        stream.flush();
        stream.close();
    }

    private void inicializarDadosUsuario() {
        this.usuarioLdap=null;
        this.usuarioAd=null;
    }

    @Override
    public Usuario getUsuario() {
        return usuarioLdap;
    }

    protected Usuario getUsuarioAd() {
        return usuarioAd;
    }

    /**
     * Inicializa informações de comparação e de apoio
     */
    private void inicializarDados() {
        inicializarListas();
        inicializarDadosUsuario();
    }

    /**
     * Inicializa as listas de alertas de ações necessárias manuais a serem
     * tomadas pelo usuário, de ações automatizáveis em scripts e dos respectivos
     * comandos destas
     */
    private void inicializarListas() {
        this.mensagensAlerta=null;
        this.acoesAutomatizaveis=null;
    }

    private static String mensagemInconsistenciaDados(String uid, String dado, String dadoAd, String dadoLdap) {
        return "Inconsistência de " + dado + " para o usuário " + uid +": \n"
             + "\tNo Active Directory: " + dadoAd + ". \n"
             + "\tNo OpenLDAP: " + dadoLdap + ".";
    }

    private List<Grupo> getListaGruposFiltrada(List<Grupo> grupos, String[] excecoes) {
        List<Grupo> resposta = new ArrayList<>();
        for (Grupo g:grupos) {
            if (!Arrays.asList(excecoes).contains(g.getSigla()))
                resposta.add(g);
        }
        return resposta;
    }
}
