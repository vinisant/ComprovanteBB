package com.comprovante.vinicius.comprovantebb;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;

import java.io.OutputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;


import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class ReadFilesReceipt {


    private static String FOLDER = "BB/comprovantes";
    private static String SSTRING = "Comprovante_";
    private static String ESTRING = ".pdf";
    private static String SAVEPATH = "ComprovantesBB";
    private static String CUTEDGE = "\n< >cortar aqui< > < > < > < > < > < > < > < > < \n";


    public static int COLUNS = 3;

    private File mem;

    File getFilePDF() {
        return fil;
    }

    private File fil;
    private File[] list;
    private List<Receipt> recp;


    ReadFilesReceipt(int sd, int sm, int sy, int ed, int em, int ey, Context ctx) {

        mem = Environment.getExternalStorageDirectory();
        mem = new File(mem, FOLDER);
        int start = sy * 10000 + sm * 100 + sd;
        int end = ey * 10000 + em * 100 + ed;


        if (mem.exists()) {

            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + SAVEPATH);
            if (!folder.exists())
                if(!folder.mkdirs()) {
                    Toast.makeText(ctx, "no folder created", Toast.LENGTH_LONG).show();
                    return;
                }


            FilenameFilter textFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(SSTRING) && name.endsWith(ESTRING);
                }
            };

            list = mem.listFiles(textFilter);
            recp = new LinkedList<>();


            int cd, cm, cy, current;
            String[] n;

            for (File aList : list) {
                n = aList.getName().substring(12, 22).split("-");

                cd = Integer.parseInt(n[0]);
                cm = Integer.parseInt(n[1]);
                cy = Integer.parseInt(n[2]);
                current = cy * 10000 + cm * 100 + cd;
                if (current >= start && current <= end) {
                    recp.add(new Receipt(aList, ctx));
                }

            }


            fil = new File(folder, start + "_" + end + ".pdf");


            Rectangle[] COLUMNS = {
                    new Rectangle(20, 15, 195, 832),
                    new Rectangle(205, 15, 390, 832),
                    new Rectangle(395, 15, 580, 832)
            };

            try {
                OutputStream file;
                file = new FileOutputStream(fil);
                Document document = new Document(PageSize.A4, 10, 10, 10, 10);
                PdfWriter writer = PdfWriter.getInstance(document, file);
                document.open();

                PdfContentByte canvas = writer.getDirectContent();
                ColumnText ct = new ColumnText(canvas);


                LinkedList<Receipt> l0 = new LinkedList<>();
                LinkedList<Receipt> l1 = new LinkedList<>();
                LinkedList<Receipt> l2 = new LinkedList<>();

                find(l0,l1,l2);

                int side_of_the_page = 0, linesCount = 0;
                ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                boolean flagNewPage = false;

                for (Receipt r : l0) {
                    if(linesCount + r.getLinesnum() >= 81){
                        linesCount = 0;
                        document.newPage();
                        flagNewPage = true;
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+CUTEDGE),
                            FontFactory.getFont(FontFactory.COURIER, 6))));
                    linesCount += r.getLinesnum() + 1;
                    while (ColumnText.hasMoreText(ct.go())) {
                        linesCount = 0;
                        document.newPage();
                        flagNewPage = true;
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }

                linesCount = 0;
                side_of_the_page = 1;
                ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                if(flagNewPage) document.newPage();
                for (Receipt r : l1) {
                    if(linesCount + r.getLinesnum() >= 81){
                        linesCount = 0;
                        document.newPage();
                        flagNewPage = true;
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+CUTEDGE),
                            FontFactory.getFont(FontFactory.COURIER, 6))));
                    linesCount += r.getLinesnum() + 1;
                    while (ColumnText.hasMoreText(ct.go())) {
                        linesCount = 0;
                        document.newPage();
                        flagNewPage = true;
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }

                linesCount = 0;
                side_of_the_page = 2;
                ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                if(flagNewPage) document.newPage();
                for (Receipt r : l2) {
                    if(linesCount + r.getLinesnum() >= 81){
                        linesCount = 0;
                        document.newPage();
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+CUTEDGE),
                            FontFactory.getFont(FontFactory.COURIER, 6))));
                    linesCount += r.getLinesnum() + 1;
                    while (ColumnText.hasMoreText(ct.go())) {
                        linesCount = 0;
                        document.newPage();
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }

                document.close();

            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }


        } //else
            //Log.w("@#@#@#@#@#@#@#@#", "nonexist BB path");


    }

    private int find(LinkedList<Receipt> l0, LinkedList<Receipt> l1, LinkedList<Receipt> l2){

        int c0, c1, c2;
        c1 = c2 = c0 = 0;

        for (Receipt r : recp) {
           if(!l0.contains(r) && !l1.contains(r) && !l2.contains(r)){
               if(c0 + r.getLinesnum() <= c1 + r.getLinesnum() && c0 + r.getLinesnum() <= c2 + r.getLinesnum()){
                   l0.add(r);
                   c0 += r.getLinesnum();
               }
               else if(c1 + r.getLinesnum() <= c0 + r.getLinesnum() && c1 + r.getLinesnum() <= c2 + r.getLinesnum()){
                   l1.add(r);
                   c1 += r.getLinesnum();
               }
               else if(c2 + r.getLinesnum() <= c0 + r.getLinesnum() && c2 + r.getLinesnum() <= c1 + r.getLinesnum()){
                   l2.add(r);
                   c2 += r.getLinesnum();
               }
           }
        }
        return 0;
    }

}