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

import blockchain.Bloco;
import blockchain.Cripto;
import blockchain.Registro;
import io.Janela;
import var.*;

/** Controlador com variáveis e recursos das aplicações.
 *
 * @author Luiz Fernando Perez Redondaro */
public final class Central {
    
    /** Grupos de aplicações. */
    public final java.util.ArrayList<Grupo> grupos = PreDefs.grupos();
    
    /** Regras de aplicações. */
    public final java.util.ArrayList<Regra> regras = PreDefs.regras();
    
    /** Blocos do sistema blockchain. */
    public final java.util.ArrayList<Bloco> blocos = new java.util.ArrayList<>();
    
    /** Aplicações em execução. */
    public final java.util.ArrayList<Aplicacao> aplicacoes = PreDefs.aplicacoes();
    
    /** Janela principal, usado para relacionar posição de outras janelas. */
    public final Janela janela;
    
    /** Objeto compartilhado para atualizar a exibição de blocos. */
    private final Janela.JanelaExibir exibeBloco;
    
    
    /** Construtor. Inicia o sistema blockchain e as aplicações.
     * @param janela JFrame Janela
     * @param exibeBloco JanelaExibir */
    public Central(Janela janela, Janela.JanelaExibir exibeBloco) {
        this.janela = janela;
        this.exibeBloco = exibeBloco;
        String chaveAdmin = aplicacoes.get(0).toString(); //chave pública do admin
        Bloco bloco = new Bloco(chaveAdmin, "0");
        blocos.add(bloco);
        for (Grupo g: grupos) {
            Registro reg = new Registro(chaveAdmin, Enumeraveis.grupo_adicionar.valor + " " + g.nome
                    + " " + Integer.toString(g.cor.getRGB()));
            reg.assinatura = aplicacoes.get(0).assinar(Cripto.hash(reg));
            adicionarRegistroConfirmado(reg);
        }
        for (Regra r: regras) {
            Registro reg = new Registro(chaveAdmin, Enumeraveis.regra_adicionar.valor + " " + r.toString());
            reg.assinatura = aplicacoes.get(0).assinar(Cripto.hash(reg));
            adicionarRegistroConfirmado(reg);
        }
        for (Aplicacao a: aplicacoes) {
            Registro reg = new Registro(chaveAdmin, Enumeraveis.par_adicionar.valor + "\n" + a.toString()
                    + "\n" + a.porta + " " + a.grupo.nome + " " + a.dados);
            reg.assinatura = aplicacoes.get(0).assinar(Cripto.hash(reg));
            adicionarRegistroConfirmado(reg);
            a.iniciar(this); //iniciar aplicação/thread
        }
    }
    
