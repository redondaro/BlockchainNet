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

/** Grupo de aplicações.
 *
 * @author Luiz Fernando Perez Redondaro */
public class Grupo {
    
    /** Nome do grupo. */
    public String nome;
    
    /** Cor do grupo. */
    public java.awt.Color cor;
    
    /** Construtor.
     * @param nome String
     * @param cor Color*/
    public Grupo(String nome, java.awt.Color cor) {
        this.nome = nome;
        this.cor = cor;
    }
    
    /** Cópia segura.
     * @return Grupo */
    @Override
    public Grupo clone() {
        return new Grupo(nome, cor);
    }
}
