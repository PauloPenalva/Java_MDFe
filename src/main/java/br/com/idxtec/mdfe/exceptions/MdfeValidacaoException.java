package br.com.idxtec.mdfe.exceptions;

import lombok.Getter;

@Getter
public class MdfeValidacaoException extends MdfeException{

    public MdfeValidacaoException(Throwable e) {
        super(e);
    }

    public MdfeValidacaoException(String message) {
        super(message);
    }
}
