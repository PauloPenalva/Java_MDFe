package br.com.idxtec.mdfe.util;

import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TConsMDFeNaoEnc;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TConsSitMDFe;
import br.com.idxtec.mdfe.schemas.consultaStatus.TConsStatServ;
import br.com.idxtec.mdfe.exceptions.MdfeException;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class XmlMdfeUtil {

    private static final String STATUS = "TConsStatServ";
    private static final String CONSULTA = "TConsSitMDFe";
    private static final String CONSULTA_NAO_ENCERRADO = "TConsMDFeNaoEnc";


    private XmlMdfeUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Transforma o String do XML em Objeto
     *
     * @param xml
     * @param classe
     * @return T
     */
    public static <T> T xmlToObject(String xml, Class<T> classe) throws JAXBException {
        return JAXB.unmarshal(new StreamSource(new StringReader(xml)), classe);
    }

    /**
     * Transforma o Objeto em XML(String)
     *
     * @param obj
     * @return
     * @throws JAXBException
     * @throws MdfeException
     */
    public static <T> String objectToXml(Object obj) throws JAXBException, MdfeException {
        return objectToXml(obj, StandardCharsets.UTF_8);
    }

    public static <T> String objectToXml(Object obj, Charset encode) throws JAXBException, MdfeException {

        JAXBContext context;
        JAXBElement<?> element;

        switch (obj.getClass().getSimpleName()) {

            case STATUS:
                context = JAXBContext.newInstance(TConsStatServ.class);
                element = new br.com.idxtec.mdfe.schemas.consultaStatus.ObjectFactory().createConsStatServMDFe((TConsStatServ) obj);
                break;
            case CONSULTA:
                context = JAXBContext.newInstance(TConsSitMDFe.class);
                element = new br.com.idxtec.mdfe.schemas.consultaSituacao.ObjectFactory().createConsSitMDFe((TConsSitMDFe) obj);
                break;
            case CONSULTA_NAO_ENCERRADO:
                context = JAXBContext.newInstance(TConsMDFeNaoEnc.class);
                element = new br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.ObjectFactory().createConsMDFeNaoEnc((TConsMDFeNaoEnc) obj);
                break;

            default:
                throw new MdfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());

        }

        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty("jaxb.encoding", "Unicode");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        StringWriter sw = new StringWriter(4096);

        String encodeXml = encode == null || !Charset.isSupported(encode.displayName()) ? "UTF-8" : encode.displayName();

        sw.append("<?xml version=\"1.0\" encoding=\"")
                .append(encodeXml)
                .append("\"?>");

        marshaller.marshal(element, sw);

        return replacesMdfe(sw.toString());
    }


    private static String replacesMdfe(String xml) {

        return xml.replace("<!\\[CDATA\\[<!\\[CDATA\\[", "<!\\[CDATA\\[")
                .replace("\\]\\]>\\]\\]>", "\\]\\]>")
                .replace("ns2:", "")
                .replace("ns3:", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">")
                .replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace(" xmlns=\"\" xmlns:ns3=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace("<MDFe>", "<MDFe xmlns=\"http://www.portalfiscal.inf.br/mdfe\">");

    }
}
