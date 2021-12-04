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

import blockchain.Cripto;
import blockchain.Registro;
import execucao.Central;
import var.Enumeraveis;
import var.Grupo;

/** Configurar grupos.
*
* @author Luiz Fernando Perez Redondaro */
public final class ConfigGrupos extends javax.swing.JDialog {
    
    /** Versão serial. */
    private static final long serialVersionUID = 12L;
    
    /** Tabela de exibição dos grupos. */
    private javax.swing.JTable tabela;
    
    /** Variáveis e recursos do sistema. */
    private final Central central;
    
    /** Construtor.
     * @param central Central */
    public ConfigGrupos(Central central) {
        super(central.janela, true);
        this.central = central;
        ConfigGrupos esse = this; //usado apenas como referência para JOptionPane
        setTitle("Grupos");
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setSize(300,260);
        setResizable(false);
        setLocationRelativeTo(central.janela);
        javax.swing.JPanel contentPane = new javax.swing.JPanel();
        contentPane.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        tabela = new javax.swing.JTable();
        tabela.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {}, new String [] {"Nome", "Cor (RGB)"}
        ){
            private static final long serialVersionUID = 120L;
            @SuppressWarnings("rawtypes")
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { //alterar ou inserir grupo
                int pos = tabela.getSelectedRow();
                javax.swing.JTextField textNome = new javax.swing.JTextField(),
                        textCor = new javax.swing.JTextField();
                textCor.setEditable(false);
                Object msg[] = {"Nome", textNome, "Cor", textCor};
                Object btn[] = {"Salvar", "Excluir"};
                String titulo = "Alterar Grupo";
                if (pos >= 0 && pos < central.grupos.size()) { //alterar
                    textNome.setText(tabela.getValueAt(pos, 0).toString());
                    textCor.setText(tabela.getValueAt(pos, 1).toString());
                    if (pos < 2) { //proteger aplicação excluída e admin
                        textNome.setEditable(false);
                        btn = new Object [] {"Fechar"};
                    }
                }else{ //adicionar
                    pos = -1;
                    textCor.setText(java.awt.Color.GRAY.getRed() + "," + java.awt.Color.GRAY.getGreen()
                            + "," + java.awt.Color.GRAY.getBlue());
                    btn[1] = "Cancelar";
                    titulo = "Novo Grupo";
                }
                Registro reg = null;
                int op = javax.swing.JOptionPane.showOptionDialog(esse, msg, titulo, javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.PLAIN_MESSAGE, null, btn, 0);
                if (op == 0) { //salvar
                    if (nomeGrupo(textNome.getText())) { //verificar nome válido
                        boolean repetido = false;
                        for (int i=0; i < central.grupos.size(); i++) {
                            Grupo g = central.grupos.get(i);
                            if (g.nome.toLowerCase().equals(textNome.getText().toLowerCase())) { //verificar repetido
                                repetido = true;
                                if (i != pos) {
                                    javax.swing.JOptionPane.showMessageDialog(esse, "Nome de grupo repetido!", "Ops...",
                                            javax.swing.JOptionPane.WARNING_MESSAGE);
                                }
                                break;
                            }
                        }
                        if (!repetido) {
                            if (pos < 0) { //adicionar
                                reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.grupo_adicionar.valor + "\n"
                                        + textNome.getText() + " " + Integer.toString(java.awt.Color.GRAY.getRGB()));
                            }else if (pos > 1) //alterar
                                reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.grupo_alterar.valor + "\n"
                                        + central.grupos.get(pos).nome + " " + textNome.getText());
                        }
                    }else{
                        javax.swing.JOptionPane.showMessageDialog(esse, "Nome de grupo inválido!",
                                "Ops...", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }else if (op == 1 && pos > 1 && pos < central.grupos.size()) //excluir
                    if (javax.swing.JOptionPane.showConfirmDialog(esse,
                            "Regras e aplicações com esse grupo também serão excluídas.\n"
                            +"Excluir grupo \"" + tabela.getValueAt(pos, 0).toString() + "\"?",
                            "Excluir", javax.swing.JOptionPane.OK_CANCEL_OPTION) == 0) {
                        reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.grupo_excluir.valor + "\n"
                                + tabela.getValueAt(pos, 0).toString() + " "
                                + Integer.toString(central.grupos.get(pos).cor.getRGB()));
                    }
                if (reg != null) { //adicionar registro, caso alguma ação realizada
                    reg.assinatura = central.aplicacoes.get(0).assinar(Cripto.hash(reg));
                    if (central.adicionarRegistro(reg))
                        atualiza();
                }
            }
        });
        tabela.setFillsViewportHeight(true);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(tabela);
        scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(12, 12, 274, 210);
        contentPane.add(scroll);
        
        //sair ao pressionar ESC
        contentPane.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        		javax.swing.KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
        contentPane.getActionMap().put("closeTheDialog", new javax.swing.AbstractAction() {
            private static final long serialVersionUID = 110L;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	dispose();
            }
        });
        
        atualiza();
    }
	
    /** Atualiza a exibição. Todos os elementos da tabela são apagados e
     * carregados novamente. */
    private void atualiza() {
        javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel)tabela.getModel();
        while (dtm.getRowCount() > 0)
            dtm.removeRow(0);
        for (Grupo g: central.grupos) {
            java.awt.Color c = g.cor;
            dtm.addRow(new Object [] {g.nome, c.getRed()+","+c.getGreen()+","+c.getBlue()});
        }
        dtm.addRow(new Object [] {null, null});
        tabela.setModel(dtm);
    }
    
    /** Verifica se o nome para grupo é válido: formado por pelo menos 2
     * caracteres alfa numéricos e/ou números e/ou '.' e/ou '-'.
     * @param nome String com o tipo de aplicação
     * @return Se o nome do tipo é válido */
    private boolean nomeGrupo(String nome) {
        if (nome == null || nome.length() < 2)
            return false;
        // primeiro caractere deve ser letra
        if (!Character.isLetter(nome.charAt(0)))
            return false;
        // caracteres '.' ou '-' devem separar letras ou números, não usados em sequência
        for (int i = 1; i < nome.length() - 1; i++)
            if (!Character.isLetter(nome.charAt(i)) && !Character.isDigit(nome.charAt(i)))
                if (nome.charAt(i) != '.' || nome.charAt(i) != '-') {
                    if (!Character.isLetter(nome.charAt(i - 1)) && !Character.isDigit(nome.charAt(i - 1)))
                        return false;
                }else
                    return false;
        // último caractere deve ser letra ou número
        return !(!Character.isLetter(nome.charAt(nome.length() - 1))
                && !Character.isDigit(nome.charAt(nome.length() - 1)));
    }
}
