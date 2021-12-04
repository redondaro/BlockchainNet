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
package execucao;

import blockchain.Cripto;
import blockchain.Registro;
import blockchain.Usuario;
import var.Grupo;
import var.PreDefs;

/** Aplicação individual do sistema.
*
* @author Luiz Fernando Perez Redondaro */
public class Aplicacao extends Usuario {
    
    /** Porta. */
    public final int porta;
    
    /** Grupo da aplicação. */
    public Grupo grupo;
    
    /** Posição da aplicação na exibição em X. */
    public int x;
    
    /** Posição da aplicação na exibição em Y. */
    public int y;
    
    /** Valor usado pela aplicação. */
    public String dados;
    
    /** Variáveis e recursos do sistema. */
    private Central central = null;
    
    /** Thread para receber mensagens. */
    private ThreadApp thread;
    
    /** Localização desta aplicação no ArrayList em controle.aplicacoes. */
    private int indice = -1;
    
    /** Construtor.
     * @param porta int
     * @param grupo Grupo
     * @param x int posição horizontal
     * @param y int posição vertical
     * @param dados String */
    public Aplicacao(int porta, Grupo grupo, int x, int y, String dados) {
    	super();
        this.porta = porta;
        this.grupo = grupo;
        this.x = x;
        this.y = y;
        if (dados == null)
            this.dados = "";
        else
            this.dados = dados;
    }
    
    /** Recebe o controle, verifica posição da aplicação no ArrayList e inicia thread.
     * @param central Central */
    public void iniciar(Central central) {
        if (this.central == null) {
            this.central = central;
            for (int i=central.aplicacoes.size()-1; i >= 0; i--)
                if (this.porta == central.aplicacoes.get(i).porta) {
                    indice = i;
                    break;
                }
            thread = new ThreadApp(central, porta, indice);
            thread.start();
        }
    }
    
    /** Enviar mensagem para determinado destinatário.
     * @param destinatario int posição da aplicação na ArrayList
     * @return boolean se foi enviado */
    public boolean enviarMensagem(int destinatario) {
        String msg = PreDefs.enviarMensagem(central, indice, destinatario);
        if (msg != null) {
            try (java.net.DatagramSocket socket = new java.net.DatagramSocket();) {
                
                //enviar mensagem com dados de bloco, para ser verificada
                Registro reg = new Registro(central.aplicacoes.get(indice).toString(), msg.substring(1));
                if (msg.charAt(0) == 's' || msg.charAt(0) == 'S')
                    reg.assinatura = central.aplicacoes.get(indice).assinar(Cripto.hash(reg));
                msg = reg.autor + " " + reg.timestamp + " " + reg.assinatura + " " + reg.dados;
                
                byte[] buffer = msg.getBytes();
                java.net.InetAddress endereco = java.net.InetAddress.getByName("localhost");
                java.net.DatagramPacket pacote = new java.net.DatagramPacket(buffer, buffer.length,
                        endereco, central.aplicacoes.get(destinatario).porta);
                socket.send(pacote);
                return true;
            } catch (java.net.SocketException | java.net.UnknownHostException
                    | java.lang.NullPointerException ex) {
            } catch (java.io.IOException ex) {}
        }
        return false;
    }
}
