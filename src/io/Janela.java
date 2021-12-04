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

import execucao.Aplicacao;
import execucao.Central;
import var.PreDefs;

/** Janela de interface e execução do sistema.
 *
 * @author Luiz Fernando Perez Redondaro */
public final class Janela extends javax.swing.JFrame {

    /** Versão serial. */
    private static final long serialVersionUID = 14L;
    
    /** Variáveis e recursos do sistema */
    private final Central central;
    
    /** Variáveis para a seleção de pares na tela. */
    private int selecA = -1, selecB = -1;
    /** Painel de exibição gráfica. */
    private final PainelExibe tela = new PainelExibe();
    
    /** Posição de bloco a ser exibido. */
    private int posicao = -1;
    /** Botões de navegação de blocos. */
    private final javax.swing.JButton btnAnterior = new javax.swing.JButton("Anterior"),
                                      btnProximo = new javax.swing.JButton("Próximo");
    
    /** Painel para saída de texto. */
    private final javax.swing.JTextPane painelSaida = new javax.swing.JTextPane();
    /** Exibir mensagem na barra. */
    private final javax.swing.JLabel labelInferior = new javax.swing.JLabel(" ");
    
    /** Menu popup e seus itens para exibição no sistema. */
    private final javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
    /** Item do menu popup. */
    private final javax.swing.JMenuItem popupTitulo = new javax.swing.JMenuItem("");
    /** Item do menu popup. */
    private final javax.swing.JMenuItem popupConfigurar = new javax.swing.JMenuItem("Configurar");
    
    //================================================================================
    //  Inicialização gráfica e funcionalidades diversas para o sistema.
    //================================================================================

