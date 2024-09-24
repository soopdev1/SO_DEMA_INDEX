package so.seta.dema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import static so.seta.dema.Engine.conf;
import static so.seta.dema.Engine.estraiEccezione;
import static so.seta.dema.Engine.logger;

public class Database {

    private Connection conn = null;
    private final String user = conf.getString("db.user");
    private final String pwd = conf.getString("db.pass");
    private final String host = conf.getString("db.ip") + ":3306/findomestic";
    private final String drivername = conf.getString("db.driver");
    private final String typedb = conf.getString("db.tipo");

    public Database() {
        try {
            Class.forName(drivername).newInstance();
            Properties p = new Properties();
            p.put("user", user);
            p.put("password", pwd);
            p.put("useUnicode", "true");
            p.put("characterEncoding", "UTF-8");
            this.conn = DriverManager.getConnection("jdbc:" + typedb + "://" + host, p);
        } catch (Exception ex) {
            this.conn = null;
            logger.severe(estraiEccezione(ex));
        }
    }

    public Database(Connection conn) {
        try {
            this.conn = conn;
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
    }

    public Connection getConnectionDB() {
        return this.conn;
    }

    public void closeDB() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
    }

    public String getPath(String id) {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("Select url From path where id='"
                + id + "'")) {
            if (rs.next()) {
                return rs.getString("url");
            }
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return null;
    }

    public boolean isIndicizzata(String endorse, String tipoDoc) {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt
                .executeQuery("SELECT * FROM `findomestic`.`indicizzate` where endorse='"
                        + endorse + "' and tipoDoc='" + tipoDoc + "'")) {
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return false;
    }

