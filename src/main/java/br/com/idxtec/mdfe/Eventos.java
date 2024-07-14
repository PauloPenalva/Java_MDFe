package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.AssinaturaEnum;
import br.com.idxtec.mdfe.enums.ServicosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.UrlWebServicesUtil;
import br.com.idxtec.mdfe.webservices.evento.MDFeRecepcaoEventoStub;
import lombok.extern.java.Log;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

@Log
public class Eventos {

    public static String enviarEvento(ConfiguracoesMdfe config, String xml, ServicosEnum tipoEvento, boolean valida) throws MdfeException {

        try {
            xml = Assinar.assinaMDFe(config, xml, AssinaturaEnum.EVENTO);

            log.info("[XML-ENVIO-" + tipoEvento + "]: " + xml);

            if (valida) {
                new Validar().validaXml(config, xml, ServicosEnum.EVENTO);
            }

            OMElement ome = AXIOMUtil.stringToOM(xml);

            return envio(config, tipoEvento, ome);
        } catch (RemoteException  | XMLStreamException e) {
            throw new MdfeException(e.getMessage(), e);
        }

    }

    private static String envio(ConfiguracoesMdfe config, ServicosEnum tipoEvento, OMElement ome) throws RemoteException {

        MDFeRecepcaoEventoStub.MdfeCabecMsg cabecMsg = new MDFeRecepcaoEventoStub.MdfeCabecMsg();
        cabecMsg.setCUF(config.getEstado().getCodigoUF());
        cabecMsg.setVersaoDados(ConstantesUtil.VERSAO.MDFE);


        MDFeRecepcaoEventoStub.MdfeCabecMsgE cabecMsgE = new MDFeRecepcaoEventoStub.MdfeCabecMsgE();
        cabecMsgE.setMdfeCabecMsg(cabecMsg);

        MDFeRecepcaoEventoStub.MdfeDadosMsg dadosMsg = new MDFeRecepcaoEventoStub.MdfeDadosMsg();
        dadosMsg.setExtraElement(ome);

        MDFeRecepcaoEventoStub stub = new MDFeRecepcaoEventoStub(
                config.getAmbiente().equals(AmbienteEnum.PRODUCAO)
                ? UrlWebServicesUtil.PRODUCAO.RECEPCAO_EVENTO
                        : UrlWebServicesUtil.HOMOLOGACAO.RECEPCAO_EVENTO);

        MDFeRecepcaoEventoStub.MdfeRecepcaoEventoResult resultado = stub.mdfeRecepcaoEvento(dadosMsg, cabecMsgE);

        log.info("[XML-RETORNO-" + tipoEvento + "]: " + resultado.getExtraElement().toString());

        return resultado.getExtraElement().toString();
    }
}
