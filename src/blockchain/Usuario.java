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

/** Entidade de um participante do sistema blockchain. Possui o par de chaves
 * público/privado, assinando registros e blocos com sua chave privada.
 *
 * @author Luiz Fernando Perez Redondaro */
public class Usuario {
    
    /** Método contrutor: apenas cria o par de chaves pública/privada. */
    public Usuario () {
        try {
            java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("ECDSA","BC");
            java.security.SecureRandom random = java.security.SecureRandom.getInstance("SHA1PRNG");
            java.security.spec.ECGenParameterSpec ecSpec = new java.security.spec.ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random); //inicializa o gerador de chaves e cria um par (pública e privada)
            java.security.KeyPair keyPair = keyGen.generateKeyPair();
            chavePrivada = keyPair.getPrivate();
            chavePublica = java.util.Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        } catch(java.security.InvalidAlgorithmParameterException | java.security.NoSuchAlgorithmException
                | java.security.NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** Gera a assinatura para uma determinada String.
     * @param entrada String
     * @return String assinatura */
    public String assinar(String entrada) {
        try {
            //obter array de bytes da assinatura
            java.security.Signature dsa = java.security.Signature.getInstance("ECDSA", "BC");
            dsa.initSign(chavePrivada);
            byte[] conjuntoBytes = entrada.getBytes();
            dsa.update(conjuntoBytes);
            conjuntoBytes = dsa.sign();
            //converter bytes da assinatura em String
            int[] lista = new int[conjuntoBytes.length];
            for (int i = 0; i < conjuntoBytes.length; i++) {
                lista[i] = (int)conjuntoBytes[i];
                lista[i] += 128;
            }
            String retorno = "";
            for (int i = 0; i < conjuntoBytes.length; i++) {
                if (lista[i] < 16)
                    retorno += "0";
                retorno += Integer.toString(lista[i],16);
            }
            return retorno;
        } catch (java.security.InvalidKeyException | java.security.NoSuchAlgorithmException
                | java.security.NoSuchProviderException | java.security.SignatureException e) {
            return "";
        }
    }
    
    /** Obter chave pública.
     * @return String chave pública */
    @Override
    public String toString() {
        return chavePublica;
    }
    
    //==========================================================================
    //======== RECURSOS OCULTOS, USADOS PELO SISTEMA CRIPTOGRÁFICO =============
    
    /** Chave privada. */
    private java.security.PrivateKey chavePrivada;
    
    /** Chave pública. */
    private String chavePublica;
}
