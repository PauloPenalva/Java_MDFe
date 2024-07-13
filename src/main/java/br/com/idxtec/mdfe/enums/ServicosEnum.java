package br.com.idxtec.mdfe.enums;

import lombok.Getter;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 02/03/2019 - 20:03
 */
@Getter
public enum ServicosEnum {

    ENVIO("mdfe_v3.00","mdfe_v3.00.xsd"),
    QRCODE("URL-QRCode", "");

    private final String servico;
    private final String xsd;

    ServicosEnum(String servico, String xsd) {
        this.servico = servico;
        this.xsd = xsd;
    }

}
