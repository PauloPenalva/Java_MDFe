package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.enums.EventosMdfeEnum;
import br.com.idxtec.mdfe.eventos.CancelarMDFe;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.eventos.EvCancMDFe;
import br.com.idxtec.mdfe.schemas.eventos.TEvento;
import br.com.idxtec.mdfe.schemas.eventos.TRetEvento;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.ObjetoMDFeUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import lombok.extern.java.Log;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.logging.Level;

import static java.util.Optional.ofNullable;

@Log
public class TesteEvCancMdfe {

    public static void main(String[] args) {

        try {
            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            String protocolo = "935240000047593";
            String chave = "35240709109848000128580010000000051225216329";
            String sequencia = "01";
            String tpEvento = EventosMdfeEnum.CANCELAR.getCodigo();

            String EventoId = "ID" + tpEvento + chave + sequencia;

            TEvento.InfEvento.DetEvento detEvento = new TEvento.InfEvento.DetEvento();
            detEvento.setAny(ObjetoMDFeUtil.objectToElement(
                    preencheObjetoCancMDFe(protocolo),
                    EvCancMDFe.class,
                    "evCancMDFe"));
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

            TRetEvento retorno = Mdfe.eventoCancelar(config, evento);

            log.info("Status: " + retorno.getInfEvento().getCStat());
            log.info("Motivo: " + retorno.getInfEvento().getXMotivo());
            log.info("Protocolo: " + ofNullable(retorno.getInfEvento().getNProt()).orElse(""));


        } catch (MdfeException | FileNotFoundException | CertificadoException e) {
            log.log(Level.SEVERE, "Erro ao cancelar MDFe", e);
        }
    }

    private static EvCancMDFe preencheObjetoCancMDFe(String protocolo) {
        EvCancMDFe canc = new EvCancMDFe();
        canc.setDescEvento("Cancelamento");
        canc.setNProt(protocolo);
        canc.setXJust("Cancelamento por erro de digitação");

        return canc;
    }
}
