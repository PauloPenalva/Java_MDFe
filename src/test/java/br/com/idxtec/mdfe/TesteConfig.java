package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.FileNotFoundException;

public class TesteConfig {

    public static ConfiguracoesMdfe iniciaConfiguracoes(EstadosEnum estado, AmbienteEnum ambiente) throws CertificadoException, FileNotFoundException, MdfeException {

        String certPath = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/COSTA2025.pfx";
        String certPass = "12345678";

        String pastaSchemas = "/Users/paulopenalva/Projetos/IdxSistemas/Java_MDFe/schemas";

        Certificado certificado = CertificadoService.certificadoPfx(certPath, certPass);
        CertificadoService.inicializaCertificado(certificado);

        ConfiguracoesMdfe configuracoesMdfe = ConfiguracoesMdfe.criarConfiguracoes(
                estado,
                ambiente,
                certificado,
                pastaSchemas);


       return configuracoesMdfe;

    }
}
