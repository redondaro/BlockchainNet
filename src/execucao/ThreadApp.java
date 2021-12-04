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
import var.Enumeraveis;
import var.PreDefs;
import var.Regra;

/** Thread das aplicações do sistema, para recebimento de mensagens.
 *
 * @author Luiz Fernando Perez Redondaro */
public class ThreadApp extends Thread {
    
    /** Socket usado pela aplicação. */
    private java.net.DatagramSocket socket;
    
    /** Buffer para recebimento de mensagem. */
    private byte[] buffer = new byte[1024];
    
    /** Variáveis e recursos do sistema. */
    private Central central = null;
    
    /** Localização desta aplicação no ArrayList em controle.aplicacoes. */
    private int indice = -1;
    
    /** Método construtor.
     * @param central Central
     * @param porta int
     * @param indice int */
    public ThreadApp(Central central, int porta, int indice) {
        try {
            this.central = central;
            this.indice = indice;
            socket = new java.net.DatagramSocket(porta);
        } catch (java.net.SocketException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Porta " + porta +
                    " indisponível.\nEsta aplicação será encerrada!", "Ops...",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    /** Executar esta thread. */
    @Override
    public void run() {
        while (true) {
            int porta = -1;
            try {
                java.net.DatagramPacket pacote = new java.net.DatagramPacket(buffer, buffer.length);
                socket.receive(pacote);
                
                //se aplicação não estiver excluída
                if (!central.aplicacoes.get(indice).grupo.nome.equals(central.grupos.get(0).nome)) {
                    porta = pacote.getPort();
                    String recebido = new String(pacote.getData(), 0, pacote.getLength());

                    //receber mensagem, remontá-la em registro e conferir se é válida
                    Registro reg = new Registro("", "");
                    try (java.util.Scanner scan = new java.util.Scanner(recebido)) {
                        reg.autor = scan.next();
                        reg.timestamp = scan.nextLong();
                        reg.assinatura = scan.next();
                        reg.dados = scan.next();
                        if (scan.hasNextLine())
                            reg.dados += scan.nextLine();
                    }
                    //procurar aplicação, se faz parte do sistema e não foi excluída
                    int encontrado = -1;
                    for (int i = 0; i < central.aplicacoes.size(); i++)
                        if (central.aplicacoes.get(i).toString().equals(reg.autor)) {
                            if (!central.aplicacoes.get(i).grupo.nome.equals("excluido"))
                                encontrado = i;
                            break;
                        }
                    if (encontrado < 0) //aplicação desconhecida ou excluída
                        registrar(Enumeraveis.usuario_desconhecido.valor + " " + "porta:"+porta + " " + reg.dados);

                    //verificar assinatura da aplicação
                    else if (Cripto.verAssinatura(reg.autor, Cripto.hash(reg), reg.assinatura)) {
                        //verificar regra de acesso para esta aplicação
                        String orig = central.aplicacoes.get(encontrado).grupo.nome,
                               dest = central.aplicacoes.get(indice).grupo.nome;
                        boolean acesso = false;
                        for (Regra r: central.regras)
                            if (r.igual(new Regra(orig, dest))) {
                                acesso = true;
                                break;
                            }
                        if (acesso) {

                            //==================================================================
                            //==================================================================
                            // verificação de timestamp seria aqui, mas não será implementado :(
                            //==================================================================
                            //==================================================================

                            //tratar mensagem conforme as pré-definições
                            String msg = PreDefs.receberMensagem(central, encontrado, indice, reg.dados);
                            if (msg != null)
                                registrar(msg);

                        }else //acesso não permitido
                            registrar(Enumeraveis.acesso_nao_permitido.valor + " " + reg.autor + " " + reg.dados);

                    }else //mensagem/assinatura inválida
                        registrar(Enumeraveis.mensage_invalida.valor + " " + "porta:"+porta + " " + reg.dados);
                }
            } catch (java.io.IOException e) { //mensagem inválida/usuário desconhecido
                registrar(Enumeraveis.usuario_desconhecido.valor + " " + "porta:"+porta+" Mensagem inválida");
            }
        }
    }
    
    /** Adicionar registro na blockchain.
     * @param dados String informações a serem registradas */
    private void registrar(String dados) {
        Registro reg = new Registro(central.aplicacoes.get(indice).toString(), dados);
        reg.assinatura = central.aplicacoes.get(indice).assinar(Cripto.hash(reg));
        central.adicionarRegistro(reg);
    }
}
