package so.seta.dema;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFImage;
import org.faceless.pdf2.PDFImageSet;
import org.faceless.pdf2.PDFPage;


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

    public static boolean writePdf(String pathin, String pathout, String indici) {
        try {
            indici = indici.trim();
            PDF pdf = new PDF();
            File tif = new File(pathin);
            File pdf1 = new File(generaId() + ".pdf");
            File pdf2 = new File(generaId() + "2.pdf");
            try (InputStream in = new FileInputStream(tif)) {
                PDFImageSet imgs = new PDFImageSet(in);
                if (indici.equals("")) {
                    for (int j = 0; j < imgs.getNumImages(); j++) {
                        PDFImage img = imgs.getImage(j);
                        float w = img.getWidth();
                        float h = img.getHeight();
                        PDFPage page = pdf.newPage((int) w, (int) h);
                        page.drawImage(img, 0, 0, w, h);
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(indici, ";");
                    if (st.hasMoreTokens()) {
                        while (st.hasMoreTokens()) {
                            PDFImage img = imgs.getImage(Integer.parseInt(st.nextToken()));
                            float w = img.getWidth();
                            float h = img.getHeight();
                            PDFPage page = pdf.newPage((int) w, (int) h);
                            page.drawImage(img, 0, 0, w, h);
                        }
                    } else {
                        PDFImage img = imgs.getImage(Integer.parseInt(indici));
                        float w = img.getWidth();
                        float h = img.getHeight();
                        PDFPage page = pdf.newPage((int) w, (int) h);
                        page.drawImage(img, 0, 0, w, h);
                    }
                }
            }
            try (OutputStream fo = new FileOutputStream(pdf1)) {
                pdf.render(fo);
            }
            try (PDDocument doc = PDDocument.load(pdf1)) {
                List pages = doc.getDocumentCatalog().getAllPages();
                for (int i = 0; i < pages.size(); i++) {
                    PDPage page1 = (PDPage) pages.get(i);
                    PDFStreamParser parser = new PDFStreamParser(
                            page1.getContents());
                    parser.parse();
                    List tokens = parser.getTokens();
                    int indexremove = 99999999;
                    boolean found = false;
                    for (int j = 0; j < tokens.size() & !found; j++) {
                        Object token = tokens.get(j);
                        if (token instanceof COSArray) {
                            COSArray cos = (COSArray) token;
                            COSString st = (COSString) cos.get(0);
                            String s = st.getString();
                            if (s.equalsIgnoreCase("DEMO")) {
                                indexremove = j;
                                found = true;
                            }
                        }
                    }
                    PDStream newContents = new PDStream(doc);
                    ContentStreamWriter writer = new ContentStreamWriter(
                            newContents.createOutputStream());
                    if (indexremove != 99999999) {
                        tokens.remove(indexremove);
                    }
                    writer.writeTokens(tokens);
                    newContents.addCompression();
                    page1.setContents(newContents);
                }
                doc.save(pdf2);
            }
            pdf1.delete();

            File pdfOut = new File(pathout);
            if (copy(pdf2, pdfOut)) {
                pdf2.delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean copy(File srcFile, File destFile) {
        try (InputStream oInStream = new FileInputStream(srcFile); OutputStream oOutStream = new FileOutputStream(destFile)) {
            byte[] oBytes = new byte[2048];
            int nLength;
            BufferedInputStream oBuffInputStream = new BufferedInputStream(oInStream);
            while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
                oOutStream.write(oBytes, 0, nLength);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    

}
