package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AssinaturaEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Classe Responsavel Por Assinar O Xml.
 *
 * @author Samuel Oliveira - samuel@swconsultoria.com.br - www.swconsultoria.com.br
 */
public class Assinar {

    private static PrivateKey privateKey;
    private static KeyInfo keyInfo;

    private Assinar() {}

    /**
     * @param stringXml
     * @param tipoAssinatura ('MDFe' para mdfe normal , 'infInut' para inutilizacao, 'evento'
     *                       para eventos)
     * @return String do Xml Assinado
     * @throws MdfeException
     */
    public static String assinaMDFe(ConfiguracoesMdfe config, String stringXml, AssinaturaEnum tipoAssinatura) throws MdfeException {

        stringXml = stringXml.replaceAll(System.lineSeparator(), ""); // Erro quando tem salto de linha.
        stringXml = stringXml.replaceAll("\\s+<", "<"); // Erro Espaço antes do final da Tag.
        stringXml = assinaDocMDFe(config, stringXml, tipoAssinatura);
        stringXml = stringXml.replace("&#13;", ""); // Java 11

        return stringXml;
    }

    private static String assinaDocMDFe(ConfiguracoesMdfe config, String xml, AssinaturaEnum tipoAssinatura) throws MdfeException {

        try {
            Document document = documentFactory(xml);
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            ArrayList<Transform> transformList = signatureFactory(signatureFactory);
            loadCertificates(config, signatureFactory);

            assinarMDFe(tipoAssinatura, signatureFactory, transformList, privateKey, keyInfo, document);

            return outputXML(document);
        } catch (SAXException | IOException | ParserConfigurationException | NoSuchAlgorithmException
                 | InvalidAlgorithmParameterException | KeyStoreException | UnrecoverableEntryException
                 | CertificadoException | MarshalException
                 | XMLSignatureException e) {
            throw new MdfeException("Erro ao Assinar MDFe", e);
        }
    }

    private static void assinarMDFe(AssinaturaEnum tipoAssinatura, XMLSignatureFactory fac, ArrayList<Transform> transformList,
                                    PrivateKey privateKey, KeyInfo ki, Document document) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, MarshalException, XMLSignatureException {

        NodeList elements = document.getElementsByTagName(tipoAssinatura.getTag());

        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(0);
        String id = el.getAttribute("Id");
        el.setIdAttribute("Id", true);
        Reference ref = fac.newReference("#" + id, fac.newDigestMethod(DigestMethod.SHA1, null), transformList, null,
                null);

        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

        XMLSignature signature = fac.newXMLSignature(si, ki);

        DOMSignContext dsc = new DOMSignContext(privateKey, document.getFirstChild());

        dsc.setBaseURI("ok");

        signature.sign(dsc);
    }

    private static ArrayList<Transform> signatureFactory(XMLSignatureFactory signatureFactory)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        ArrayList<Transform> transformList = new ArrayList<>();
        Transform envelopedTransform = signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
        Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null);

        transformList.add(envelopedTransform);
        transformList.add(c14NTransform);
        return transformList;
    }

    private static Document documentFactory(String xml) throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(xml)));
    }

    private static void loadCertificates(ConfiguracoesMdfe config, XMLSignatureFactory signatureFactory)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificadoException {

        Certificado certificado = config.getCertificado();
        KeyStore keyStore = CertificadoService.getKeyStore(certificado);

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(certificado.getNome(),
                new KeyStore.PasswordProtection(ofNullable(certificado.getSenha()).orElse("").toCharArray()));
        privateKey = pkEntry.getPrivateKey();

        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        List<X509Certificate> x509Content = new ArrayList<>();

        x509Content.add(CertificadoService.getCertificate(certificado, keyStore));
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));
    }

    private static String outputXML(Document doc) throws MdfeException {

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));
            return os.toString()
                    .replace(System.lineSeparator(), "")
                    .replace(" standalone=\"no\"", "");
        } catch (TransformerException | IOException e) {
            throw new MdfeException("Erro ao Transformar Documento:", e);
        }

    }

}
