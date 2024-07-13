package br.com.idxtec.mdfe.util;

public interface UrlWebServicesUtil {

    interface PRODUCAO {
        String RECEPCAO = "https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcao/MDFeRecepcao.asmx";
        String RET_RECEPCAO = "https://mdfe.svrs.rs.gov.br/ws/MDFeRetRecepcao/MDFeRetRecepcao.asmx";
        String RECEPCAO_EVENTO = "https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcaoEvento/MDFeRecepcaoEvento.asmx";
        String CONSULTA = "https://mdfe.svrs.rs.gov.br/ws/MDFeConsulta/MDFeConsulta.asmx";
        String STATUS_SERVICO = "https://mdfe.svrs.rs.gov.br/ws/MDFeStatusServico/MDFeStatusServico.asmx";
        String CONS_NAO_ENC = "https://mdfe.svrs.rs.gov.br/ws/MDFeConsNaoEnc/MDFeConsNaoEnc.asmx";
        String DISTRIBUICAO_DFE = "https://mdfe.svrs.rs.gov.br/ws/MDFeDistribuicaoDFe/MDFeDistribuicaoDFe.asmx";
        String RECEPCAO_SINC = "https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcaoSinc/MDFeRecepcaoSinc.asmx";
    }

    interface HOMOLOGACAO {
        String RECEPCAO = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcao/MDFeRecepcao.asmx";
        String RET_RECEPCAO = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRetRecepcao/MDFeRetRecepcao.asmx";
        String RECEPCAO_EVENTO = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcaoEvento/MDFeRecepcaoEvento.asmx";
        String CONSULTA = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeConsulta/MDFeConsulta.asmx";
        String STATUS_SERVICO = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeStatusServico/MDFeStatusServico.asmx";
        String CONS_NAO_ENC = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeConsNaoEnc/MDFeConsNaoEnc.asmx";
        String DISTRIBUICAO_DFE = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeDistribuicaoDFe/MDFeDistribuicaoDFe.asmx";
        String RECEPCAO_SINC = "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcaoSinc/MDFeRecepcaoSinc.asmx";
    }
}
