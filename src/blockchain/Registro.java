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
package blockchain;

/** Unidade de dados a ser armazenada em um bloco do sistema blockchain.
 *
 * @author Luiz Fernando Perez Redondaro */
public class Registro {
    
    /** Chave pública do autor do registro. */
    public String autor;
    
    /** Tipo de registro, mensagem e/ou dados a serem armazenados. */
    public String dados;
    
    /** Timestamp. */
    public long timestamp;
    
    /** Assinatura do criador do registro. */
    public String assinatura = "";
    
    /** Método construtor.
     * @param autor String chave pública do autor do registro
     * @param dados String dados do registro */
    public Registro(String autor, String dados) {
        this.autor = autor;
        this.dados = dados;
        timestamp = new java.util.Date().getTime();
    }
    
    /** Descrição de todos os elementos do registro.
     * @return String - As informações do registro */
    @Override
    public String toString() {
        return autor + "\n" + dados + "\n" + Cripto.dataHora(timestamp);
    }
}
