package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.AssinaturaEnum;
import br.com.idxtec.mdfe.enums.ServicosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.recepcao.TMDFe;
import br.com.idxtec.mdfe.schemas.recepcao.TRetMDFe;
import br.com.idxtec.mdfe.util.UrlWebServicesUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.idxtec.mdfe.webservices.recepcaoSinc.MDFeRecepcaoSincStub;
import br.com.idxtec.mdfe.ws.RetryParameter;
import lombok.extern.java.Log;
import org.apache.axis2.transport.http.HTTPConstants;

import static java.util.Optional.ofNullable;

/**
 * Classe responsável por Enviar a MDFe
 *
 * @author Paulo Penalva - paulo.penalva@gmail.com - github.com/PauloPenalva
 */
@Log
public class Enviar {

    private Enviar() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Metodo para Montar a NFE
     *
     * @param mdfe - Objeto TEnviMDFe
     * @param valida  - Booleano para validar o Xml
     * @throws MdfeException
     */
    public static TMDFe montaMDFe(ConfiguracoesMdfe config, TMDFe mdfe, boolean valida) throws MdfeException {

        try {

            /**
             * Cria o xml
             */
            String xml = XmlMdfeUtil.objectToXml(mdfe);

            /**
             * Assina o Xml
             */
            xml = Assinar.assinaMDFe(config, xml, AssinaturaEnum.MDFE);

            //Retira Quebra de Linha
            xml = xml.replaceAll(System.lineSeparator(), "");

            log.info("[XML-ASSINADO]: " + xml);

            /**
             * Valida o Xml caso sejá selecionado True
             */
            if (valida) {
                new Validar().validaXml(config, xml, ServicosEnum.ENVIO);
            }

            return XmlMdfeUtil.xmlToObject(xml, TMDFe.class);

        } catch (Exception e) {
            throw new MdfeException(e.getMessage(),e);
        }

    }

    /**
     * Metodo para Enviar a MDFe
     *
     * @param config - Configurações
     * @param mdfe - Objeto TEnviMDFe
     * @return TRetEnviMDFe
     * @throws MdfeException
     */
    public static TRetMDFe enviaMDFe(ConfiguracoesMdfe config, TMDFe mdfe) throws MdfeException {
        try {

            String xml = XmlMdfeUtil.objectToXml(mdfe);

            log.info("[XML-ENVIO]: " + xml);

            MDFeRecepcaoSincStub.MdfeDadosMsg dadosMsg = new MDFeRecepcaoSincStub.MdfeDadosMsg();
            dadosMsg.setMdfeDadosMsg(XmlMdfeUtil.xmlToGZip(xml));

            MDFeRecepcaoSincStub stub = new MDFeRecepcaoSincStub(
                        config.getAmbiente().equals(AmbienteEnum.PRODUCAO)
                            ? UrlWebServicesUtil.PRODUCAO.RECEPCAO_SINC
                            : UrlWebServicesUtil.HOMOLOGACAO.RECEPCAO_SINC
            );

            // Timeout
            if (ofNullable(config.getTimeout()).isPresent()) {
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, config.getTimeout());
            }

            if (ofNullable(config.getRetry()).isPresent()) {
                RetryParameter.populateRetry(stub, config.getRetry());
            }

            MDFeRecepcaoSincStub.MdfeRecepcaoResult result = stub.mdfeRecepcao(dadosMsg);

            log.info("[XML-RETORNO]: " + result.getExtraElement().toString());

            return XmlMdfeUtil.xmlToObject(result.getExtraElement().toString(), TRetMDFe.class);
        } catch (Exception e) {
            throw new MdfeException(e.getMessage(),e);
        }
    }
}
