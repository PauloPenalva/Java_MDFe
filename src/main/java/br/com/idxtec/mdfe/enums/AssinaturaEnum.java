package br.com.idxtec.mdfe.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssinaturaEnum {

    MDFE("MDFe","infMDFe"),
    EVENTO("eventoMDFe","infEvento");

    private final String tipo;
    private final String tag;
}
