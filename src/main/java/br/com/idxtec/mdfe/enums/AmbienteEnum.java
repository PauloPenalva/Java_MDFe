package br.com.idxtec.mdfe.enums;

import lombok.Getter;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 02/03/2019 - 20:06
 */
@Getter
public enum AmbienteEnum {

    HOMOLOGACAO("2"),
    PRODUCAO("1");

    private final String codigo;

    AmbienteEnum(String codigo) {
        this.codigo = codigo;
    }

    public static AmbienteEnum getByCodigo(String codigo) {
        for (AmbienteEnum e : values()) {
            if (e.codigo.equals(codigo)) return e;
        }
        throw new IllegalArgumentException();
    }
}
