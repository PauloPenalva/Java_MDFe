package br.com.idxtec.mdfe.util;

import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TConsMDFeNaoEnc;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TConsSitMDFe;
import br.com.idxtec.mdfe.schemas.consultaStatus.TConsStatServ;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.eventos.TEvento;
import br.com.idxtec.mdfe.schemas.eventos.TProcEvento;
import br.com.idxtec.mdfe.schemas.recepcao.MdfeProc;
import br.com.idxtec.mdfe.schemas.recepcao.TMDFe;
import br.com.idxtec.mdfe.schemas.recepcao.TProtMDFe;
import lombok.extern.java.Log;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

@Log
public class XmlMdfeUtil {

    private static final String STATUS = "TConsStatServ";
    private static final String CONSULTA = "TConsSitMDFe";
    private static final String CONSULTA_NAO_ENCERRADO = "TConsMDFeNaoEnc";
    private static final String MDFE = "TMDFe";
    private static final String MDFE_PROT = "TProtMDFe";
    private static final String MDFE_PROC = "MdfeProc";
    private static final String EVENTO = "TEvento";
    private static final String EVENTO_PROC = "TProcEvento";


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
        return objectToXml(obj, UTF_8);
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
            case MDFE:
                context = JAXBContext.newInstance(TMDFe.class);
                element = new JAXBElement<TMDFe>(
                        new QName("http://www.portalfiscal.inf.br/mdfe", "MDFe"),
                        TMDFe.class,
                        (TMDFe) obj);
                break;
            case MDFE_PROT:
                context = JAXBContext.newInstance(TProtMDFe.class);
                element = new JAXBElement<TProtMDFe>(
                        new QName("http://www.portalfiscal.inf.br/mdfe", "protMDFe"),
                        TProtMDFe.class,
                        (TProtMDFe) obj);
                break;
            case MDFE_PROC:
                context = JAXBContext.newInstance(MdfeProc.class);
                element = new JAXBElement<MdfeProc>(
                        new QName("http://www.portalfiscal.inf.br/mdfe", "mdfeProc"),
                        MdfeProc.class,
                        (MdfeProc) obj);
                break;
            case EVENTO:
                context = JAXBContext.newInstance(TEvento.class);
                element = new JAXBElement<TEvento>(
                        new QName("http://www.portalfiscal.inf.br/mdfe", "eventoMDFe"),
                        TEvento.class,
                        (TEvento) obj);
                break;

            case EVENTO_PROC:
                context = JAXBContext.newInstance(TProcEvento.class);
                element = new JAXBElement<TProcEvento>(
                        new QName("http://www.portalfiscal.inf.br/mdfe", "retEventoMDFe"),
                        TProcEvento.class,
                        (TProcEvento) obj);
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
                //.replace(" xmlns=\"\" xmlns:ns3=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace(" xmlns=\"\" xmlns:ns2=\"http://www.portalfiscal.inf.br/mdfe\" xmlns:ns3=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace("<MDFe>", "<MDFe xmlns=\"http://www.portalfiscal.inf.br/mdfe\">");

    }


    public static String gZipToXml(byte[] conteudo) throws IOException {
        if (conteudo == null || conteudo.length == 0) {
            return "";
        }
        GZIPInputStream gis;
        gis = new GZIPInputStream(new ByteArrayInputStream(conteudo));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, UTF_8));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }

        return outStr.toString();
    }

    public static String xmlToGZip(final String xml) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            return null;
        }

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final GZIPOutputStream gzipOutput = new GZIPOutputStream(baos)) {
            gzipOutput.write(xml.getBytes(UTF_8));
            gzipOutput.finish();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao compactar GZIp", e);
        }
    }


    public static String dataMdfe(LocalDateTime dataASerFormatada) {
        return dataMdfe(dataASerFormatada, ZoneId.systemDefault());
    }

    public static String dataMdfe(LocalDateTime dataASerFormatada, ZoneId zoneId) {
        try {
            GregorianCalendar calendar = GregorianCalendar.from(dataASerFormatada.atZone(ofNullable(zoneId).orElse(ZoneId.of("Brazil/East"))));

            XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            xmlCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            return xmlCalendar.toString();

        } catch (DatatypeConfigurationException e) {
            log.log(Level.SEVERE, "Erro ao converter Data CTe", e);
        }
        return null;
    }

    public static String criaMdfeProc(TMDFe mdfe, Object retorno) throws MdfeException, JAXBException {

        MdfeProc mdfeProc = new MdfeProc();
        mdfeProc.setVersao(ConstantesUtil.VERSAO.MDFE);
        mdfeProc.setMDFe(mdfe);
        mdfeProc.setProtMDFe(
                xmlToObject(objectToXml(retorno), br.com.idxtec.mdfe.schemas.recepcao.TProtMDFe.class));

        return XmlMdfeUtil.objectToXml(mdfeProc);
    }
}
