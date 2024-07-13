package br.com.idxtec.mdfe.util;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static java.util.Optional.ofNullable;

public class ObjetoMDFeUtil {

    private final static String URL_QRCODE = "https://dfe-portal.svrs.rs.gov.br/mdfe/qrCode";

    private ObjetoMDFeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String criaQRCode(String chave, ConfiguracoesMdfe configuracoesMdfe) throws MdfeException {

        StringBuilder qrCode = new StringBuilder();
        qrCode.append(URL_QRCODE);
        qrCode.append("?chCTe=");
        qrCode.append(chave);
        qrCode.append("&tpAmb=");
        qrCode.append(configuracoesMdfe.getAmbiente().getCodigo());
        if (chave.charAt(34) == '2') {
            qrCode.append("&sign=");
            try {
                qrCode.append(assinaSign(chave, configuracoesMdfe.getCertificado()));
            } catch (Exception e) {
                throw new MdfeException("Erro ao assinar Chave contingencia: ", e);
            }
        }

        return qrCode.toString();

    }

    private static String assinaSign(String id, Certificado certificado) throws NoSuchAlgorithmException, CertificadoException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException, SignatureException {

        KeyStore keyStore = CertificadoService.getKeyStore(certificado);
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(certificado.getNome(),
                new KeyStore.PasswordProtection(ofNullable(certificado.getSenha()).orElse("").toCharArray()));
        byte[] data = id.getBytes(StandardCharsets.UTF_8);

        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(pkEntry.getPrivateKey());
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        return (Base64.getEncoder().encodeToString(signatureBytes))
                .replace("&#13;", "")
                .replace("\r\n", "")
                .replace("\n", "")
                .replace(System.lineSeparator(), "");
    }
}