    /** Adicionar um registro no sistema. Será verificado e executadas as
     * instruções presentes.
     * @param registro Registro
     * @return boolean se foi confirmado */
    public boolean adicionarRegistro(Registro registro) {
        
        //verificação
        if (!Cripto.verAssinatura(registro.autor, Cripto.hash(registro), registro.assinatura))
            return false;
        
        try (java.util.Scanner scan = new java.util.Scanner(registro.dados)) {
            if (!scan.hasNextInt())
                return false;
            //executar ações do registro, caso existam
            int opcao = scan.nextInt();
            
            if (opcao > 10 && opcao < 14) { //grupos
                String nome = scan.next(), cor = scan.next();
                if (opcao == 11) //inserir
                    grupos.add(new Grupo(nome, new java.awt.Color(Integer.valueOf(cor))));
                else{
                    int encontrado = -1;
                    for (int i = 0; i < grupos.size(); i++)
                        if (grupos.get(i).nome.equals(nome)) {
                            encontrado = i;
                            break;
                        }
                    if (encontrado >= 0)
                        if (opcao == 12) { //alterar
                            grupos.get(encontrado).nome = cor;
                            for (Regra r: regras) {
                                if (r.origem.equals(nome))
                                    r.origem = cor;
                                if (r.destino.equals(nome))
                                    r.destino = cor;
                            }
                            for (Aplicacao a: aplicacoes)
                                if (a.grupo.nome.equals(nome))
                                    a.grupo.nome = cor;
                        }else{ //excluir
                            grupos.remove(encontrado);
                            for (int i = regras.size() - 1; i >= 0; i--)
                                if (regras.get(i).origem.equals(nome) || regras.get(i).destino.equals(nome))
                                    regras.remove(i);
                            for (Aplicacao a: aplicacoes)
                                if (a.grupo.nome.equals(nome)) {
                                    a.grupo.nome = grupos.get(0).nome;
                                    a.grupo.cor = grupos.get(0).cor;
                                }
                        }
                }
                
            }else if (opcao > 20 && opcao < 24) { //regras
                String orig = scan.next(), dest = scan.next();
                Regra atual = new Regra(orig, dest);
                if (opcao == 21) //inserir
                    regras.add(atual);
                else{
                    int encontrado = -1;
                    for (int i = 0; i < regras.size(); i++)
                        if (regras.get(i).igual(atual)) {
                            encontrado = i;
                            break;
                        }
                    if (encontrado >= 0)
                        if (opcao == 22) { //alterar
                            regras.get(encontrado).origem = scan.next();
                            regras.get(encontrado).destino = scan.next();
                        }else{ //excluir
                            regras.remove(encontrado);
                        }
                }
                
            }else if (opcao == 32) { //pares, apenas é permitido alterar
                String s = scan.next();
                Aplicacao a = aplicacoes.get(0); //inicialização inútil, p/ evitar erro
                for (Aplicacao b: aplicacoes)
                    if (b.toString().equals(s)) {
                        a = b;
                        break;
                    }
                scan.next();
                s = scan.next();
                if (!s.equals(a.grupo.nome)) //alterar grupo
                    for (Grupo g: grupos)
                        if (g.nome.equals(s)) {
                            a.grupo = g.clone();
                            break;
                        }
                s = scan.nextLine();
                if (s.length() > 0)
                    s = s.substring(1);
                a.dados = s;
            }else if (opcao == Enumeraveis.msg_aplicacao.valor)
                PreDefs.tratarMensagemApp(this, registro.autor, scan.nextLine().substring(1));
            
            adicionarRegistroConfirmado(registro);
            exibeBloco.atualiza();
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    /** Adicionar registro confirmado no sistema blockchain. As etapas de
     * verificação e execução das instruções já deverão ter sido executadas.
     * @param registro Registro */
    private void adicionarRegistroConfirmado(Registro registro) {
        Bloco atual = blocos.get(blocos.size() - 1);
        
        //se o bloco atual já está completo e minerado
        if (atual.registros.size() == PreDefs.NUM_REGISTRO_BLOCO) {
            atual = new Bloco("", Cripto.hash(atual));
            blocos.add(atual);
        }
        atual.registros.add(registro);
        
        //se completou o bloco, minerar e assinar
        if (atual.registros.size() == PreDefs.NUM_REGISTRO_BLOCO) {
            int posicao = 0;
            for (int i = 0; i < aplicacoes.size(); i++)
                if (aplicacoes.get(i).toString().equals(registro.autor)) {
                    posicao = i;
                    break;
                }
            atual.autor = aplicacoes.get(posicao).toString();
            Cripto.minerar(atual);
            atual.assinatura = aplicacoes.get(posicao).assinar(Cripto.hash(atual));
            if (PreDefs.DEBUG) {
                System.out.println("Minerado bloco " + blocos.size()
                        + "\nhash " + Cripto.hash(atual)
                        + "\nassinatura " + atual.assinatura
                        + "\n--------------------------------------------------"
                        + "--------------------------------------------------");
            }
            
            //=======================================================================
            //=======================================================================
            // aqui seria persistido o bloco em arquivo, mas não será implementado :(
            //=======================================================================
            //=======================================================================
        }
    }
}
