package br.com.idxtec.mdfe.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventosMdfeEnum {

    CANCELAR("110111", "Cancelamento"),
    ENCERRAR("110112", "Encerramento");

    private final String codigo;
    private final String descricao;
}
