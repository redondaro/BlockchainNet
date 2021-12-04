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
package io;

import blockchain.Bloco;
import blockchain.Cripto;
import blockchain.Registro;
import execucao.Aplicacao;
import execucao.Central;
import var.Enumeraveis;
import var.PreDefs;

/** Recursos para a Janela de exibição.
 *
 * @author Luiz Fernando Perez Redondaro */
public class JanelaRecursos {
    
    /** Configurar aplicação.
     * @param central Central
     * @param posicao int posição da aplicação no ArrayList de aplicações */
    @SuppressWarnings("unchecked")
	public static synchronized void configurarApp(Central central, int posicao) {
        Aplicacao a = central.aplicacoes.get(posicao); //aplicação selecionada

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(); //painel com informações da aplicação
        scrollPane.setBounds(12, 12, 274, 47);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        javax.swing.JTextPane textPane = new javax.swing.JTextPane();
        textPane.setEditable(false);
        String s = a.toString();
        textPane.setText("Chave pública: ..." + s.substring(s.length()-6)
                + "\nPorta: " + a.porta);
        scrollPane.setViewportView(textPane);

        @SuppressWarnings("rawtypes")
		javax.swing.JComboBox comboBox = new javax.swing.JComboBox(); //combobox grupo da aplicação
        comboBox.setBounds(12, 90, 110, 24);
        int selecionado = 0;
        for (int i=0; i < central.grupos.size(); i++) {
            comboBox.addItem(central.grupos.get(i).nome);
            if (central.grupos.get(i).nome.equals(a.grupo.nome)) //selecionar grupo da aplicação
                selecionado = i;
        }
        comboBox.setSelectedIndex(selecionado);
        javax.swing.JTextField textField = new javax.swing.JTextField(a.dados); //textField com os dados

        Object msg[] = {scrollPane, "Grupo", comboBox,
                "Dados (0-3 inteiros)", textField}; //criar JOptionPane com opções
        Object[] btn = {"Salvar"};
        int result = javax.swing.JOptionPane.showOptionDialog(central.janela, msg, "Aplicação",
                javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE, null, btn, 2);
        if (result == 0) { //aplicar alterações
            String g = a.grupo.nome, d = a.dados;
            boolean alterou = false;
            if (comboBox.getSelectedIndex() != selecionado) { //alterar grupo
                g = central.grupos.get(comboBox.getSelectedIndex()).nome;
                if (g.equals("admin")) {
                    javax.swing.JOptionPane.showMessageDialog(central.janela, "Não é permitido definir outro admin!", "Ops...",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }else
                    alterou = true;
            }
            if (!textField.getText().equals(a.dados)) { //alterar dados
                if (textField.getText().length() > 0) {
                    java.util.Scanner scan = new java.util.Scanner(textField.getText());
                    int qt = 0;
                    while (scan.hasNextInt() && qt < 3) {
                        scan.nextInt();
                        qt++;
                    }
                    if (scan.hasNext()) {
                        javax.swing.JOptionPane.showMessageDialog(central.janela, "Entrada de dados inválida!", "Ops...",
                                javax.swing.JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                d = textField.getText();
                alterou = true;
            }
            if (alterou) {
                Registro reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.par_alterar.valor +
                        "\n" + a.toString() + "\n" + a.porta + " " + g + " " + d);
                reg.assinatura = central.aplicacoes.get(0).assinar(Cripto.hash(reg));
                central.adicionarRegistro(reg);
            }
        }
    }
    
    /** Obter descrição do Bloco em uma String.
     * @param central Central
     * @param posicao int posição do bloco no ArrayList
     * @return String descrição do bloco */
    public static synchronized String stringBloco(Central central, int posicao) {
        if (posicao < 0 || posicao >= central.blocos.size())
            return "";
        Bloco b = central.blocos.get(posicao);
        String hashant = "...000000";
        if (b.hashAnterior.length() > 7)
            hashant = "..." + b.hashAnterior.substring(b.hashAnterior.length()-6);
        String retorno = "CACHE\nHash anterior " + hashant + "\n\n";
        if (b.registros.size() == PreDefs.NUM_REGISTRO_BLOCO) { //se bloco minerado
            String autor = "", hash = Cripto.hash(b);
            if (b.autor.length() > 7)
                autor = "..." + b.autor.substring(b.autor.length()-6);
            retorno = "BLOCO " + (posicao + 1) + "   Autor " + autor +
                    "   Nonce " + Long.toString(b.nonce) +
                    "\nHash anterior " + hashant +
                    "   Hash ..." + hash.substring(hash.length()-6) +
                    "\nTimestamp " + Cripto.dataHora(b.timestamp) +
                    "\nAssinatura ..." + b.assinatura.substring(b.assinatura.length()-20)
                    ;
        }
        retorno += "\n=========================";

        //exibir registros
        for (int i=0; i < b.registros.size(); i++) {
            Registro r = b.registros.get(i);

            //tratar exibição de dados
            String dados = "", auxiliar;
            try (java.util.Scanner scan = new java.util.Scanner(r.dados)) {
                int op = scan.nextInt();
                dados += Enumeraveis.descricao(op) + " ";
                switch (op) {
                    case 1: case 2: case 3: case 4: case 5:
                        auxiliar = scan.next();
                        dados += "Par:..." + auxiliar.substring(auxiliar.length()-6);
                        if (scan.hasNextLine())
                            dados += "\nMsg: " + scan.nextLine().substring(1);
                        break;

                    //excluir: confirmar -> excluir -> excluir regras com o grupo
                    case 11: case 12: case 13: //grupos
                        dados += scan.next();
                        if (op == 12)
                            dados += " " + scan.next();
                        else{
                            java.awt.Color c = new java.awt.Color(scan.nextInt());
                            dados += " Cor:" + c.getRed() + "," + c.getGreen() + "," + c.getBlue();
                        }
                        break;
                    case 21: case 22: case 23: //regras
                        dados += scan.next() + " -> " + scan.next();
                        if (op == 22) //alterar
                            dados += "\n" + scan.next() + " -> " + scan.next();
                        break;
                    case 31: case 32: case 33://clientes
                        auxiliar = scan.next();
                        dados += "Ch:..." + auxiliar.substring(auxiliar.length()-6);
                        dados += "\nP:" + scan.next() + " G:";
                        dados += scan.next();
                        dados += " D:" + scan.nextLine();
                        break;
                    default:
                        auxiliar = scan.next();
                        dados += "Par:..." + auxiliar.substring(auxiliar.length()-6);
                    	if (scan.hasNextLine()) {
                            auxiliar = scan.nextLine();
                            dados += "\n" + auxiliar.substring(1);
                        }
                        while (scan.hasNextLine())
                            dados += "\n" + scan.nextLine();
                }
            }

            retorno += "\nREGISTRO " + (i+1) + "    Autor ..." + r.autor.substring(r.autor.length()-6)
                    + "\nTimestamp " + Cripto.dataHora(r.timestamp)
                    + "\nAssinatura ..." + r.assinatura.substring(r.assinatura.length()-20)
                    + "\n" + dados
                    + "\n------------------------------------------------------------";
        }
        return retorno;
    }
    
    /** Desenha o ícone de um computador no painel Graphics2D.
     * @param g Graphics2D painel
     * @param x int posição X
     * @param y int posição Y
     * @param cor Color
     */
    public static synchronized void desenhaIcone(java.awt.Graphics2D g, int x, int y, java.awt.Color cor) {
        if (g == null)
            return;
        g.setStroke(new java.awt.BasicStroke(1.0f));

        //cor do PC
        g.setColor(java.awt.Color.GRAY);
        g.drawRect(x - 19, y - 20, 38, 24);
        g.drawRect(x - 18, y - 19, 36, 22);
        g.drawLine(x - 18, y + 6, x + 18, y + 6);
        g.drawLine(x - 19, y + 8, x + 19, y + 8);
        g.drawLine(x - 20, y + 9, x - 20, y + 10);
        g.drawLine(x + 20, y + 9, x + 20, y + 10);
        g.drawLine(x - 18, y + 10, x + 18, y + 10);
        g.drawLine(x - 21, y + 11, x - 21, y + 12);
        g.drawLine(x + 21, y + 11, x + 21, y + 12);
        g.drawLine(x - 19, y + 11, x + 19, y + 11);
        g.drawRect(x - 23, y + 13, 46, 6);
        g.drawRect(x - 22, y + 14, 44, 4);
        g.drawRect(x - 21, y + 15, 42, 2);
        g.drawLine(x - 20, y + 16, x + 20, y + 16);

        //contornos
        g.setColor(java.awt.Color.DARK_GRAY);
        g.drawRect(x - 20, y - 21, 40, 26);
        g.drawLine(x - 17, y - 19, x + 17, y - 19);
        g.drawLine(x - 17, y + 3, x + 17, y + 3);
        g.drawLine(x - 18, y - 18, x - 18, y + 2);
        g.drawLine(x + 18, y - 18, x + 18, y + 2);
        g.drawLine(x - 19, y + 5, x - 19, y + 6);
        g.drawLine(x + 19, y + 5, x + 19, y + 6);
        g.drawLine(x - 20, y + 7, x + 20, y + 7);
        g.drawLine(x - 20, y + 7, x - 20, y + 8);
        g.drawLine(x + 20, y + 7, x + 20, y + 8);
        g.drawLine(x - 21, y + 9, x - 21, y + 10);
        g.drawLine(x + 21, y + 9, x + 21, y + 10);
        g.drawLine(x - 19, y + 9, x - 19, y + 10);
        g.drawLine(x + 19, y + 9, x + 19, y + 10);
        g.drawLine(x - 19, y + 9, x + 19, y + 9);
        g.drawLine(x - 22, y + 11, x - 22, y + 12);
        g.drawLine(x + 22, y + 11, x + 22, y + 12);
        g.drawLine(x - 20, y + 11, x - 20, y + 12);
        g.drawLine(x + 20, y + 11, x + 20, y + 12);
        g.drawLine(x - 19, y + 12, x + 19, y + 12);
        g.drawLine(x - 23, y + 13, x - 23, y + 14);
        g.drawLine(x + 23, y + 13, x + 23, y + 14);
        g.drawLine(x - 6, y + 14, x + 6, y + 14);
        g.drawLine(x - 7, y + 15, x - 7, y + 16);
        g.drawLine(x + 7, y + 15, x + 7, y + 16);
        g.drawLine(x - 24, y + 15, x - 24, y + 20);
        g.drawLine(x + 24, y + 15, x + 24, y + 20);
        g.drawLine(x - 24, y + 17, x + 24, y + 17);
        g.drawLine(x - 24, y + 20, x + 24, y + 20);

        //cor na tela do PC
        if (cor != null)
            g.setColor(cor);
        else
            g.setColor(java.awt.Color.black);
        g.drawRect(x - 17, y - 18, 34, 20);
        g.drawRect(x - 16, y - 17, 32, 18);
        g.drawRect(x - 15, y - 16, 30, 16);
        g.drawRect(x - 14, y - 15, 28, 14);
        g.drawRect(x - 13, y - 14, 26, 12);
        g.drawRect(x - 12, y - 13, 24, 10);
        g.drawRect(x - 11, y - 12, 22, 8);
        g.drawRect(x - 10, y - 11, 20, 6);
        g.drawRect(x - 9, y - 10, 18, 4);
        g.drawRect(x - 8, y - 9, 16, 2);
        g.drawLine(x - 7, y - 8, x + 14, y - 8);
    }
    
    /** Créditos do sistema.
     * @param esse JFrame da Janela como referencial de posição para outras janelas */
    public static synchronized void sobre(javax.swing.JFrame esse) {
        javax.swing.JOptionPane.showMessageDialog(esse,
                "UNIVERSIDADE ESTADUAL PAULISTA \"JÚLIO DE MESQUITA FILHO\""
                +"\nCâmpus de Presidente Prudente" +"\n\nTrabalho de Conclusão de Curso"
                +"\nAutenticação entre Aplicações Distribuídas com Blockchain"
                +"\n\nAluno: Luiz Fernando Perez Redondaro"
                +"\nOrientador: Prof. Dr. Milton Hirokazu Shimabukuro","Sobre",
                javax.swing.JOptionPane.PLAIN_MESSAGE);
    }
}
