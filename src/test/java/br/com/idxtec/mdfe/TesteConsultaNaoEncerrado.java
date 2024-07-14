package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TRetConsMDFeNaoEnc;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;
import lombok.extern.java.Log;

@Log
public class TesteConsultaNaoEncerrado {

    public static void main(String[] args) {
        try {
            String cnpj = "09109848000128";
            String cpf = null;

            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            TRetConsMDFeNaoEnc retorno = ConsultaNaoEncerrado.consulta(ConfiguracoesUtil.iniciaConfiguracoes(config), cnpj, null);
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
            log.info("Quantidade de MDF-e não encerrados: " + retorno.getInfMDFe().size());
            log.info("Chaves dos MDF-e não encerrados:");
            log.info("");
            retorno.getInfMDFe().forEach(infMDFe -> {
                log.info("Chave: " + infMDFe.getChMDFe());
                log.info("Protocolo: " + infMDFe.getNProt());
                log.info("=======================================================");
            });

        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
}
