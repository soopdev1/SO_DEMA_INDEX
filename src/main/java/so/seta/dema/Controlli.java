package so.seta.dema;

import javax.swing.JOptionPane;
import static so.seta.dema.Engine.logger;

public class Controlli {

    private boolean check14(String str) {
        if (str.length() < 14) {
            logger.severe("Il campo codice ha un numero di caratteri inferiore a 14");
            return false;
        }
        if (str.length() > 14) {
            logger.severe("Il campo codice ha un numero di caratteri maggiore di 14");
            return false;
        }
        return true;
    }

    private boolean checkPRT(String str) {
        if ((str.substring(0, 2).equals("10")) || (str.substring(0, 2).equals("20"))) {
            return true;
        }
        logger.severe("Il codice Pratica è errato.");
        return false;
    }

    private boolean checkCIR(String str) {
        if ((str.substring(0, 5).equals("60000")) || (str.substring(0, 5).equals("61000")) || (str.substring(0, 5).equals("62000")) || (str.substring(0, 5).equals("50000"))) {
            return true;
        }
        logger.severe("Il codice CIR è errato.");
        return false;
    }

    public boolean check(String codPrt, String codAut) {
        if (!codPrt.trim().equals("")) {
            if ((check14(codPrt)) && (checkPRT(codPrt))) {
                return true;
            }
        } else if (!codAut.trim().equals("")) {
            if ((check14(codAut)) && (checkCIR(codAut))) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}