    public ResultSet getPratiche0() {
        try {
            return this.conn.createStatement().executeQuery("Select * From coda order by data asc");
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return null;
    }

    public ResultSet getPratiche1() {
        try {
            return this.conn.createStatement().executeQuery("Select * From coda where RIGHT(endorse,1)%3=1 order by data asc limit 100");
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return null;
    }

    public ResultSet getPratiche2() {
        try {
            return this.conn.createStatement().executeQuery("Select * From coda where RIGHT(endorse,1)%3=2 order by data asc limit 100");
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return null;
    }

    public int numeroPratiche(String operatore) {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt
                .executeQuery("Select count(*) From coda where utente='"
                        + operatore + "'")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return 0;
    }

    public int eliminaPratica(String endorse, String tipoDoc) {
        try (Statement stmt = this.conn.createStatement()) {
            return stmt.executeUpdate("delete From coda where endorse='" + endorse + "' and tipoDoc='" + tipoDoc + "'");
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return -1;
    }

    public boolean insertIndicizzata(String lotto, String endorse, String codPrt, String codAut, String tipoDoc, String tipoLav, String originale, String codBusta, String corriere, String dataCorriere, String oraCorriere, String dataBusta, String oraBusta, String dataLav, String oraLav, String dataOCR, String oraOCR, String dataOCRManuale, String oraOCRManuale, String pagine, String nomeFile, String scatola, String utente, String dalm, String tvei, String nome, String cognome, String cap, String csz, String cod9899, String dataScansione, String oraScansione, String dataInizioLavorazione, String oraInizioLavorazione, String dataFineLavorazione, String oraFineLavorazione, String indici, String pompator) {
        try {
            try (Statement stmt = this.conn.createStatement()) {
                stmt.executeUpdate("start transaction");
                
                ResultSet rs = stmt.executeQuery("Select * From pratica Where endorse ='"
                        + endorse + "' and pompator = '" + pompator + "'");
                if (!rs.next()) {
                    stmt.executeUpdate("insert into pratica (endorse, codPrt, codAut, tipoLav, codBusta, corriere, scatola, utente, dalm, lotto,pompator) values ('"
                            + endorse
                            + "','"
                            + codPrt
                            + "','"
                            + codAut
                            + "','"
                            + tipoLav
                            + "','"
                            + codBusta
                            + "','"
                            + corriere
                            + "','"
                            + scatola
                            + "','"
                            + utente
                            + "','"
                            + dalm
                            + "','"
                            + lotto
                            + "','"
                            + pompator + "')");
                    stmt.executeUpdate("insert into cronologia ( endorse, dataCorriere, oraCorriere, dataBusta, oraBusta, dataLav, oraLav, dataOCR, oraOCR, dataOCRManuale, oraOCRManuale, dataScansione,oraScansione, dataInizioLavorazione, oraInizioLavorazione, dataFineLavorazione,  oraFineLavorazione,pompator) values ('"
                            + endorse
                            + "', '"
                            + dataCorriere
                            + "', '"
                            + oraCorriere
                            + "', '"
                            + dataBusta
                            + "', '"
                            + oraBusta
                            + "', '"
                            + dataLav
                            + "','"
                            + oraLav
                            + "', '"
                            + dataOCR
                            + "', '"
                            + oraOCR
                            + "', '"
                            + dataOCRManuale
                            + "', '"
                            + oraOCRManuale
                            + "','"
                            + dataScansione
                            + "','"
                            + oraScansione
                            + "', '"
                            + dataInizioLavorazione
                            + "', '"
                            + oraInizioLavorazione
                            + "','"
                            + dataFineLavorazione
                            + "', '"
                            + oraFineLavorazione + "','" + pompator + "')");
                    if ((!tvei.trim().equals(""))
                            && (!nome.trim().equals(""))
                            && (!cognome.trim().equals(""))
                            && (!cap.trim().equals("")) && (!csz.trim().equals(""))) {
                        stmt.executeUpdate("insert into coda9899 ( endorse, tvei, nome, cognome, cap, csz, cod9899, pompator) values ('"
                                + endorse
                                + "','"
                                + tvei
                                + "', '"
                                + nome.replace("'", "''")
                                + "', '"
                                + cognome.replace("'", "''")
                                + "', '"
                                + cap
                                + "', '"
                                + csz
                                + "', '"
                                + cod9899
                                + "','"
                                + pompator + "')");
                    }
                }
                rs
                        = stmt.executeQuery("Select * From documento Where endorse ='"
                                + endorse + "' and tipoDoc = '" + tipoDoc
                                + "' and pompator = '" + pompator + "'");
                if (!rs.next()) {
                    stmt.executeUpdate("insert into documento (endorse, tipoDoc, originale,pagine, nomeFile, indici,pompator) values ('"
                            + endorse
                            + "', '"
                            + tipoDoc
                            + "', '"
                            + originale
                            + "','"
                            + pagine
                            + "', '"
                            + nomeFile.replace("\\", "\\\\")
                            + "','"
                            + indici + "','" + pompator + "')");
                }
                stmt.executeUpdate("commit");
                
                stmt
                        .executeUpdate("insert into indicizzate (lotto,  endorse,  codPrt,  codAut,  tipoDoc,  tipoLav,  originale, codBusta,corriere,  dataCorriere,  oraCorriere, dataBusta,  oraBusta,  dataLav,oraLav,  dataOCR,  oraOCR,  dataOCRManuale,  oraOCRManuale,pagine,  nomeFile,  scatola,  utente, dalm,  tvei,nome,  cognome,  cap, csz,  cod9899,  dataScansione,oraScansione,  dataInizioLavorazione,  oraInizioLavorazione,dataFineLavorazione,  oraFineLavorazione,  indici, data, ora) values ('"
                                + lotto
                                + "', '"
                                + endorse
                                + "', '"
                                + codPrt
                                + "', '"
                                + codAut
                                + "', "
                                        + "'"
                                + tipoDoc
                                + "', '"
                                + tipoLav
                                + "', '"
                                + originale
                                + "', '"
                                + codBusta
                                + "',"
                                        + "'"
                                + corriere
                                + "', '"
                                + dataCorriere
                                + "', '"
                                + oraCorriere
                                + "', '"
                                + dataBusta
                                + "', '"
                                + oraBusta
                                + "', '"
                                + dataLav
                                + "',"
                                        + "'"
                                + oraLav
                                + "', '"
                                + dataOCR
                                + "', '"
                                + oraOCR
                                + "', '"
                                + dataOCRManuale
                                + "', '"
                                + oraOCRManuale
                                + "',"
                                        + "'"
                                + pagine
                                + "', '"
                                + nomeFile.replace("\\", "\\\\")
                                + "', '"
                                + scatola
                                + "', '"
                                + utente
                                + "', '"
                                + dalm
                                + "', '"
                                + tvei
                                + "',"
                                        + "'"
                                + nome.replace("'", "''")
                                + "', '"
                                + cognome.replace("'", "''")
                                + "', '"
                                + cap
                                + "', '"
                                + csz
                                + "', '"
                                + cod9899
                                + "', '"
                                + dataScansione
                                + "',"
                                        + "'"
                                + oraScansione
                                + "', '"
                                + dataInizioLavorazione
                                + "', '"
                                + oraInizioLavorazione
                                + "',"
                                        + "'"
                                + dataFineLavorazione
                                + "', '"
                                + oraFineLavorazione
                                + "','"
                                + indici
                                + "','"
                                + new SimpleDateFormat("ddMMyyyy")
                                        .format(new Date())
                                + "','"
                                + new SimpleDateFormat("HHmmss").format(new Date())
                                + "')");
            }

            return true;
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
        }
        return false;
    }
}
