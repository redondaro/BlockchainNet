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
package blockchain;

/** Funções criptográficas para o sistema blockchain.
*
* @author Luiz Fernando Perez Redondaro */
public class Cripto {
    
    /** Dificuldade usada para a mineração. Quanto mais alto, maior a demora. */
    private final static int DIFICULDADE = 3;
    
    /** Obter hash de uma string com o algoritmo sha256.
     * @param entrada String para ser criada a hash
     * @return String - Valor hash */
    public static synchronized String hash(Object entrada) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(entrada.toString().getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder(); //contém valor hash em hexadecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(java.io.UnsupportedEncodingException | java.security.NoSuchAlgorithmException e) {
            return "";
        }
    }
    
    /** Obter Árvore Merkle de um array de registros.
     * @param registros ArrayList de Registro
     * @return String com Árvore Merkle */
    public static synchronized String merkle(java.util.ArrayList<Registro> registros) {
        if (registros == null || registros.isEmpty())
            return "";
        java.util.ArrayList<String> lista = new java.util.ArrayList<>();
        for (Registro reg: registros) //copiar lista de hashs
            lista.add(hash(reg));
        int cont = lista.size();
        java.util.ArrayList<String> auxiliar;
        String txt;
        while (cont > 1) {
            auxiliar = new java.util.ArrayList<>();
            for (int c = 1; c < cont; c += 2) {
                txt = hash(lista.get(c-1) + lista.get(c));
                auxiliar.add(txt);
            }
            if (cont % 2 == 1) {
                txt = hash(lista.get(cont - 1));
                auxiliar.add(txt);
            }
            lista = auxiliar;
            cont = auxiliar.size();
        }
        return lista.get(0);
    }
    
    /** Incrementa o valor nonce até que a hash esteja conforme a dificuldade.
     * @param bloco Bloco
     * @return boolean - Se o bloco foi minerado com sucesso */
    public static synchronized boolean minerar(Bloco bloco) {
        if (bloco == null || bloco.registros == null || bloco.registros.isEmpty())
            return false;
        for (Registro r: bloco.registros)
            if (!verAssinatura(r.autor, hash(r), r.assinatura))
                return false;
        while(!dificuldade(hash(bloco))) //enquanto não atender ao desafio
            bloco.nonce++;
        return true;
    }
    
    /** Verifica se a assinatura é válida.
     * @param chave String chave pública do autor
     * @param hash String de hash a ser comparada com o valor obtido pela chave pública
     * @param assinatura Assinatura que será descriptografada com a chave pública
     * @return boolean - Se a assinatura é válida */
    public static synchronized boolean verAssinatura(String chave, String hash, String assinatura) {
        try {
            //converter String da chave pública no objeto PublicKey
            byte[] publicBytes = org.bouncycastle.util.encoders.Base64.decode(chave);
            java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(publicBytes);
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("ECDSA","BC");
            java.security.PublicKey chaveAutor = keyFactory.generatePublic(keySpec);
            //converter String da assinatura em em array de bytes (byte[])
            java.util.ArrayList<Integer> lista = new java.util.ArrayList<>();
            for (int i = 0; i < assinatura.length(); i += 2) {
                String num = "" + assinatura.charAt(i) + assinatura.charAt(i+1);
                lista.add(Integer.valueOf(num, 16) - 128);
            }
            byte[] assinaturaByte = new byte[lista.size()];
            for (int i = 0; i < lista.size(); i++)
                assinaturaByte[i] = lista.get(i).byteValue();
            //verificar assinatura
            java.security.Signature ecdsaVerify = java.security.Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(chaveAutor);
            ecdsaVerify.update(hash.getBytes());
            return ecdsaVerify.verify(assinaturaByte);
        } catch(NumberFormatException | java.security.InvalidKeyException
                |java.security.NoSuchAlgorithmException | java.security.NoSuchProviderException
                | java.security.SignatureException | java.security.spec.InvalidKeySpecException e) {
            return false;
        }
    }
    
    /** Verifica se um bloco é válido.
     * @param bloco Bloco
     * @return boolean - Se o bloco está correto */
    public static synchronized boolean verBloco(Bloco bloco) {
        if (bloco == null || bloco.registros == null || bloco.registros.isEmpty() || !dificuldade(hash(bloco)))
            return false;
        for (Registro r: bloco.registros)
            if (!verAssinatura(r.autor, hash(r), r.assinatura))
                return false;
        return verAssinatura(bloco.autor, hash(bloco), bloco.assinatura);
    }
    
    /** Obter descrição de um long timestamp em DD/MM/AAAA-HH:MM:SS.
     * @param timestamp long
     * @return String descrição */
    public static synchronized String dataHora(long timestamp) {
    	String t = new java.sql.Timestamp(timestamp).toString();
        return t.substring(8, 10) + "/" + t.substring(5, 7) + "/" +
                t.substring(0, 4) + "-" + t.substring(11, 13) + ":" +
                t.substring(14, 16) + ":" + t.substring(17, 19);
    }
    
    
    //==========================================================================
    //======== RECURSOS OCULTOS, USADAS PELO SISTEMA CRIPTOGRÁFICO =============
    
    /** *  Inicializa o provedor de segurança para o sistema blockchain.Execução
     * obrigatória! */
    public static synchronized void iniciar() {
        if (java.security.Security.getProvider("BC") == null)
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    /** Constante usada para verAssinatura o desafio, na mineração de blocos. */
    private final static String ALVO = new String(new char[DIFICULDADE]).replace('\0', '0');
    
    /** Verifica a String de dificuldade de um bloco, se atende ao desafio.
     * @param hash String
     * @return boolean, se atende ao desafio */
    private static synchronized boolean dificuldade(String hash) {
        return (hash.substring(0, DIFICULDADE).equals(ALVO));
    }
    
    
    //==========================================================================
    //=========================== FUNÇÃO DRIVER ================================
    
    /** Função driver, de teste para exemplo de uso da estrutura do blockchain.
     * @param args argumentos não usados */
    public static void main(String[] args) {
        iniciar(); //nunca esquecer de executar esse método!!!
        Usuario usuario = new Usuario();
        Bloco bloco = new Bloco(usuario.toString(), "0");
        
        Registro reg = new Registro(usuario.toString(), "12345 teste 1");
        reg.assinatura = usuario.assinar(hash(reg));
        System.out.println("REGISTRO 1\n" + reg.toString());
        System.out.println("Verificação: " + verAssinatura(reg.autor, hash(reg), reg.assinatura) + "\n");
        bloco.registros.add(reg);
        
        reg = new Registro(usuario.toString(), "12345 mensagem");
        reg.assinatura = usuario.assinar(hash(reg));
        System.out.println("REGISTRO 2\n" + reg.toString());
        System.out.println("Verificação: " + verAssinatura(reg.autor, hash(reg), reg.assinatura) + "\n");
        bloco.registros.add(reg);
        
        minerar(bloco);
        bloco.assinatura = usuario.assinar(hash(bloco));
        System.out.println("BLOCO\n" + bloco.toString());
        System.out.println("Verificação: " + verAssinatura(bloco.autor, hash(bloco), bloco.assinatura));
    }
}
