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
import var.Regra;

/** Configurar regras.
*
* @author Luiz Fernando Perez Redondaro */
public final class ConfigRegras extends javax.swing.JDialog {
    
    /** Versão serial. */
    private static final long serialVersionUID = 13L;
    
    /** Tabela de exibição das regras. */
    private final javax.swing.JTable tabela;
    
    /** Variáveis e recursos do sistema. */
    private final Central central;
    
    /** Construtor.
     * @param central Central */
    public ConfigRegras(Central central) {
        super(central.janela, true);
        this.central = central;
        ConfigRegras esse = this; //usado apenas como referência para JOptionPane
        setTitle("Regras");
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
                new Object [][] {}, new String [] {"Origem", "Destino"}
        ){
            private static final long serialVersionUID = 130L;
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
            @SuppressWarnings("unchecked")
			@Override
            public void mouseClicked(java.awt.event.MouseEvent e) { //alterar ou inserir regra
                int pos = tabela.getSelectedRow();
                @SuppressWarnings("rawtypes")
				javax.swing.JComboBox comboOrig = new javax.swing.JComboBox(),
                        comboDest = new javax.swing.JComboBox();
                for (int i = 2; i < central.grupos.size(); i++) { //oculta excluído e admin
                    comboOrig.addItem(central.grupos.get(i).nome);
                    comboDest.addItem(central.grupos.get(i).nome);
                }
                Object msg[] = {"Origem", comboOrig, "Destino", comboDest};
                Object btn[] = {"Salvar", "Excluir"};
                String titulo = "Alterar Regra";
                
                if (pos >= 0 && pos < central.regras.size()) { //alterar
                    Regra r = central.regras.get(pos);
                    for (int i = 2; i < central.grupos.size(); i++) { //oculta excluído e admin
                        if (r.origem.toLowerCase().equals(central.grupos.get(i).nome.toLowerCase()))
                            comboOrig.setSelectedIndex(i-2);
                        if (r.destino.toLowerCase().equals(central.grupos.get(i).nome.toLowerCase()))
                            comboDest.setSelectedIndex(i-2);
                    }
                }else{ //adicionar
                    pos = -1;
                    btn[1] = "Cancelar";
                    titulo = "Nova Regra";
                }
                Registro reg = null;
                int op = javax.swing.JOptionPane.showOptionDialog(esse, msg, titulo, javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.PLAIN_MESSAGE, null, btn, 0);
                if (op == 0) { //salvar
                    boolean repetido = false;
                    Regra r = new Regra(comboOrig.getSelectedItem().toString(), comboDest.getSelectedItem().toString());
                    for (int i = 0; i < central.regras.size(); i++)
                        if (central.regras.get(i).igual(r)) {
                            repetido = true;
                            if (i != pos) { //adicionar repetido, caso contrário não estará alterando
                                javax.swing.JOptionPane.showMessageDialog(esse, "Regra repetida!", "Ops...",
                                        javax.swing.JOptionPane.WARNING_MESSAGE);
                            }
                            break;
                        }
                    if (!repetido) {
                        if (pos < 0) { //adicionar
                            reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.regra_adicionar.valor + "\n"
                                    + comboOrig.getSelectedItem().toString() + " " + comboDest.getSelectedItem().toString());
                        }else{ //alterar
                            reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.regra_alterar.valor + "\n"
                                    + tabela.getValueAt(pos, 0).toString() + " " + tabela.getValueAt(pos, 1).toString() + " "
                                    + comboOrig.getSelectedItem().toString() + " " + comboDest.getSelectedItem().toString());
                        }
                    }
                }else if (op == 1 && pos >= 0) { //excluir
                    if (javax.swing.JOptionPane.showConfirmDialog(esse,
                            "Excluir regra \"" + tabela.getValueAt(pos, 0).toString() + "->" +
                                    tabela.getValueAt(pos, 1).toString() + "\"?", "Excluir",
                                    javax.swing.JOptionPane.OK_CANCEL_OPTION) == 0) {
                        reg = new Registro(central.aplicacoes.get(0).toString(), Enumeraveis.regra_excluir.valor + "\n"
                                + tabela.getValueAt(pos, 0).toString() + " " + tabela.getValueAt(pos, 1).toString());
                    }
                }
                if (reg != null) {
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
        for (Regra r: central.regras)
            dtm.addRow(new Object [] {r.origem, r.destino});
        dtm.addRow(new Object [] {null, null});
        tabela.setModel(dtm);
    }
}
