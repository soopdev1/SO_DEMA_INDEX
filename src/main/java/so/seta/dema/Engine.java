package so.seta.dema;

import java.io.File;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class Engine {

    public static final ResourceBundle conf = ResourceBundle.getBundle("conf.conf");
    public static final Logger logger = createLog("DEMA_INDEX");

    private String dirIn = "";
    private String dirOut = "";
    private Database db = null;
    private String lotto = "";
    private String endorse = "";
    private String codPrt = "";
    private String codAut = "";
    private String tipoDoc = "";
    private String tipoLav = "";
    private String originale = "";
    private String codBusta = "";
    private String corriere = "";
    private String dataCorriere = "";
    private String oraCorriere = "";
    private String dataBusta = "";
    private String oraBusta = "";
    private String dataLav = "";
    private String oraLav = "";
    private String dataOCR = null;
    private String oraOCR = null;
    private String dataOCRManuale = null;
    private String oraOCRManuale = null;
    private String totPag = "";
    private String pathFilePdf = "";
    private String scatola = "";
    private String utente = "";
    private String dalm = "";
    private String tvei = "";
    private String nome = "";
    private String cognome = "";
    private String cap = "";
    private String csz = "";
    private String cod9899 = "";
    private String dataScansione = "";
    private String oraScansione = "";
    private String dataInizioLavorazione = "";
    private String oraInizioLavorazione = "";
    private String dataFineLavorazione = "";
    private String oraFineLavorazione = "";
    private String indici = "";
    private String pompator = "";

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    String cartella = formatter.format(date);

    private static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "IMPOSSIBILE LEGGERE FILE: {0}", file.getPath());
            logger.severe(estraiEccezione(ex));
        }
        return null;
    }

    public Engine() {

    }

    public void run() {

        this.db = new Database();
        this.dirIn = this.db.getPath("PathArchivio");
        this.dirOut = this.db.getPath("PathOut");

        try {

            ResultSet rs = this.db.getPratiche0();

            while (rs.next()) {
                this.lotto = rs.getString("lotto");
                this.endorse = rs.getString("endorse");
                this.codPrt = rs.getString("codPrt");
                this.codAut = rs.getString("codAut");
                this.tipoDoc = rs.getString("tipoDoc");
                this.tipoLav = rs.getString("tipoLav");
                this.originale = rs.getString("originale");
                this.codBusta = rs.getString("codBusta");
                this.corriere = rs.getString("corriere");
                this.dataCorriere = rs.getString("dataCorriere");
                this.oraCorriere = rs.getString("oraCorriere");
                this.dataBusta = rs.getString("dataBusta");
                this.oraBusta = rs.getString("oraBusta");
                this.dataLav = rs.getString("dataLav");
                this.oraLav = rs.getString("oraLav");
                if (rs.getString("dataOCR").equals("null")) {
                    this.dataOCR = null;
                } else {
                    this.dataOCR = rs.getString("dataOCR");
                }
                if (rs.getString("oraOCR").equals("null")) {
                    this.oraOCR = null;
                } else {
                    this.oraOCR = rs.getString("oraOCR");
                }
                if (rs.getString("dataOCRManuale").equals("null")) {
                    this.dataOCRManuale = null;
                } else {
                    this.dataOCRManuale = rs.getString("dataOCRManuale");
                }
                if (rs.getString("oraOCRManuale").equals("null")) {
                    this.oraOCRManuale = null;
                } else {
                    this.oraOCRManuale = rs.getString("oraOCRManuale");
                }
                this.totPag = rs.getString("pagine");
                this.pathFilePdf = rs.getString("nomeFile");
                this.scatola = rs.getString("scatola");
                this.utente = rs.getString("utente");
                this.dalm = rs.getString("dalm");
                this.tvei = rs.getString("tvei");
                this.nome = rs.getString("nome");
                this.cognome = rs.getString("cognome");
                this.cap = rs.getString("cap");
                this.csz = rs.getString("csz");
                this.cod9899 = rs.getString("cod9899");
                this.dataScansione = rs.getString("dataScansione");
                this.oraScansione = rs.getString("oraScansione");
                this.dataInizioLavorazione = rs.getString("dataInizioLavorazione");
                this.oraInizioLavorazione = rs.getString("oraInizioLavorazione");
                this.dataFineLavorazione = rs.getString("dataFineLavorazione");
                this.oraFineLavorazione = rs.getString("oraFineLavorazione");
                this.indici = rs.getString("indici");
                this.pompator = rs.getString("pompator");

                String pathOut = this.dirOut + File.separatorChar + this.endorse + File.separatorChar;
                new File(pathOut).mkdirs();
                boolean responsePDF = PdfGen.writePdf(this.dirIn + File.separatorChar + "pratiche" + File.separatorChar + this.dataLav + File.separatorChar + this.endorse + ".tif",
                        pathOut + this.endorse + this.tipoDoc + ".pdf", this.indici);
                if (responsePDF) {
                    if ((this.db.insertIndicizzata(this.lotto, this.endorse, this.codPrt, this.codAut, this.tipoDoc, this.tipoLav, this.originale, this.codBusta, this.corriere, this.dataCorriere, this.oraCorriere, this.dataBusta, this.oraBusta, this.dataLav, this.oraLav, this.dataOCR, this.oraOCR, this.dataOCRManuale, this.oraOCRManuale, this.totPag, this.pathFilePdf, this.scatola, this.utente, this.dalm, this.tvei, this.nome, this.cognome, this.cap, this.csz, this.cod9899, this.dataScansione, this.oraScansione, this.dataInizioLavorazione, this.oraInizioLavorazione, this.dataFineLavorazione, this.oraFineLavorazione, this.indici, this.pompator)) || (this.db.isIndicizzata(this.endorse, this.tipoDoc))) {
                        FactWS serv = new FactWS();
                        SOFACTWS ffws = serv.getSOFACTWSPort();
                        Pratica pratica = new Pratica();
                        pratica.setIddocumento(this.endorse + this.tipoDoc);
                        pratica.setEndorse(this.endorse);
                        pratica.setCodPrt(this.codPrt);
                        pratica.setCodAut(this.codAut);
                        pratica.setTipoDoc(this.tipoDoc);
                        pratica.setTipoLav(this.tipoLav);
                        pratica.setOriginale(this.originale);
                        pratica.setCodBusta(this.codBusta);
                        pratica.setCorriere(this.corriere);
                        pratica.setDataCorriere(this.dataCorriere);
                        pratica.setOraCorriere(this.oraCorriere);
                        pratica.setDataBusta(this.dataBusta);
                        pratica.setOraBusta(this.oraBusta);
                        pratica.setDataLav(this.dataLav);
                        pratica.setOraLav(this.oraLav);
                        pratica.setDataOCR(this.dataOCR);
                        pratica.setOraOCR(this.oraOCR);
                        pratica.setDataOCRManuale(this.dataOCRManuale);
                        pratica.setOraOCRManuale(this.oraOCRManuale);
                        pratica.setPagine(this.totPag);
                        pratica.setNomeFile(db.getPath("destNewFact") + cartella + "/" + this.endorse + this.tipoDoc + ".pdf");
                        pratica.setScatola(this.scatola);
                        pratica.setUtente(this.utente);
                        pratica.setDalm(this.dalm);
                        pratica.setLotto(this.lotto);
                        pratica.setTvei(this.tvei);
                        pratica.setNome("");
                        pratica.setCognome("");
                        pratica.setCap(this.cap);
                        pratica.setCsz(this.csz);
                        pratica.setCod9899(this.cod9899);
                        pratica.setDataScansione(this.dataScansione);
                        pratica.setOraScansione(this.oraScansione);
                        pratica.setDataInizioLavorazione(this.dataInizioLavorazione);
                        pratica.setOraInizioLavorazione(this.oraInizioLavorazione);
                        pratica.setDataFineLavorazione(this.dataFineLavorazione);
                        pratica.setOraFineLavorazione(this.oraFineLavorazione);
                        pratica.setLavorazione("DEMA");
                        pratica.setBarcode("");
                        pratica.setEsitoconformita("DE");

                        String base64 = encodeFileToBase64(new File(pathOut + this.endorse + this.tipoDoc + ".pdf"));
                        if (base64 != null) {
                            logger.warning(ffws.getErroriWebServices(ffws.inserisciPratica(pratica, base64,
                                    cartella, this.endorse + this.tipoDoc + ".pdf")));

                            this.db.eliminaPratica(this.endorse, this.tipoDoc);
                        }
                    }
                }
                logger.log(Level.INFO, "File: {0} --> creazione pdf = {1}", new Object[]{new File(this.pathFilePdf).getName(), responsePDF});
            }
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        this.db.closeDB();
    }

    public static void main(String args[]) {
        new Engine().run();
    }

    private static Logger createLog(String appl) {
        try {
            Date d = new Date();
            String dataOdierna = (new SimpleDateFormat("ddMMyyyy")).format(d);
            String ora = (new SimpleDateFormat("HHmmss")).format(d);
            File dir1 = new File(conf.getString("path.log"));
            dir1.mkdirs();
            File dir2 = new File(dir1.getPath() + File.separator + dataOdierna);
            dir2.mkdirs();
            Logger log = Logger.getLogger(appl);
            FileHandler fileTxt = new FileHandler(dir2.getPath() + File.separator + appl + "_" + ora + ".log");
            SimpleFormatter formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            log.addHandler(fileTxt);
            return log;
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public static String estraiEccezione(Exception ec1) {
        try {
            return ec1.getStackTrace()[0].getMethodName() + " - " + getStackTrace(ec1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ec1.getMessage();

    }
}
