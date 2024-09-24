package so.seta.dema;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import static so.seta.dema.Engine.estraiEccezione;
import static so.seta.dema.Engine.logger;

public class PdfGen {

    public static String generaId() {
        SimpleDateFormat sd = new SimpleDateFormat("yyMMddHHmmssSSS");
        String val = sd.format(new Date());
        String random = RandomStringUtils.randomAlphanumeric(5).trim();
        return val + random;
    }

    public static String fillString(String in, int totalSize,
            String fillChar, String orientation) {
        String fillString = "";
        if (totalSize > in.length()) {
            for (int i = 0; i < totalSize - in.length(); i++) {
                fillString = fillString + fillChar;
            }
            if (orientation.toUpperCase().equals("SX")) {
                return fillString + in;
            }
            if (orientation.toUpperCase().equals("DX")) {
                return in + fillString;
            }
        }
        return in;
    }

    public static boolean convertTifToPDF(String fileIn, String fileOut) {
        try {
            RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(fileIn);
            int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);
            Document TifftoPDF = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(TifftoPDF, new FileOutputStream(fileOut));
            pdfWriter.setStrictImageSequence(true);
            TifftoPDF.open();
            for (int i = 1; i <= numberOfPages; i++) {
                Image tempImage = TiffImage.getTiffImage(myTiffFile, i);
                Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
                TifftoPDF.setPageSize(pageSize);
                TifftoPDF.newPage();
                TifftoPDF.add((Element) tempImage);
            }
            TifftoPDF.close();
            return true;
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
            return false;
        }
    }

    public static boolean writePdf(String fileIn, String fileOut, String indici) {
        try {
            if (indici.trim().equals("0")) {
                return convertTifToPDF(fileIn, fileOut);
            }
            String[] pages = indici.split(";");
            if (pages.length > 0) {
                RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(fileIn);
                Document TifftoPDF = new Document();
                PdfWriter pdfWriter = PdfWriter.getInstance(TifftoPDF, new FileOutputStream(fileOut));
                pdfWriter.setStrictImageSequence(true);
                TifftoPDF.open();
                for (String page : pages) {
                    Image tempImage = TiffImage.getTiffImage(myTiffFile, Integer.parseInt(page) + 1);
                    Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
                    TifftoPDF.setPageSize(pageSize);
                    TifftoPDF.newPage();
                    TifftoPDF.add((Element) tempImage);
                }
                TifftoPDF.close();
            }
            return true;
        } catch (Exception ex) {
            logger.severe(estraiEccezione(ex));
            return false;
        }
    }

}
