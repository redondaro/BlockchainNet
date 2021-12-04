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

/** Conjunto de registros que será minerado, formando um bloco no blockchain.
 *
 * @author Luiz Fernando Perez Redondaro */
public class Bloco {
    
    /** Chave pública do criador do bloco. */
    public String autor;
    
    /** Hash anterior. */
    public String hashAnterior;
    
    /** Timestamp. */
    public long timestamp;
    
    /** Valor de nonce para desafio. */
    public long nonce = 0;
    
    /** Assinatura do criador do bloco. */
    public String assinatura = "";
    
    /** Registros. */
    public java.util.ArrayList<Registro> registros = new java.util.ArrayList<>();
    
    /** Método construtor.
     * @param autor String chave pública do autor do bloco
     * @param hashAnterior String de hash do bloco anterior */
    public Bloco(String autor, String hashAnterior) {
        this.autor = autor;
        this.hashAnterior = hashAnterior;
        timestamp = new java.util.Date().getTime();
    }
    
    /** Descrição de todos os elementos do bloco.
     * @return String - As informações do bloco */
    @Override
    public String toString() {
        return autor + "\n" + hashAnterior + "\n" + Cripto.dataHora(timestamp) + "\n"
                + Long.toString(nonce) + "\n" + registros.size() + "\n" + Cripto.merkle(registros);
    }
}
