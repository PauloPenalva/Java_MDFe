package br.com.idxtec.mdfe.util;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import static java.util.Optional.ofNullable;


/**
 * Classe Responsavel Por Carregar as informações do Certificado Digital
 *
 * @author Samuel Oliveira
 *
 */
public class ConfiguracoesUtil {

    /**
     * Recebe como parâmetro um objeto ConfiguracoesNfe e Inicializa as COnfigurações e retorna um objeto
     * ConfiguracoesNfe.
     *
     * <p>
     * Semelhante ao método iniciaConfiguracoes(), o Certificado Digital será
     * validado e inicializado.Caso ocorrá algum prolema será disparado um
     * NfeException
     * </p>
     *
     * @param configuracoesMdfe ConfiguracoesMdfe
     * @return ConfiguracoesMdfe
     * @throws MdfeException
     * @see CertificadoException
     */
    public static ConfiguracoesMdfe iniciaConfiguracoes(ConfiguracoesMdfe configuracoesMdfe) throws MdfeException {


        return iniciaConfiguracoes(configuracoesMdfe, null);
    }

    /**
     * Recebe como parâmetro um objeto ConfiguracoesNfe e Inicializa as COnfigurações e retorna um objeto
     * ConfiguracoesNfe.
     *
     * <p>
     * Semelhante ao método iniciaConfiguracoes(), o Certificado Digital será
     * validado e inicializado.Caso ocorrá algum prolema será disparado um
     * NfeException
     * </p>
     *
     * @param configuracoesMdfe
     * @param cpfCnpj
     * @return ConfiguracoesWebNfe
     * @throws MdfeException
     * @see CertificadoException
     */
    public static ConfiguracoesMdfe iniciaConfiguracoes(ConfiguracoesMdfe configuracoesMdfe, String cpfCnpj) throws MdfeException {

        ofNullable(configuracoesMdfe).orElseThrow( () -> new MdfeException("Configurações não foram criadas"));

        try {
            if (!configuracoesMdfe.getCertificado().isValido()) {
                throw new CertificadoException("Certificado vencido ou inválido.");
            }

            if (configuracoesMdfe.isValidacaoDocumento() && cpfCnpj != null && !configuracoesMdfe.getCertificado().getCnpjCpf().substring(0,8).equals(cpfCnpj.substring(0,8))) {
                throw new CertificadoException("Documento do Certificado("+configuracoesMdfe.getCertificado().getCnpjCpf()+") não equivale ao Documento do Emissor("+cpfCnpj+")");
            }

            if( ofNullable(configuracoesMdfe.getCacert()).isPresent()){
                CertificadoService.inicializaCertificado(configuracoesMdfe.getCertificado(),configuracoesMdfe.getCacert());
            }else{
                CertificadoService.inicializaCertificado(configuracoesMdfe.getCertificado());
            }
        } catch (CertificadoException e) {
            throw new MdfeException(e.getMessage(),e);
        }

        return configuracoesMdfe;
    }

}
