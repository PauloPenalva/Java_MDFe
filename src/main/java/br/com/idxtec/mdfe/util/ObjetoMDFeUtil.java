package br.com.idxtec.mdfe.util;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ObjetoMDFeUtil {

    private final static String URL_QRCODE = "https://dfe-portal.svrs.rs.gov.br/mdfe/qrCode";

    private ObjetoMDFeUtil() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Element objectToElement(Object objeto, Class<T> classe, String qName) throws MdfeException {

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            JAXB.marshal(new JAXBElement(new QName(qName), classe, objeto), new DOMResult(document));

            return document.getDocumentElement();

        } catch (ParserConfigurationException e) {
            throw new MdfeException("Erro Ao Converter Objeto em Elemento: ", e);
        }
    }

    public static <T> T elementToObject(org.w3c.dom.Element element, Class<T> classe) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(classe);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(element, classe).getValue();
    }

    public static String elementToString(Element element) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) element.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false);
        return serializer.writeToString(element);
    }

    public static String criaQRCode(String chave, ConfiguracoesMdfe configuracoesMdfe) throws MdfeException {

        StringBuilder qrCode = new StringBuilder();
        qrCode.append(URL_QRCODE);
        qrCode.append("?chMDFe=");
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


    /**
     * Verifica se um objeto é vazio.
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty() ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.isEmpty() || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }
}
