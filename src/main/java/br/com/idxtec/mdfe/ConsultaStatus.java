package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.schemas.consultaStatus.TConsStatServ;
import br.com.idxtec.mdfe.schemas.consultaStatus.TRetConsStatServ;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;
import br.com.idxtec.mdfe.util.UrlWebServicesUtil;
import br.com.idxtec.mdfe.webservices.consultaStatus.MDFeStatusServicoStub;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import lombok.extern.java.Log;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

/**
 * Classe responsável por Consultar o Status do Serviço
 * @author Paulo Penalva - paulo.penalva@gmail.com
 * @since 12/07/2024
 */
@Log
public class ConsultaStatus {

    /**
     * Metodo para Consulta do Status do Serviço
     *
     * @author Paulo Penalva
     * @param config Configurações MDFe
     * @return TRetConsStatServ
     */
    public static TRetConsStatServ consulta(ConfiguracoesMdfe config) throws MdfeException {

        try {
            TConsStatServ consStatServ = new TConsStatServ();
            consStatServ.setTpAmb(config.getAmbiente().getCodigo());
            consStatServ.setXServ("STATUS");
            consStatServ.setVersao(ConstantesUtil.VERSAO.MDFE);

            String xml = XmlMdfeUtil.objectToXml(consStatServ);

            log.info("[XML-ENVIO]: " + xml);

            OMElement ome = AXIOMUtil.stringToOM(xml);

            MDFeStatusServicoStub.MdfeCabecMsg cabec = new MDFeStatusServicoStub.MdfeCabecMsg();
            cabec.setCUF(config.getEstado().getCodigoUF());
            cabec.setVersaoDados(ConstantesUtil.VERSAO.MDFE);

            MDFeStatusServicoStub.MdfeCabecMsgE cabecEnv = new MDFeStatusServicoStub.MdfeCabecMsgE();
            cabecEnv.setMdfeCabecMsg(cabec);

            MDFeStatusServicoStub.MdfeDadosMsg dados = new MDFeStatusServicoStub.MdfeDadosMsg();
            dados.setExtraElement(ome);

            MDFeStatusServicoStub stub = new MDFeStatusServicoStub(
                    config.getAmbiente().equals(AmbienteEnum.PRODUCAO)
                            ? UrlWebServicesUtil.PRODUCAO.STATUS_SERVICO
                            : UrlWebServicesUtil.HOMOLOGACAO.STATUS_SERVICO);

            MDFeStatusServicoStub.MdfeStatusServicoMDFResult result = stub.mdfeStatusServicoMDF(dados, cabecEnv);

            log.info("[XML-RETORNO]: " + result.getExtraElement().toString());

            return XmlMdfeUtil.xmlToObject(result.getExtraElement().toString(), TRetConsStatServ.class);
        } catch (Exception e) {
            throw new MdfeException(e.getMessage(), e);
        }

    }

    public static void main(String[] args) {
        try {
            String certPath = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/COSTA2025.pfx";
            String certPass = "12345678";

            String pastaSchemas = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/schemas";

            Certificado certificado = CertificadoService.certificadoPfx(certPath, certPass);

            ConfiguracoesMdfe config = ConfiguracoesMdfe.criarConfiguracoes(
                    EstadosEnum.SP,
                    AmbienteEnum.HOMOLOGACAO,
                    certificado,
                    pastaSchemas);

            TRetConsStatServ retorno = ConsultaStatus.consulta(ConfiguracoesUtil.iniciaConfiguracoes(config));
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
}