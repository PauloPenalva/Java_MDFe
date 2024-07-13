package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.consultaNaoEncerrado.TRetConsMDFeNaoEnc;
import br.com.idxtec.mdfe.schemas.consultaSituacao.TRetConsSitMDFe;
import br.com.idxtec.mdfe.schemas.consultaStatus.TRetConsStatServ;
import br.com.idxtec.mdfe.schemas.recepcao.TEnviMDFe;
import br.com.idxtec.mdfe.schemas.recepcao.TMDFe;
import br.com.idxtec.mdfe.schemas.recepcao.TRetMDFe;
import br.com.idxtec.mdfe.util.ConfiguracoesUtil;

public class Mdfe {

    private Mdfe() {
        throw new IllegalStateException("Utility class");
    }

    public static TMDFe montaMDFe(ConfiguracoesMdfe configuracoesMdfe, TMDFe enviMDFe, boolean valida) throws MdfeException {
        return Enviar.montaMDFe(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe), enviMDFe, valida);
    }


    public static TRetMDFe enviar(ConfiguracoesMdfe configuracoesMdfe, TMDFe mdfe) throws MdfeException {
        return Enviar.enviaMDFe(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe), mdfe);
    }


    public static TRetConsMDFeNaoEnc consultaNaoEncerrado(ConfiguracoesMdfe configuracoesMdfe, String cnpj, String cpf) throws MdfeException {
        return ConsultaNaoEncerrado.consulta(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe), cnpj, cpf);
    }

    public static TRetConsSitMDFe consultaSituacaoMdfe(ConfiguracoesMdfe configuracoesMdfe, String chaveMdfe) throws MdfeException {
        return ConsultaSituacaoMdfe.consulta(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe), chaveMdfe);
    }

    public static TRetConsStatServ consultaStatusServico(ConfiguracoesMdfe configuracoesMdfe) throws MdfeException {

        return ConsultaStatus.consulta(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesMdfe));
    }
}
