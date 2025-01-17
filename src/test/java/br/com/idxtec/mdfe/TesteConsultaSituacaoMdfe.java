package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TRetConsSitMDFe;
import lombok.extern.java.Log;

@Log
public class TesteConsultaSituacaoMdfe {

    public static void main(String[] args) {
        try {
            String chaveMdfe = "35240709109848000128580010000000011972259814";

            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            TRetConsSitMDFe retorno = ConsultaSituacaoMdfe.consulta(config, chaveMdfe);
            log.info("Status: " + retorno.getCStat());
            log.info("Motivo: " + retorno.getXMotivo());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

}
