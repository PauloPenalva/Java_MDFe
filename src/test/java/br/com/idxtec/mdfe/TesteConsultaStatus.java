package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.schemas.consultaStatus.TRetConsStatServ;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;
import lombok.extern.java.Log;

@Log
public class TesteConsultaStatus {

    public static void main(String[] args) {
        try {
            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            TRetConsStatServ retorno = ConsultaStatus.consulta(ConfiguracoesUtil.iniciaConfiguracoes(config));
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

}
