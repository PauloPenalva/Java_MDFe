package br.com.idxtec.mdfe.dom;

import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

import static java.util.Optional.ofNullable;

/**
 * @author Samuel Oliveira
 * @author Paulo Penalva
 * <p>
 * Responsável por iniciar as configurações das operações NF-e.
 * <p>
 * Para iniciar as configurações chame o método estático
 * iniciaConfiguracoes:<br>
 * {@code
 * ConfiguracoesIniciaisMdfe.iniciaConfiguracoes(estado, ambiente, certificado, schemas);
 * }
 * @see ConfiguracoesMdfe
 */

@Getter
@Setter
@Log
public class ConfiguracoesMdfe {

    private EstadosEnum estado;
    private AmbienteEnum ambiente;
    private Certificado certificado;
    private String pastaSchemas;
    private Proxy proxy;
    private Integer timeout;
    private boolean contigenciaSVC;
    private boolean validacaoDocumento = true;
    private String arquivoWebService;
    private Integer retry;
    private InputStream cacert;
    private Charset encode;
    private ZoneId zoneId;

    /**
     * Este método recebe como parâmetro os dados necessários para iniciar a
     * comunicação de operações dos eventos da NF-e. Retorna uma instância dela
     * mesma.
     * Nessa inicializacao é usado o ZoneId padrao America/Sao_Paulo
     *
     * @param estado       enumeration Estados, UF do emitente.
     * @param ambiente     Enumeration AmbienteEnum
     * @param certificado  objeto Certificado
     * @param pastaSchemas local dos arquivo de schemas da NF-e.
     * @return ConfiguracoesIniciaisNfe
     * @see br.com.swconsultoria.certificado.Certificado
     * @see EstadosEnum
     */
    public static ConfiguracoesMdfe criarConfiguracoes(EstadosEnum estado, AmbienteEnum ambiente, Certificado certificado, String pastaSchemas) throws CertificadoException {
        return criarConfiguracoes(estado,ambiente,certificado,pastaSchemas, ZoneId.of("America/Sao_Paulo"));
    }

    /**
     * Este método recebe como parâmetro os dados necessários para iniciar a
     * comunicação de operações dos eventos da MDFe-e. Retorna uma instância dela
     * mesma.
     *
     * @param estado       enumeration Estados, UF do emitente.
     * @param ambiente     Enumeration AmbienteEnum
     * @param certificado  objeto Certificado
     * @param pastaSchemas local dos arquivo de schemas da MDF-e.
     * @param zoneId       Zona para configuracoes de data
     * @return ConfiguracoesIniciaisNfe
     * @see br.com.swconsultoria.certificado.Certificado
     * @see EstadosEnum
     */
    public static ConfiguracoesMdfe criarConfiguracoes(EstadosEnum estado, AmbienteEnum ambiente, Certificado certificado, String pastaSchemas, ZoneId zoneId) throws CertificadoException {

        ConfiguracoesMdfe configuracoesMdfe = new ConfiguracoesMdfe();
        configuracoesMdfe.setEstado(ofNullable(estado).orElseThrow(() -> new IllegalArgumentException("Estado não pode ser Nulo.")));
        configuracoesMdfe.setAmbiente(ofNullable(ambiente).orElseThrow(() -> new IllegalArgumentException("Ambiente não pode ser Nulo.")));
        configuracoesMdfe.setCertificado(ofNullable(certificado).orElseThrow(() -> new IllegalArgumentException("Certificado não pode ser Nulo.")));
        configuracoesMdfe.setPastaSchemas(pastaSchemas);
        configuracoesMdfe.setZoneId(ofNullable(zoneId).orElseThrow(() -> new IllegalArgumentException("Zone ID não pode ser Nulo.")));

        /**
         * Para as versões Java até 11, Eu ainda seto o Encoding por que é permitido.
         * Para quem trabalha com Java 12+, Aconselhasse setar o Encoding :
         * -Dfile.encoding="UTF-8"
         * -Dsun.jnu.encoding="UTF-8"
         *
         */
        if (Integer.parseInt(System.getProperty("java.class.version").substring(0, 2)) < 56) {
            try {
                //Setando Encoding.
                System.setProperty("file.encoding", "UTF-8");
                Field charset = Charset.class.getDeclaredField("defaultCharset");
                charset.setAccessible(true);
                charset.set(null, null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new CertificadoException("Erro ao setar Encoding.");
            }
        }

        log.info(String.format("JAVA-MDFE | Paulo Penalva | paulo.penalva@gmail.com " +
                        "| VERSAO=%s | DATA_VERSAO=%s | PASTA_SCHEMAS=%s | AMBIENTE=%s | ESTADO=%s",
                "3.0.0",
                "12/07/2024",
                pastaSchemas,
                ambiente,
                estado.getNome().toUpperCase()));

        if (!certificado.isValido()) {
            throw new CertificadoException("Certificado Vencido/Inválido");
        }
        return configuracoesMdfe;
    }



    /**
     * Atribui uma string que representa o local da pasta dos schemas da NF-e
     * (.xsd)
     *
     * @param pastaSchemas
     */
    private void setPastaSchemas(String pastaSchemas) {
        this.pastaSchemas = pastaSchemas;
    }


    /**
     * Atribui um objeto Certificado.
     *
     * @param certificado
     */
    private void setCertificado(Certificado certificado) {
        this.certificado = certificado;
    }


    /**
     * Passar encode via String para o xml.
     *
     * @param nomeEncode
     */
    public void setEncode(String nomeEncode) {
        if (nomeEncode != null && !nomeEncode.equals("")) {
            try {
                this.encode = Charset.forName(nomeEncode);
            } catch (Exception ex) {
                this.encode = StandardCharsets.UTF_8;
            }
        }
    }

}
