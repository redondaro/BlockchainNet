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

/** Regra de acesso de aplicações.
 *
 * @author Luiz Fernando Perez Redondaro */
public class Regra {
    
    /** Cliente que realizará a comunicação. */
    public String origem;
    
    /** Destinatário da comunicação. */
    public String destino;
    
    
    /** Método construtor.
     * @param origem Que realizará a comunicação
     * @param destino Que receberá a comunicação */
    public Regra(String origem, String destino) {
        this.origem = origem;
        this.destino = destino;
    }
    
    /** Compara a regra atual com outra, verificando se são iguais.
     * @param regra Regra
     * @return boolean Se a regra é igual esta */
    public boolean igual(Regra regra) {
        if (regra != null && regra.origem != null && regra.destino != null) {
            return (origem.toLowerCase().equals(regra.origem.toLowerCase())
                    && destino.toLowerCase().equals(regra.destino.toLowerCase()));
        }else
            return false;
    }
    
    /** Descrição.
     * @return String */
    @Override
    public String toString() {
        return origem + " " + destino;
    }
}
