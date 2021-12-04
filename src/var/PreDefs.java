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

import blockchain.Cripto;
import execucao.Aplicacao;
import execucao.Central;

/** Pré-definições de aplicações a serem executadas no sistema.
*
* @author Luiz Fernando Perez Redondaro */
public class PreDefs {
    
    /** Imprimir aplicações, registros, blocos, etc. */
    public final static boolean DEBUG = true;
    
    /** Constante de quantos registros são adicionados por bloco. */
    public final static int NUM_REGISTRO_BLOCO = 5;
    
    /** Grupos de aplicações do sistema. Esta função deverá ser usada apenas na
     * inicialização do sistema.
     * @return ArrayList de String de grupos. */
    public static synchronized java.util.ArrayList<Grupo> grupos() {
        java.util.ArrayList<Grupo> retorno = new java.util.ArrayList<>();
        
        //excluída             nome       cor
        retorno.add(new Grupo("excluido", java.awt.Color.BLACK));
        
        //administrador do sistema
        retorno.add(new Grupo("admin", java.awt.Color.WHITE));
        
        //aplicações 1
        retorno.add(new Grupo("chat", java.awt.Color.CYAN));
        
        //aplicações 2
        retorno.add(new Grupo("cliente", java.awt.Color.GREEN));
        retorno.add(new Grupo("impressora", java.awt.Color.PINK));
        retorno.add(new Grupo("dados", java.awt.Color.ORANGE));
        
        //aplicações 3
        retorno.add(new Grupo("intermed", java.awt.Color.MAGENTA));
        retorno.add(new Grupo("comprador", java.awt.Color.BLUE));
        retorno.add(new Grupo("vendedor", java.awt.Color.RED));
        
        return retorno;
    }
    
    /** Regras de acesso do sistema. Esta função deverá ser usada apenas na
     * inicialização do sistema.
     * @return ArrayList de Regras de acesso. */
    public static synchronized java.util.ArrayList<Regra> regras() {
    	java.util.ArrayList<Regra> retorno = new java.util.ArrayList<>();
        
        //aplicações 1        origem, destino
        retorno.add(new Regra("chat", "chat"));
        
        //aplicações 2
        retorno.add(new Regra("cliente", "impressora"));
        retorno.add(new Regra("cliente", "dados"));
        
        //aplicações 3
        retorno.add(new Regra("comprador", "intermed"));
        retorno.add(new Regra("comprador", "vendedor"));
        
        return retorno;
    }
    
