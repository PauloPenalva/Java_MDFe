package br.com.idxtec.mdfe.exceptions;

import lombok.Getter;

@Getter
public class MdfeException extends Exception{
    String message;

    /**
     * Construtor da classe.
     *
     * @param e
     */
    public MdfeException(Throwable e) {
        super(e);
    }


    /**
     * Construtor da classe.
     *
     * @param message
     */
    public MdfeException(String message) {
        this((Throwable) null);
        this.message = message;
    }

    /**
     * Construtor da classe.
     *
     * @param message
     * @param cause
     */
    public MdfeException(String message, Throwable cause) {
        this(cause);
        this.message = message;
    }
}
