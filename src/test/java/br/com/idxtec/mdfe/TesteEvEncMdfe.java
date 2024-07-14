package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.enums.EventosMdfeEnum;
import br.com.idxtec.mdfe.enums.StatusMdfeEnum;
import br.com.idxtec.mdfe.eventos.EncerrarMDFe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.eventos.EvEncMDFe;
import br.com.idxtec.mdfe.schemas.eventos.TEvento;
import br.com.idxtec.mdfe.schemas.eventos.TRetEvento;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.ObjetoMDFeUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import lombok.extern.java.Log;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

@Log
public class TesteEvEncMdfe {

    public static void main(String[] args) {

        try {
            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            String protocolo = "935240000047550";
            String cCodUfEnc = "35";
            String chave = "35240709109848000128580010000000041548903185";
            String sequencia = "01";
            String tpEvento = EventosMdfeEnum.ENCERRAR.getCodigo();

            String EventoId = "ID" + tpEvento + chave + sequencia;

            TEvento.InfEvento.DetEvento detEvento = new TEvento.InfEvento.DetEvento();
            detEvento.setAny(ObjetoMDFeUtil.objectToElement(
                    preencheObjetoEncMDFe(protocolo, cCodUfEnc),
                    EvEncMDFe.class,
                    "evEncMDFe"));
            detEvento.setVersaoEvento(ConstantesUtil.VERSAO.MDFE);

            TEvento.InfEvento infEvento = new TEvento.InfEvento();
            infEvento.setId(EventoId);
            infEvento.setCOrgao(config.getEstado().getCodigoUF());
            infEvento.setTpAmb(config.getAmbiente().getCodigo());
            infEvento.setCNPJ("09109848000128");
            infEvento.setChMDFe(chave);
            infEvento.setDhEvento(XmlMdfeUtil.dataMdfe(LocalDateTime.now()));
            infEvento.setTpEvento(tpEvento);
            infEvento.setNSeqEvento(sequencia);
            infEvento.setDetEvento(detEvento);

            TEvento evento = new TEvento();
            evento.setInfEvento(infEvento);
            evento.setVersao(ConstantesUtil.VERSAO.MDFE);

            TRetEvento retorno = EncerrarMDFe.eventoEncerramento(config, evento, true);

            if (StatusMdfeEnum.EVENTO_VINCULADO.getCodigo().equals(retorno.getInfEvento().getCStat())) {
                log.info("Status: " + retorno.getInfEvento().getCStat());
                log.info("Motivo: " + retorno.getInfEvento().getXMotivo());
                log.info("Protocolo: " + retorno.getInfEvento().getNProt());
            } else {
                log.info("Status: " + retorno.getInfEvento().getCStat());
                log.info("Motivo: " + retorno.getInfEvento().getXMotivo());
            }

        } catch (MdfeException | FileNotFoundException | CertificadoException e) {
            log.log(Level.SEVERE, "Erro ao cancelar MDFe", e);
        }
    }

    private static EvEncMDFe preencheObjetoEncMDFe(String protocolo, String cCodUfEnc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        EvEncMDFe enc = new EvEncMDFe();
        enc.setDescEvento("Encerramento");
        enc.setNProt(protocolo);
        enc.setDtEnc(formatter.format(LocalDate.now()));
        enc.setCUF(cCodUfEnc);
        enc.setCMun("3550308");

        return enc;
    }
}