    /** Máquinas que serão usadas no sistema. Esta função deverá ser usada
     * apenas na inicialização do sistema.
     * @return ArrayList de Pares do sistema. */
    public static synchronized java.util.ArrayList<Aplicacao> aplicacoes() {
    	
    	//inicializa o provedor de segurança para o sistema blockchain
    	Cripto.iniciar();
    	
    	java.util.ArrayList<Grupo> grupos = grupos();
    	java.util.ArrayList<Aplicacao> retorno = new java.util.ArrayList<>();
        int contadorPorta = 1230;
        
        //a primeira aplicação deverá ser o administrador
        //                        porta            grupo  posição x   y   dados
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(1), 45, 40, ""));
        
        //aplicações 1
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(2), 195, 55, ""));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(2), 75, 155, ""));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(2), 195, 155, ""));
        
        //aplicações 2
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(3), 320, 40, "1 0"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(3), 430, 40, "0 1"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(4), 320, 145, ""));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(5), 430, 145, ""));
        
        //aplicações 3
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(6).clone(), 105, 270, "100"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(6).clone(), 215, 270, "200"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(7).clone(), 50, 360, "100 0 0"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(7).clone(), 50, 460, "500 0 0"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(8).clone(), 270, 360, "0 500"));
        retorno.add(new Aplicacao(contadorPorta++, grupos.get(8).clone(), 270, 460, "0 200"));
        
        return retorno;
    }
    
    /** Elementos gráficos de informações a serem exibidos.
     * @param g2 Graphics2D interface de exibição.
     * @param grupos ArrayList de Grupos do sistema
     * @param regras ArrayList de Regras do sistema */
    public static synchronized void legendas(java.awt.Graphics2D g2,
            java.util.ArrayList<Grupo> grupos, java.util.ArrayList<Regra> regras) {
        int posicao = 202, inicial = 202;
        g2.setColor(java.awt.Color.DARK_GRAY);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g2.drawString("GRUPOS", 360, posicao + 18);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        posicao += 30;
        for (Grupo g: grupos) {
            g2.drawString(g.nome, 380, posicao);
            posicao += 12;
        }
        posicao += 12;
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g2.drawString("REGRAS", 360, posicao);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        posicao += 12;
        for (Regra r: regras) {
            g2.drawString(r.origem + " -> " + r.destino, 360, posicao);
            posicao += 12;
        }
        
        //informações dos dados, adicionar +12 na posição para cada linha
        posicao += 12;
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g2.drawString("DADOS", 360, posicao);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        posicao += 12;
        g2.drawString("Cliente: acessos (0 ou 1)", 360, posicao);
        posicao += 12;
        g2.drawString("<impressora> <dados>", 360, posicao);
        posicao += 15;
        g2.drawString("Comprador e Vendedor:", 360, posicao);
        posicao += 12;
        g2.drawString("<saldo> <itens> <aut>", 360, posicao);
        posicao += 15;
        g2.drawString("Intermed:", 360, posicao);
        posicao += 12;
        g2.drawString("<valor que autoriza>", 360, posicao);
        
        g2.setStroke(new java.awt.BasicStroke(1.0f));
        g2.drawRect(350, inicial, 140, posicao-inicial+12);
        g2.setStroke(new java.awt.BasicStroke(3.0f));
        inicial += 25;
        for (Grupo g: grupos) {
            g2.setColor(g.cor);
            g2.drawRect(362, inicial, 12, 3);
            inicial += 12;
        }
    }
    
    /** Pré configuração para envio da mensagem. O primeiro caractere indica se
     * a mensagem deverá ser assinada (s ou n).
     * @param central Central
     * @param origem int posição da aplicação no ArrayList
     * @param destino int posição da aplicação no ArrayList
     * @return String mensagem de retorno, null caso não seja enviada */
    public static synchronized String enviarMensagem(Central central, int origem, int destino) {
    	String msg = "Mensagem a ser enviada (padrão: \"<mensagem>\")", padrao = "<mensagem>";
        
        switch (central.aplicacoes.get(origem).grupo.nome) {
            
            //definir mensagens para aplicações
            case "comprador":
                msg = "Valor para compra (padrão: \"100\")";
                padrao = "100";
                if (central.aplicacoes.get(destino).grupo.nome.equals("intermed"))
                    msg = "Solicitar valor para compra (padrão: \"100\")";
                break;
                
        }
        javax.swing.JCheckBox check = new javax.swing.JCheckBox("Assinar a mensagem", null, true);
        msg = javax.swing.JOptionPane.showInputDialog(central.janela, new Object[] {check, msg},
                "Comunicação", javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (msg != null) {
            if (msg.length() == 0)
                msg = padrao;
            else {
                switch (central.aplicacoes.get(origem).grupo.nome) {
                    
                    //difinir verificações de entrada para aplicações
                    case "comprador":
                        if (!msg.matches("[0-9]*")) {
                            javax.swing.JOptionPane.showMessageDialog(central.janela, "Valor inválido!",
                                    "Ops...", javax.swing.JOptionPane.WARNING_MESSAGE);
                            return null;
                        }
                        break;
                        
                }
            }
            if (check.isSelected())
                return "s" + msg;
            return "n" + msg;
        }
        return null;
    }
        
    /** Configuração para o tratamento de uma mensagem recebida.
     * @param central Central
     * @param origem int
     * @param destino int
     * @param dados String
     * @return String */
    public static synchronized String receberMensagem(Central central, int origem, int destino, String dados) {
        int enummsg = Enumeraveis.mensagem_ok.valor;
        String retorno = dados;
        switch (central.aplicacoes.get(origem).grupo.nome) {
            
            //definir ações para aplicações
            case "comprador":
                if (dados.matches("[0-9]*")) { //se dados é valor numérico
                    int origA = 0, origC = 0,
                        destA = 0, destB = 0,
                        valor = Integer.valueOf(dados);
                    try (java.util.Scanner scan = new java.util.Scanner(central.aplicacoes.get(origem).dados)) {
                        if (scan.hasNextInt()) {
                            origA = scan.nextInt();
                            if (scan.hasNextInt()) {
                                scan.nextInt();
                                if (scan.hasNextInt())
                                    origC = scan.nextInt();
                            }
                        }
                    }
                    try (java.util.Scanner scan = new java.util.Scanner(central.aplicacoes.get(destino).dados)) {
                        if (scan.hasNextInt()) {
                            destA = scan.nextInt();
                            if (scan.hasNextInt())
                                destB = scan.nextInt();
                        }
                    }
                    retorno = "compra " + dados;
                    if (central.aplicacoes.get(destino).grupo.nome.equals("intermed")) {
                        enummsg = Enumeraveis.msg_aplicacao.valor;
                        retorno = "autoriza " + dados;
                        if (valor > 0 && valor <= destA && valor <= origA && origC == 0)
                            retorno += " ok";
                        else
                            retorno += " negado";
                    }else if (central.aplicacoes.get(destino).grupo.nome.equals("vendedor")) {
                        enummsg = Enumeraveis.msg_aplicacao.valor;
                        if (valor > 0 && valor <= destB && valor <= origA && valor == origC)
                            retorno += " ok";
                        else
                            retorno += " negado";
                    }
                }
                break;
                
            case "cliente":
                enummsg = Enumeraveis.mensagem_ok.valor;
                int a = 0, b = 0;
                try (java.util.Scanner scan = new java.util.Scanner(central.aplicacoes.get(origem).dados)) {
                    if (scan.hasNextInt()) {
                        a = scan.nextInt();
                        if (scan.hasNextInt())
                            b = scan.nextInt();
                    }
                }
                if (central.aplicacoes.get(destino).grupo.nome.equals("impressora"))
                    if (a != 1)
                        enummsg = Enumeraveis.acesso_nao_permitido.valor;
                if (central.aplicacoes.get(destino).grupo.nome.equals("dados"))
                    if (b != 1)
                        enummsg = Enumeraveis.acesso_nao_permitido.valor;
                break;
        }
        return enummsg + " " + central.aplicacoes.get(origem).toString() + " " + retorno;
    }
    
    /** Trata de um registro de mensagem recebida personalizado.
     * @param central Central
     * @param autor String
     * @param dados String a ser tratada */
    public static synchronized void tratarMensagemApp(Central central, String autor, String dados) {
        
        //<par> compra 100 ok
        String aut, op = "", resp = "";
        int valor = 0, indice = -1;
        try (java.util.Scanner scan = new java.util.Scanner(dados)) {
            aut = scan.next();
            op = scan.next();
            valor = scan.nextInt();
            resp = scan.next();
            for (int i = 0; i < central.aplicacoes.size(); i++)
                if (central.aplicacoes.get(i).toString().equals(aut)) {
                    indice = i;
                    break;
                }
        } catch (Exception e) {}
        if (resp.equals("ok") && indice >= 0) {
            int origA = 0, origB = 0, origC = 0;
            try (java.util.Scanner scan = new java.util.Scanner(central.aplicacoes.get(indice).dados)) {
                origA = scan.nextInt();
                if (scan.hasNextInt()) {
                    origB = scan.nextInt();
                    if (scan.hasNextInt())
                        origC = scan.nextInt();
                }
            } catch (Exception e) {}
            if (op.equals("compra")) {
                int outro = -1;
                for (int i = 0; i < central.aplicacoes.size(); i++)
                    if (central.aplicacoes.get(i).toString().equals(autor)) {
                        outro = i;
                        break;
                    }
                if (outro >= 0) {
                    int destA = 0, destB = 0;
                    try (java.util.Scanner scan = new java.util.Scanner(central.aplicacoes.get(outro).dados)) {
                        destA = scan.nextInt();
                        if (scan.hasNextInt())
                            destB = scan.nextInt();
                    } catch (Exception e) {}
                    destA += valor;
                    destB -= valor;
                    central.aplicacoes.get(outro).dados = destA + " " + destB;
                    origA -= valor;
                    origB += valor;
                    origC -= valor;
                }
            }else if (op.equals("autoriza"))
                origC = valor;
            else
                return;
            central.aplicacoes.get(indice).dados = origA + " " + origB + " " + origC;
        }
    }
}
