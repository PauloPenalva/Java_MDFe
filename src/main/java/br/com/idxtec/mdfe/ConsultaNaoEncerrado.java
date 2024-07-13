package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TConsMDFeNaoEnc;
import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TRetConsMDFeNaoEnc;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.UrlWebServicesUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.idxtec.mdfe.webservices.consultaNaoEncerrado.MDFeConsNaoEncStub;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import lombok.extern.java.Log;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

@Log
public class ConsultaNaoEncerrado {

    private ConsultaNaoEncerrado() {
        throw new IllegalStateException("Utility class");
    }

    public static TRetConsMDFeNaoEnc consulta(ConfiguracoesMdfe config, String cnpj, String cpf) throws MdfeException {
        try {
            TConsMDFeNaoEnc consMDFeNaoEnc = new TConsMDFeNaoEnc();
            consMDFeNaoEnc.setVersao(ConstantesUtil.VERSAO.MDFE);
            consMDFeNaoEnc.setTpAmb(config.getAmbiente().getCodigo());
            consMDFeNaoEnc.setXServ("CONSULTAR NÃO ENCERRADOS");

            if (cnpj != null && !cnpj.isEmpty()) {
                consMDFeNaoEnc.setCNPJ(cnpj);
            }

            if (cpf != null && !cpf.isEmpty()) {
                consMDFeNaoEnc.setCPF(cpf);
            }


            String xml = XmlMdfeUtil.objectToXml(consMDFeNaoEnc);

            log.info("[XML-ENVIO]: " + xml);

            OMElement ome = AXIOMUtil.stringToOM(xml);

            MDFeConsNaoEncStub.MdfeCabecMsg cabec = new MDFeConsNaoEncStub.MdfeCabecMsg();
            cabec.setCUF(config.getEstado().getCodigoUF());
            cabec.setVersaoDados(ConstantesUtil.VERSAO.MDFE);

            MDFeConsNaoEncStub.MdfeCabecMsgE cabecEnv = new MDFeConsNaoEncStub.MdfeCabecMsgE();
            cabecEnv.setMdfeCabecMsg(cabec);

            MDFeConsNaoEncStub.MdfeDadosMsg dados = new MDFeConsNaoEncStub.MdfeDadosMsg();
            dados.setExtraElement(ome);

            MDFeConsNaoEncStub stub = new MDFeConsNaoEncStub(config.getAmbiente().equals(AmbienteEnum.PRODUCAO)
                    ? UrlWebServicesUtil.PRODUCAO.CONS_NAO_ENC
                    : UrlWebServicesUtil.HOMOLOGACAO.CONS_NAO_ENC);

            MDFeConsNaoEncStub.MdfeConsNaoEncResult result = stub.mdfeConsNaoEnc(dados, cabecEnv);

            log.info("[XML-RETORNO]: " + result.getExtraElement().toString());

            return XmlMdfeUtil.xmlToObject(result.getExtraElement().toString(), TRetConsMDFeNaoEnc.class);

        } catch (Exception e) {
            throw new MdfeException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            String certPath = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/COSTA2025.pfx";
            String certPass = "12345678";

            String pastaSchemas = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/schemas";

            String cnpj = "09109848000128";
            String cpf = null;

            Certificado certificado = CertificadoService.certificadoPfx(certPath, certPass);

            ConfiguracoesMdfe config = ConfiguracoesMdfe.criarConfiguracoes(
                    EstadosEnum.SP,
                    AmbienteEnum.HOMOLOGACAO,
                    certificado,
                    pastaSchemas);

            TRetConsMDFeNaoEnc retorno = ConsultaNaoEncerrado.consulta(ConfiguracoesUtil.iniciaConfiguracoes(config), cnpj, null);
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
}
