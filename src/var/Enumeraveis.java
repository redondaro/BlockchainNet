/*
 *        UNIVERSIDADE ESTADUAL PAULISTA "JÚLIO DE MESQUITA FILHO"
 *                     Câmpus de Presidente Prudente
 * 
 *                     Trabalho de Conclusão de Curso
 *        Autenticação Entre Aplicações Distribuídas com Blockchain
 * 
 *        Aluno: Luiz Fernando Perez Redondaro
 *        Orientador: Prof. Dr. Milton Hirokazu Shimabukuro
 */
package var;

/** Enumeráveis para registros.
 *
 * @author Luiz Fernando Perez Redondaro */
public enum Enumeraveis {
    
    //acesso de aplicação
	/** Acesso de aplicação OK */
    mensagem_ok(1),
    /** Aplicação acessando outra não permitida */
    acesso_nao_permitido(2),
    /** Timestamp da mensagem expirado */
    timestamp_expirado (3),
    /** Mensagem não assinada ou com assinatura inválida */
    mensage_invalida(4),
    /** Aplicação desconhecida pelo sistema */
    usuario_desconhecido(5),
    
    //administrativos (somente para admin)
    /** Adicionar grupo */
    grupo_adicionar(11),
    /** Alterar grupo */
    grupo_alterar(12),
    /** Excluir grupo */
    grupo_excluir(13),
    /** Adicionar regra */
    regra_adicionar(21),
    /** Alterar regra */
    regra_alterar(22),
    /** Excluir regra */
    regra_excluir(23),
    /** Adicionar par */
    par_adicionar(31),
    /** Alterar par */
    par_alterar(32),
    /** Excluir par */
    par_excluir(33),
    
    //diversos
    /** Mensagem de aplicação */
    msg_aplicacao(50)
    ;
    
    /** Valor do enumerável. */
    public final int valor;
    /** Método construtor.
     * @param n int */
    Enumeraveis(int n) {
        valor = n;
    }
    
    /** Obter a descrição de um dado valor de registro.
     * 
     * @param valor int
     * @return String */
    public static synchronized String descricao(int valor) {
        switch (valor) {
            case 1: return "mensagem_ok";
            case 2: return "acesso_nao_permitido"; case 3: return "timestamp_expirado";
            case 4: return "mensage_invalida"; case 5: return "usuario_desconhecido";
            
            case 11: return "tipo_adicionar";  case 12: return "tipo_alterar";  case 13: return "tipo_excluir";
            case 21: return "regra_adicionar"; case 22: return "regra_alterar"; case 23: return "regra_excluir";
            case 31: return "par_adicionar";   case 32: return "par_alterar";   case 33: return "par_excluir";
            
            default: return "msg_aplicação";
        }
    }
}
