package br.com.idxtec.mdfe.enums;

import lombok.Getter;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 02/03/2019 - 20:03
 */
@Getter
public enum ServicosEnum {

    ENVIO("mdfe_v3.00","mdfe_v3.00.xsd"),
    QRCODE("URL-QRCode", ""),
    EVENTO("", "eventoMDFe_v3.00.xsd"),
    EVENTO_CANC(Constants.RECEPCAO_EVENTO_3_00, "evCancMDFe_v3.00.xsd"),
    EVENTO_ALTERACAO_PAGTO_SERV(Constants.RECEPCAO_EVENTO_3_00, "evAlteracaoPagtoServMDFe_v3.00.xsd"),
    EVENTO_CONFIRMA_SERV(Constants.RECEPCAO_EVENTO_3_00, "evConfirmaServMDFe_v3.00.xsd"),
    EVENTO_ENC(Constants.RECEPCAO_EVENTO_3_00, "evEncMDFe_v3.00.xsd"),
    EVENTO_INC_CONDUTOR(Constants.RECEPCAO_EVENTO_3_00, "evIncCondutorMDFe_v3.00.xsd"),
    EVENTO_INCLUSAO_DFE(Constants.RECEPCAO_EVENTO_3_00, "evInclusaoDFeMDFe_v3.00.xsd"),
    EVENTO_PAGTO_OPER(Constants.RECEPCAO_EVENTO_3_00, "evPagtoOperMDFe_v3.00.xsd");

    private final String servico;
    private final String xsd;

    ServicosEnum(String servico, String xsd) {
        this.servico = servico;
        this.xsd = xsd;
    }

    private static class Constants {
        private static final String RECEPCAO_EVENTO_3_00 = "RecepcaoEvento_3.00";
    }

}
