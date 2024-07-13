package br.com.idxtec.mdfe;

import br.com.idxtec.mdfe.dom.ConfiguracoesMdfe;
import br.com.idxtec.mdfe.enums.AmbienteEnum;
import br.com.idxtec.mdfe.enums.EstadosEnum;
import br.com.idxtec.mdfe.enums.StatusMdfeEnum;
import br.com.idxtec.mdfe.exceptions.MdfeException;
import br.com.idxtec.mdfe.schemas.modal.rodo.Rodo;
import br.com.idxtec.mdfe.schemas.recepcao.*;
import br.com.idxtec.mdfe.util.ChaveUtil;
import br.com.idxtec.mdfe.util.ConstantesUtil;
import br.com.idxtec.mdfe.util.ObjetoMDFeUtil;
import br.com.idxtec.mdfe.util.XmlMdfeUtil;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import lombok.extern.java.Log;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;

@Log
public class TesteEnvioMdfe {


    public static void main(String[] args) {

        try {
            ConfiguracoesMdfe config = TesteConfig.iniciaConfiguracoes(EstadosEnum.SP, AmbienteEnum.HOMOLOGACAO);

            String cnpj = "09109848000128";
            int serie = 1;
            int numero = 4;

            TMDFe mdfe = preencheObjetoMdfe(config, cnpj, serie, numero);
            mdfe = Mdfe.montaMDFe(config, mdfe, true);

            TMDFe.InfMDFeSupl infMDFeSupl = new TMDFe.InfMDFeSupl();
            infMDFeSupl.setQrCodMDFe(
                    ObjetoMDFeUtil.criaQRCode(mdfe.getInfMDFe().getId().substring(4), config)
            );

            mdfe.setInfMDFeSupl(infMDFeSupl);

            TRetMDFe retorno = Mdfe.enviar(config, mdfe);

            log.info("Status: " + retorno.getCStat() + " - " + retorno.getXMotivo());

            if (retorno.getCStat().equals(StatusMdfeEnum.AUTORIZADO.getCodigo())) {
                log.info("Protocolo: " + retorno.getProtMDFe().getInfProt().getNProt());
                log.info("XML Final: " + XmlMdfeUtil.criaMdfeProc(mdfe, retorno.getProtMDFe()));
            }

        } catch (MdfeException | CertificadoException | FileNotFoundException | JAXBException e) {
            log.log(Level.SEVERE, "Erro ao enviar MDFe", e);
        }

    }

    private static TMDFe preencheObjetoMdfe(ConfiguracoesMdfe config, String cnpj, int serie, int numero) throws MdfeException {

        String modelo = "58";
        String tipoEmissao = "1";
        String cct = String.format("%08d", new Random().nextInt(99999999));

        ChaveUtil chaveUtil = new ChaveUtil(config.getEstado(),
                cnpj, modelo, serie, numero,
                tipoEmissao, cct, LocalDateTime.now());

        String chave = chaveUtil.getChaveCT();
        String dv = chaveUtil.getDigitoVerificador();

        TMDFe.InfMDFe infMDFe = new TMDFe.InfMDFe();
        infMDFe.setVersao(ConstantesUtil.VERSAO.MDFE);
        infMDFe.setId(chave);

        infMDFe.setIde(preencheObjetoIde(config, modelo, String.valueOf(serie), String.valueOf(numero), cct, dv));
        infMDFe.setEmit(preencheObjetoEmit());
        infMDFe.setInfModal(preencheObjetoInfModal());
        infMDFe.setInfDoc(preencheObjetoInfDoc());
        infMDFe.getSeg().add(preencheObjetoSeg());
        infMDFe.setProdPred(preencheObjetoProdPred());
        infMDFe.setTot(preencheObjetoTot());
        infMDFe.getAutXML().add(preencheObjetoAutXML());
        infMDFe.setInfAdic(preencheObjetoInfAdic());

        infMDFe.setInfRespTec(preencheObjetoRespTec());

        TMDFe mdfe = new TMDFe();
        mdfe.setInfMDFe(infMDFe);

        return mdfe;
    }


    private static TMDFe.InfMDFe.ProdPred preencheObjetoProdPred() {
        TMDFe.InfMDFe.ProdPred prodPred = new TMDFe.InfMDFe.ProdPred();

        /**
         01-Granel sólido;
         02-Granel líquido;
         03-Frigorificada;
         04-Conteinerizada;
         05-Carga Geral;
         06-Neogranel;
         07-Perigosa (granel sólido);
         08-Perigosa (granel líquido);
         09-Perigosa (carga frigorificada);
         10-Perigosa (conteinerizada);
         11-Perigosa (carga geral).
         */
        prodPred.setTpCarga("05");
        prodPred.setXProd("PRODUTO TESTE");
        prodPred.setInfLotacao(preencheObjetoInfLotacao());
        return prodPred;
    }