    /** Construtor. */
    public Janela() {
        central = new Central(this, new JanelaExibir());
        setTitle("Autenticação Entre Aplicações Distribuídas com Blockchain");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        
        popupTitulo.setEnabled(false);
        popup.add(popupTitulo);
        javax.swing.JPopupMenu.Separator separadorPop = new javax.swing.JPopupMenu.Separator();
        popup.add(separadorPop);
        
        popupConfigurar.addActionListener((java.awt.event.ActionEvent e) -> { //configurar aplicação
            JanelaRecursos.configurarApp(central, selecB);
        });
        popup.add(popupConfigurar);
        
        javax.swing.JScrollPane scrollTela = new javax.swing.JScrollPane();
        scrollTela.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollTela.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollTelaMouseClicked(evt);
            }
        });
        tela.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED, null, null));
        scrollTela.setViewportView(tela);
        
        javax.swing.JScrollPane scrollSaida = new javax.swing.JScrollPane();
        scrollSaida.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollSaida.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        painelSaida.setEditable(false);
        scrollSaida.setViewportView(painelSaida);
        
        javax.swing.JToolBar barraInferior = new javax.swing.JToolBar();
        barraInferior.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        barraInferior.setFloatable(false);
        barraInferior.setRollover(true);
        barraInferior.add(labelInferior);
        
        javax.swing.JMenuBar barraMenu = new javax.swing.JMenuBar();
        javax.swing.JMenu menuArquivo = new javax.swing.JMenu();
        menuArquivo.setText("Arquivo");
        menuArquivo.setMnemonic('a');
        
        javax.swing.JMenuItem menuGrupos = new javax.swing.JMenuItem();
        menuGrupos.setText("Grupos");
        menuGrupos.setMnemonic('g');
        menuGrupos.addActionListener((java.awt.event.ActionEvent e) -> { //configurar grupos
            ConfigGrupos confGrupos = new ConfigGrupos(central);
            confGrupos.setVisible(true);
        });
        menuArquivo.add(menuGrupos);
        javax.swing.JMenuItem menuRegras = new javax.swing.JMenuItem();
        menuRegras.setText("Regras");
        menuRegras.setMnemonic('r');
        menuRegras.addActionListener((java.awt.event.ActionEvent e) -> { //configurar regras
            ConfigRegras confRegras = new ConfigRegras(central);
            confRegras.setVisible(true);
        });
        menuArquivo.add(menuRegras);
        
        javax.swing.JPopupMenu.Separator separador1 = new javax.swing.JPopupMenu.Separator();
        menuArquivo.add(separador1);
        javax.swing.JMenuItem menuSair = new javax.swing.JMenuItem();
        menuSair.setText("Sair");
        menuSair.setMnemonic('s');
        menuSair.addActionListener((java.awt.event.ActionEvent e) -> {
            System.exit(0);
        });
        menuArquivo.add(menuSair);
        barraMenu.add(menuArquivo);
        
        javax.swing.JMenu menuAjuda = new javax.swing.JMenu();
        menuAjuda.setText("Ajuda");
        menuAjuda.setMnemonic('u');
        javax.swing.JMenuItem menuSobre = new javax.swing.JMenuItem();
        menuSobre.setText("Sobre");
        menuSobre.setMnemonic('o');
        menuSobre.addActionListener((java.awt.event.ActionEvent e) -> { //exibir créditos do sistema
            JanelaRecursos.sobre(this);
        });
        menuAjuda.add(menuSobre);
        barraMenu.add(menuAjuda);
        setJMenuBar(barraMenu);
        
        btnAnterior.addActionListener((java.awt.event.ActionEvent e) -> {
            exibirBlocos(posicao-1);
            painelSaida.setCaretPosition(0);
        });
        btnAnterior.setFocusable(false);
        
        btnProximo.addActionListener((java.awt.event.ActionEvent e) -> {
            exibirBlocos(posicao+1);
            painelSaida.setCaretPosition(0);
        });
        btnProximo.setFocusable(false);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
        				.addComponent(scrollTela, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        				.addComponent(barraInferior, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
        			.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
        				.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
        					.addComponent(btnAnterior)
        					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
        					.addComponent(btnProximo)
        					.addContainerGap())
        				.addComponent(scrollSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        				.addComponent(scrollSaida, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
        				.addComponent(scrollTela, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
        			.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        				.addComponent(barraInferior, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        					.addComponent(btnAnterior)
        					.addComponent(btnProximo))))
        );
        getContentPane().setLayout(layout);
        
        pack();
        setLocationRelativeTo(null);
        exibirBlocos(-1);
        painelSaida.setCaretPosition(0);
    }
    
    /** Clique do mouse no painel de exibição.
     * @param evt MouseEvent */
    private void scrollTelaMouseClicked(java.awt.event.MouseEvent evt) {
        int encontrado = -1;
        for (int i = 0; i < central.aplicacoes.size(); i++) { //verificar se alguma aplicação recebeu clique do mouse
            Aplicacao a = central.aplicacoes.get(i);
            if (evt.getX() > a.x - 24 && evt.getX() < a.x + 24 && evt.getY() > a.y - 24 && evt.getY() < a.y + 24) {
                encontrado = i;
                break;
            }
        }
        if (PreDefs.DEBUG && encontrado >= 0) { //imprimir dados da aplicação selecionada
            System.out.println("Aplicação: " + central.aplicacoes.get(encontrado).toString()
                    + "\nPorta: " + central.aplicacoes.get(encontrado).porta
                    + "  Grupo: " + central.aplicacoes.get(encontrado).grupo.nome
                    + "  X: " + central.aplicacoes.get(encontrado).x
                    + "  Y: " + central.aplicacoes.get(encontrado).y
                    + "  Dados: " + central.aplicacoes.get(encontrado).dados
                    + "\n--------------------------------------------------"
                    + "--------------------------------------------------"
            );
        }
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) { //botão esquerdo do mouse
            if (encontrado < 1) { //administrador ou não encontrado
                selecA = selecB = -1;
                labelInferior.setText(" ");
            }else
                if (selecA < 0) {
                    selecA = encontrado;
                    selecB = -1;
                    labelInferior.setText(" Comunicação de \"" + central.aplicacoes.get(selecA).porta
                            +" "+central.aplicacoes.get(selecA).grupo.nome + "\" para...");
                }else
                    if (selecB < 0) {
                        if (selecA != encontrado) { //envio de mensagem
                            
                            //aplicação destinatária excluída, cancela envio
                            if (central.aplicacoes.get(encontrado).grupo.nome.equals(central.grupos.get(0).nome)) {
                                selecA = selecB = -1;
                                labelInferior.setText(" ");
                            }else{
                                selecB = encontrado;
                                if (central.aplicacoes.get(selecA).enviarMensagem(selecB)) {
                                    labelInferior.setText(" Comunicação de \"" +
                                            central.aplicacoes.get(selecA).porta + " " +
                                            central.aplicacoes.get(selecA).grupo.nome + "\" para \"" +
                                            central.aplicacoes.get(selecB).porta + " " +
                                            central.aplicacoes.get(selecB).grupo.nome + "\"");
                                }else{
                                    selecA = selecB = -1;
                                    labelInferior.setText(" ");
                                }
                            }
                        }else{
                            selecA = selecB = -1;
                            labelInferior.setText(" ");
                        }
                    }else{
                        selecA = encontrado;
                        selecB = -1;
                        labelInferior.setText(" Comunicação de \"" + central.aplicacoes.get(selecA).porta
                                +" "+central.aplicacoes.get(selecA).grupo.nome + "\" para...");
                    }
            tela.atualiza();
            
        }else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) { //botão direito do mouse
            selecA = -1;
            labelInferior.setText(" ");
            tela.atualiza();
            if (encontrado >= 0) {
                selecB = encontrado;
                String s = central.aplicacoes.get(selecB).toString();
                popupTitulo.setText("..."+s.substring(s.length()-6)
                        +"  "+central.aplicacoes.get(selecB).porta
                        +"  "+central.aplicacoes.get(selecB).grupo.nome);
                popupConfigurar.setEnabled(encontrado != 0); //não permite configurar admin
                popup.show(this, evt.getX(), evt.getY()+60);
            }else
                selecB = -1;
        }
    }
    
    /** Atualiza a exibição na posição determinada. Caso a posição seja inválida,
     * será exibido o último bloco.
     * @param posicao int */
    private void exibirBlocos(int posicao) {
        if (central.blocos == null)
            return;
        if (posicao < 0 || posicao >= central.blocos.size())
            posicao = central.blocos.size() -1;
        this.posicao = posicao;
        if (posicao > 0)
            btnAnterior.setEnabled(true);
        else
            btnAnterior.setEnabled(false);
        if (posicao < central.blocos.size() -1)
            btnProximo.setEnabled(true);
        else
            btnProximo.setEnabled(false);
        if (posicao >= 0)
            painelSaida.setText(JanelaRecursos.stringBloco(central, posicao));
    }
    
    /** Objeto compartilhado para possibilitar a atualização da exibição por outra classe. */
    public class JanelaExibir {
        /** Contrutor. */
        public JanelaExibir() {}
        /** Atualizar exibição. */
        public void atualiza() {
            exibirBlocos(-1);
            painelSaida.setCaretPosition(painelSaida.getText().length()-1);
            tela.atualiza();
        }
    }
    
    /** Função driver para execução e testes.
     * @param args String[] */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            Janela frame = new Janela();
            frame.setVisible(true);
        });
    }
    
    
    //================================================================================
    //  Painel de exibição gráfica do sistema
    //================================================================================
    
    /** Painel de exibição gráfica do sistema. Adaptado de um trabalho acadêmico
     * do professor doutor Danilo Medeiros Eler. */
    private final class PainelExibe extends javax.swing.JPanel {

        /** Versão serial. */
        private static final long serialVersionUID = 02L;

        /** Imagens a serem exibidas. */
        private java.awt.image.BufferedImage imageBuffer;

        /** Contrutor. */
        public PainelExibe() {
            //this.setBackground(java.awt.Color.WHITE);
            this.setBackground(java.awt.Color.LIGHT_GRAY);
            this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        }

        /** Exibição gráfica.
         * @param g Graphics - interface gráfica de exibição */
        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

            if (central.aplicacoes != null && this.imageBuffer == null) {
                this.imageBuffer = new java.awt.image.BufferedImage(600, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2Buffer = this.imageBuffer.createGraphics();
                g2Buffer.setColor(this.getBackground());
                g2Buffer.fillRect(0, 0, 601, 601);
                g2Buffer.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (selecA >= 0 && selecB >= 0) {
                    g2Buffer.setStroke(new java.awt.BasicStroke(3.0f));
                    g2Buffer.setColor(java.awt.Color.RED);
                    g2Buffer.drawLine(central.aplicacoes.get(selecA).x, central.aplicacoes.get(selecA).y,
                            central.aplicacoes.get(selecB).x, central.aplicacoes.get(selecB).y);
                    g2Buffer.setStroke(new java.awt.BasicStroke(1.0f));
                }
                for (Aplicacao m: central.aplicacoes) {
                    if (m.grupo != null)
                        JanelaRecursos.desenhaIcone(g2Buffer, m.x, m.y, m.grupo.cor);
                    else
                        JanelaRecursos.desenhaIcone(g2Buffer, m.x, m.y, java.awt.Color.black);
                    g2Buffer.setColor(java.awt.Color.DARK_GRAY);
                    g2Buffer.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
                    String auxiliar = m.toString();
                    g2Buffer.drawString("ChPub: ..." + auxiliar.substring(auxiliar.length()-6), m.x-28, m.y+32);
                    g2Buffer.drawString("Porta: " + m.porta, m.x - 28, m.y+50);
                    g2Buffer.drawString("Grupo: " + m.grupo.nome, m.x - 28, m.y+41);
                    if (m.dados.length() > 0)
                        g2Buffer.drawString("Dados: " + m.dados, m.x-28, m.y+59);
                }
                if (selecA >= 0) {
                    g2Buffer.setStroke(new java.awt.BasicStroke(3.0f));
                    g2Buffer.setColor(java.awt.Color.RED);
                    g2Buffer.drawRect(central.aplicacoes.get(selecA).x-22,
                            central.aplicacoes.get(selecA).y-21, 42, 42);
                    if (selecB >= 0)
                        g2Buffer.drawRect(central.aplicacoes.get(selecB).x-22,
                                central.aplicacoes.get(selecB).y-21, 42, 42);
                    g2Buffer.setStroke(new java.awt.BasicStroke(1.0f));
                }
                PreDefs.legendas(g2Buffer, central.grupos, central.regras);
                g2Buffer.dispose();
            }
            if (this.imageBuffer != null)
                g2.drawImage(this.imageBuffer, 0, 0, null);
        }

        /** Atualiza exibição gráfica. */
        public void atualiza() {
            this.setPreferredSize(new java.awt.Dimension(600, 600));
            this.setSize(new java.awt.Dimension(600, 600));
            this.imageBuffer = null;
            this.repaint();
        }
    }
}
