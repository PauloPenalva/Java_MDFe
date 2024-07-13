package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TRetConsSitMDFe;
import br.com.idxtec.mdfe.schemas.consultaStatus.TRetConsStatServ;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;

public class Mdfe {

    private Mdfe() {

    }

    public static TRetConsSitMDFe consultaSituacaoMdfe(ConfiguracoesMdfe configuracoesMdfe, String chaveMdfe) throws MdfeException {
        return ConsultaSituacaoMdfe.consulta(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe), chaveMdfe);
    }

    public static TRetConsStatServ consultaStatusServico(ConfiguracoesMdfe configuracoesMdfe) throws MdfeException {

        return ConsultaStatus.consulta(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe));
    }
}