    private static TMDFe.InfMDFe.ProdPred.InfLotacao preencheObjetoInfLotacao() {
        TMDFe.InfMDFe.ProdPred.InfLotacao infLotacao = new TMDFe.InfMDFe.ProdPred.InfLotacao();
        infLotacao.setInfLocalCarrega(preencheObjetoInfLocalCarrega());
        infLotacao.setInfLocalDescarrega(preencheObjetoInfLocalDescarrega());
        return infLotacao;
    }

    private static TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalCarrega preencheObjetoInfLocalCarrega() {
        TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalCarrega infLocalCarrega = new TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalCarrega();
        infLocalCarrega.setCEP("01001000");
        return infLocalCarrega;
    }

    private static TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalDescarrega preencheObjetoInfLocalDescarrega() {
        TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalDescarrega infLocalDescarrega = new TMDFe.InfMDFe.ProdPred.InfLotacao.InfLocalDescarrega();
        infLocalDescarrega.setCEP("18212600");
        return infLocalDescarrega;
    }

    private static TMDFe.InfMDFe.InfAdic preencheObjetoInfAdic() {
        TMDFe.InfMDFe.InfAdic infAdic = new TMDFe.InfMDFe.InfAdic();
        infAdic.setInfCpl("MDF-E EMITIDO EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        infAdic.setInfAdFisco("MDF-E EMITIDO EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        return infAdic;
    }

    private static TMDFe.InfMDFe.AutXML preencheObjetoAutXML() {
        TMDFe.InfMDFe.AutXML autXML = new TMDFe.InfMDFe.AutXML();
        autXML.setCNPJ("09109848000128");
        return autXML;
    }

    private static TMDFe.InfMDFe.Tot preencheObjetoTot() {
        TMDFe.InfMDFe.Tot tot = new TMDFe.InfMDFe.Tot();
        tot.setQCTe("1");
        tot.setQCarga("1000");
        tot.setCUnid("01"); // 01 - KG 02 - TON
        tot.setVCarga("1000.00");
        return tot;
    }

    private static TMDFe.InfMDFe.Seg preencheObjetoSeg() {
        TMDFe.InfMDFe.Seg seg = new TMDFe.InfMDFe.Seg();
        seg.setNApol("1234567890");
        seg.getNAver().add("1234567890");
        seg.setInfResp(preencheObjetoInfResp());
        seg.setInfSeg(preencheObjetoInfSeg());
        return seg;
    }

    private static TMDFe.InfMDFe.Seg.InfSeg preencheObjetoInfSeg() {
        TMDFe.InfMDFe.Seg.InfSeg infSeg = new TMDFe.InfMDFe.Seg.InfSeg();
        infSeg.setCNPJ("09109848000128");
        infSeg.setXSeg("SEGURADORA XYZ");
        return infSeg;
    }

    private static TMDFe.InfMDFe.Seg.InfResp preencheObjetoInfResp() {
        TMDFe.InfMDFe.Seg.InfResp infResp = new TMDFe.InfMDFe.Seg.InfResp();
        infResp.setRespSeg("1");
        infResp.setCNPJ("09109848000128");
        return infResp;
    }

    private static TMDFe.InfMDFe.InfDoc preencheObjetoInfDoc() {
        TMDFe.InfMDFe.InfDoc infDoc = new TMDFe.InfMDFe.InfDoc();
        infDoc.getInfMunDescarga().add(preencheObjetoInfMunDescarga());
        return infDoc;
    }

    private static TMDFe.InfMDFe.InfDoc.InfMunDescarga preencheObjetoInfMunDescarga() {
        TMDFe.InfMDFe.InfDoc.InfMunDescarga infMunDescarga = new TMDFe.InfMDFe.InfDoc.InfMunDescarga();
        infMunDescarga.setCMunDescarga("3550308");
        infMunDescarga.setXMunDescarga("SAO PAULO");
        infMunDescarga.getInfCTe().add(preencheObjetoInfCTe());
        return infMunDescarga;
    }

    private static TMDFe.InfMDFe.InfDoc.InfMunDescarga.InfCTe preencheObjetoInfCTe() {
        TMDFe.InfMDFe.InfDoc.InfMunDescarga.InfCTe infCTe = new TMDFe.InfMDFe.InfDoc.InfMunDescarga.InfCTe();
        infCTe.setChCTe("35240709109848000128570020000635101064280824");
        return infCTe;
    }

    private static TMDFe.InfMDFe.InfModal preencheObjetoInfModal() throws MdfeException {
        TMDFe.InfMDFe.InfModal infModal = new TMDFe.InfMDFe.InfModal();
        infModal.setVersaoModal(ConstantesUtil.VERSAO.MDFE);
        infModal.setAny(ObjetoMDFeUtil.objectToElement(preencheObjetoRodo(), Rodo.class, "rodo"));
        return infModal;
    }

    private static Rodo preencheObjetoRodo() {
        Rodo rodo = new Rodo();
        rodo.setInfANTT(preencheObjetoInfANTT());
        rodo.setVeicTracao(preencheObjetoVeicTracao());
        return rodo;
    }

    private static Rodo.VeicTracao preencheObjetoVeicTracao() {
        Rodo.VeicTracao veicTracao = new Rodo.VeicTracao();
        veicTracao.setPlaca("AAA0002");
        veicTracao.setTara("10000");
        veicTracao.getCondutor().add(preencheObjetoCondutor());
        veicTracao.setTpRod("01");
        veicTracao.setTpCar("02");
        return veicTracao;
    }

    private static Rodo.VeicTracao.Condutor preencheObjetoCondutor() {
        Rodo.VeicTracao.Condutor condutor = new Rodo.VeicTracao.Condutor();
        condutor.setXNome("JOSE DA SILVA");
        condutor.setCPF("14888965005");
        return condutor;
    }

    private static Rodo.InfANTT preencheObjetoInfANTT() {
        Rodo.InfANTT infANTT = new Rodo.InfANTT();
        infANTT.setRNTRC("11382119");
        infANTT.getInfContratante().add(preencheObjetoInfContratante());
        return infANTT;
    }

    private static Rodo.InfANTT.InfContratante preencheObjetoInfContratante() {
        Rodo.InfANTT.InfContratante infContratante = new Rodo.InfANTT.InfContratante();
        infContratante.setCNPJ("09109848000128");
        return infContratante;
    }

    private static TRespTec preencheObjetoRespTec() {
        TRespTec respTec = new TRespTec();
        respTec.setCNPJ("18802081000109");
        respTec.setXContato("PAULO ROGERIO PENALVA");
        respTec.setEmail("paulo.penalva@gmail.com");
        respTec.setFone("15997554275");

        return respTec;
    }

    private static TMDFe.InfMDFe.Emit preencheObjetoEmit() {
        TMDFe.InfMDFe.Emit emit = new TMDFe.InfMDFe.Emit();
        emit.setCNPJ("09109848000128");
        emit.setIE("371101850116");
        emit.setXNome("MDF-E EMITIDO EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        emit.setXFant("MDF-E EMITIDO EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        emit.setEnderEmit(preencheObjetoEnderEmit());
        return emit;
    }

    private static TEndeEmi preencheObjetoEnderEmit() {
        TEndeEmi enderEmit = new TEndeEmi();
        enderEmit.setXLgr("RUA TESTE");
        enderEmit.setNro("123");
        enderEmit.setXCpl("SALA 1");
        enderEmit.setXBairro("CENTRO");
        enderEmit.setCMun("3550308");
        enderEmit.setXMun("SAO PAULO");
        enderEmit.setCEP("01001000");
        enderEmit.setUF(TUf.SP);
        enderEmit.setFone("1122334455");
        enderEmit.setEmail("teste@email.com");

        return enderEmit;
    }

    private static TMDFe.InfMDFe.Ide preencheObjetoIde(ConfiguracoesMdfe config, String modelo, String serie, String numero, String cct, String dv) {
        TMDFe.InfMDFe.Ide ide = new TMDFe.InfMDFe.Ide();
        ide.setCUF(config.getEstado().getCodigoUF());
        ide.setTpAmb(config.getAmbiente().getCodigo());
        ide.setTpEmit("1");  // 1 - Prestador de serviço de transporte 2 - Transportador de Carga Própria 3 - Prestador de serviço de transporte que emitirá CTe Globalizado
        //ide.setTpTransp();
        ide.setMod(modelo);
        ide.setSerie(serie);
        ide.setNMDF(numero);
        ide.setCMDF(cct);
        ide.setCDV(dv);
        ide.setModal("1");  // RODOVIARIO
        ide.setDhEmi(XmlMdfeUtil.dataMdfe(LocalDateTime.now()));
        ide.setTpEmis("1");
        ide.setProcEmi("0");
        ide.setVerProc("E-FISCLOUD V.3.0.0");
        ide.setUFIni(TUf.SP);
        ide.setUFFim(TUf.SP);
        ide.getInfMunCarrega().add(preecheObjetoInfMunCarrega());
        //ide.getInfPercurso().add(preencheObjetoInfPercurso());
        return ide;
    }

    private static TMDFe.InfMDFe.Ide.InfMunCarrega preecheObjetoInfMunCarrega() {
        TMDFe.InfMDFe.Ide.InfMunCarrega infMunCarrega = new TMDFe.InfMDFe.Ide.InfMunCarrega();
        infMunCarrega.setCMunCarrega("3550308");
        infMunCarrega.setXMunCarrega("SAO PAULO");
        return infMunCarrega;
    }

    private static TMDFe.InfMDFe.Ide.InfPercurso preencheObjetoInfPercurso() {
        TMDFe.InfMDFe.Ide.InfPercurso infPercurso = new TMDFe.InfMDFe.Ide.InfPercurso();
        infPercurso.setUFPer(TUf.SP);
        return infPercurso;
    }
}
