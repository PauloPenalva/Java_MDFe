package br.com.idxtec.mdfe.eventos;

import br.com.idxtec.mdfe.Eventos;
import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.ServicosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.eventos.TEvento;
import br.com.idxtec.mdfe.schemas.eventos.TRetEvento;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;

import javax.xml.bind.JAXBException;

public class CancelarMDFe {

    public static TRetEvento eventoCancelamento(ConfiguracoesMdfe config, TEvento envEvento, boolean valida) throws MdfeException {

        try {
            String xml = XmlMdfeUtil.objectToXml(envEvento);
                    //.replace(" xmlns:ns2=\"http://www.portalfiscal.inf.br/mdfe\" xmlns:ns3=\"http://www.portalfiscal.inf.br/mdfe\"", "");

            xml = Eventos.enviarEvento(config, xml, ServicosEnum.EVENTO_CANC, valida);

            return XmlMdfeUtil.xmlToObject(xml, TRetEvento.class);
        } catch (JAXBException e) {
            throw new MdfeException(e.getMessage(), e);
        }

    }
}
