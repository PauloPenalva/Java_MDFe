package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TConsSitMDFe;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TRetConsSitMDFe;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.UrlWebServicesUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.idxtec.mdfe.webservices.consultaSituacao.MDFeConsultaStub;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import lombok.extern.java.Log;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

@Log
public class ConsultaSituacaoMdfe {

    public static TRetConsSitMDFe consulta(ConfiguracoesMdfe config, String chaveMdfe) throws MdfeException {
        try {
            TConsSitMDFe consSitMDFe = new TConsSitMDFe();
            consSitMDFe.setTpAmb(config.getAmbiente().getCodigo());
            consSitMDFe.setVersao(ConstantesUtil.VERSAO.MDFE);
            consSitMDFe.setXServ("CONSULTAR");
            consSitMDFe.setChMDFe(chaveMdfe);

            String xml = XmlMdfeUtil.objectToXml(consSitMDFe);

            log.info("[XML-ENVIO]: " + xml);

            OMElement ome = AXIOMUtil.stringToOM(xml);


            MDFeConsultaStub.MdfeCabecMsg cabecMsg = new MDFeConsultaStub.MdfeCabecMsg();
            cabecMsg.setCUF(config.getEstado().getCodigoUF());
            cabecMsg.setVersaoDados(ConstantesUtil.VERSAO.MDFE);

            MDFeConsultaStub.MdfeCabecMsgE cabecMsgE = new MDFeConsultaStub.MdfeCabecMsgE();
            cabecMsgE.setMdfeCabecMsg(cabecMsg);

            MDFeConsultaStub.MdfeDadosMsg dadosMsg = new MDFeConsultaStub.MdfeDadosMsg();
            dadosMsg.setExtraElement(ome);


            MDFeConsultaStub stub = new MDFeConsultaStub(
                    config.getAmbiente().equals(AmbienteEnum.PRODUCAO)
                            ? UrlWebServicesUtil.PRODUCAO.CONSULTA
                            : UrlWebServicesUtil.HOMOLOGACAO.CONSULTA);


            MDFeConsultaStub.MdfeConsultaMDFResult result = stub.mdfeConsultaMDF(dadosMsg, cabecMsgE);


            log.info("[XML-RETORNO]: " + result.getExtraElement().toString());

            return XmlMdfeUtil.xmlToObject(result.getExtraElement().toString(), TRetConsSitMDFe.class);
        } catch (Exception e) {
            throw new MdfeException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            String certPath = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/COSTA2025.pfx";
            String certPass = "12345678";

            String pastaSchemas = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/schemas";

            String chaveMdfe = "35170812195067000108580010000000021000000026";

            Certificado certificado = CertificadoService.certificadoPfx(certPath, certPass);

            ConfiguracoesMdfe config = ConfiguracoesMdfe.criarConfiguracoes(
                    EstadosEnum.SP,
                    AmbienteEnum.PRODUCAO,
                    certificado,
                    pastaSchemas);

            TRetConsSitMDFe retorno = ConsultaSituacaoMdfe.consulta(ConfiguracoesUtil.iniciaConfiguracoes(config), chaveMdfe);
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
}
