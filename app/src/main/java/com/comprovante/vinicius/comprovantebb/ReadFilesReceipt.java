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


    public static String FOLDER = "BB/comprovantes";
    public static String SSTRING = "Comprovante_";
    public static String ESTRING = ".pdf";
    public static String SAVEPATH = "ComprovantesBB";


    public static int COLUNS = 3;

    public File mem;

    public File getFilePDF() {
        return fil;
    }

    public File fil;
    public File[] list;
    public List<Receipt> recp;


    public ReadFilesReceipt(int sd, int sm, int sy, int ed, int em, int ey, Context ctx) {

        mem = Environment.getExternalStorageDirectory();
        mem = new File(mem, FOLDER);
        int start = sy * 10000 + sm * 100 + sd;
        int end = ey * 10000 + em * 100 + ed;

        if (mem.exists()) {

            FilenameFilter textFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith(SSTRING) && name.endsWith(ESTRING)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            list = mem.listFiles(textFilter);
            recp = new LinkedList<Receipt>();


            int cd, cm, cy, current;
            String[] n;

            for (int i = 0; i < list.length; i++) {
                n = list[i].getName().substring(12, 22).split("-");

                cd = Integer.parseInt(n[0]);
                cm = Integer.parseInt(n[1]);
                cy = Integer.parseInt(n[2]);
                current = cy * 10000 + cm * 100 + cd;
                if (current >= start && current <= end) {
                    recp.add(new Receipt(list[i],ctx));
                }

            }

            int c = 1;
            String html = "<html><head><style> td { \n" +
                    "    padding-top: 25px;\n" +
                    "    padding-right: 15px;\n" +
                    "    padding-left: 15px;\n" +
                    "    font-size:xx-small;\n" +
                    "    vertical-align: text-top;\n" +
                    "}</style></head><body><table>";
            for(Receipt r : recp) {
                if(c % COLUNS == 1)
                    html += "<tr><td style=\"padding-left: 15px\">"+r.getContent().replace("\n","<br>\n").replace(" ","&nbsp")+"</td>"; //&nbsp
                else if(c % COLUNS == 0)
                    html += "<td>"+r.getContent().replace("\n","<br>\n").replace(" ","&nbsp")+"</td></tr>";
                else
                    html += "<td>"+r.getContent().replace("\n","<br>\n").replace(" ","&nbsp")+"</td>";

                c++;
            }
            if(c % COLUNS != 1)
                html += "</tr>";
            html += "</html></body></table>";
            //Log.w("@#@#@#@#@#@#@#@#", html);







            int mesescolhido = -1;
            int anoescolhido = -1;
            for(Receipt r : recp) {
                int m = r.getPm();
                int y = r.getPy();
                if(mesescolhido<0)
                    mesescolhido = m;
                else
                    if(mesescolhido != m)
                        mesescolhido = 0;

                if(anoescolhido<0)
                    anoescolhido = y;
                else
                    if(anoescolhido != y)
                        anoescolhido = 0;
            }

            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + SAVEPATH + File.separator + anoescolhido + File.separator + ((mesescolhido < 10)?("0"):("")) + mesescolhido);
            fil = new File(folder, "_imprimir.pdf");


            //Log.e(">>>>MES",mesescolhido+"/"+anoescolhido);

            if(mesescolhido >= 0) {
                //File folder = new File(Environment.getExternalStorageDirectory() +
                //        File.separator + SAVEPATH + File.separator + anoescolhido + File.separator + ((mesescolhido < 10)?("0"):("")) + mesescolhido);


                if (!folder.exists()) {
                    int a = 1;
                    if(folder.mkdirs()) {
                        for (Receipt r : recp) {
                            try {
                                File fi = new File(folder, r.getName().replace(' ', '_') + '_' + a++ + ".txt");
                                FileWriter writer = new FileWriter(fi);
                                writer.append("<name>" + r.getName() + "</name>\n" +
                                        "<original>\n" + r.getContentOriginal() + "\n</original>\n" +
                                        "<clear>\n" + r.getContent() + "\n</clear>\n" +
                                        "<value>" + r.getValue() + "</value>\n" +
                                        "<rdate>" + r.getRd() + "/" + r.getRm() + "/" + r.getRy() + "</rdate>\n" +
                                        "<pdate>" + r.getPd() + "/" + r.getPm() + "/" + r.getPy() + "</pdate>\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                else{
                    int a = 1 + folder.list().length;
                    for (Receipt r : recp) {
                        try {
                            File fi = new File(folder, r.getName().replace(' ', '_') + '_' + a++ + ".txt");
                            FileWriter writer = new FileWriter(fi);
                            writer.append("<name>" + r.getName() + "</name>\n" +
                                    "<original>\n" + r.getContentOriginal() + "\n</original>\n" +
                                    "<clear>\n" + r.getContent() + "\n</clear>\n" +
                                    "<value>" + r.getValue() + "</value>\n" +
                                    "<rdate>" + r.getRd() + "/" + r.getRm() + "/" + r.getRy() + "</rdate>\n" +
                                    "<pdate>" + r.getPd() + "/" + r.getPm() + "/" + r.getPy() + "</pdate>\n");
                            writer.flush();
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

           /* try {
                OutputStream file = new FileOutputStream(fil);
                Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
                PdfWriter.getInstance(document, file);
                document.open();
                HTMLWorker htmlWorker = new HTMLWorker(document);
                htmlWorker.parse(new StringReader(html));
                document.close();
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */



            Rectangle[] COLUMNS = {
                    new Rectangle(20, 15, 195, 832),
                    new Rectangle(205, 15, 390, 832),
                    new Rectangle(395, 15, 580, 832)
            };

            try {
                OutputStream file = null;
                file = new FileOutputStream(fil);
                Document document = new Document(PageSize.A4, 10, 10, 10, 10);
                PdfWriter writer = PdfWriter.getInstance(document, file);
                document.open();

                PdfContentByte canvas = writer.getDirectContent();
                ColumnText ct = new ColumnText(canvas);


                LinkedList<Receipt> l0 = new LinkedList<Receipt>();
                LinkedList<Receipt> l1 = new LinkedList<Receipt>();
                LinkedList<Receipt> l2 = new LinkedList<Receipt>();

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
                ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+"\n¬¬¬cortar aqui¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬"+'\n'),
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
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+"\n¬¬¬cortar aqui¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬"+'\n'),
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
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+"\n¬¬¬cortar aqui¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬"+'\n'),
                            FontFactory.getFont(FontFactory.COURIER, 6))));
                    linesCount += r.getLinesnum() + 1;
                    while (ColumnText.hasMoreText(ct.go())) {
                        linesCount = 0;
                        document.newPage();
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }

                document.close();












               /* int paragraphs = 0;
                for (Receipt r : recp) {
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s", r.getContent()+"\n"),
                            FontFactory.getFont(FontFactory.COURIER, 6))));
                    while (ColumnText.hasMoreText(ct.go())) {
                        if (side_of_the_page == 0) {
                            side_of_the_page = 1;
                        }
                        else {
                            side_of_the_page = 2;
                        }
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }
                document.close();
                */

                /*int paragraphs = 0;
                while (paragraphs++ < 247) {
                    ct.addElement(new Paragraph(new Phrase(10,String.format("%s%s", (paragraphs>9)?(paragraphs):("0"+paragraphs),"---------------------------------------------;"),
                            FontFactory.getFont(FontFactory.SYMBOL, 6))));
                    while (ColumnText.hasMoreText(ct.go())) {
                        if (side_of_the_page == 0) {
                            side_of_the_page = 1;
                            //canvas.moveTo(297.5f, 36);
                           // canvas.lineTo(297.5f, 806);
                            //canvas.stroke();
                        }
                        else {
                            side_of_the_page = 2;
                            //document.newPage();
                        }
                        ct.setSimpleColumn(COLUMNS[side_of_the_page]);
                    }
                }
                document.close();*/

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }


           /* try {
            File fi = new File(path, "comps.html");
            FileWriter writer = new FileWriter(fi);
            writer.append(html);
            writer.flush();
            writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        } else
            Log.w("@#@#@#@#@#@#@#@#", "NAO EXISTE CAMINHO");


    }

    public int find(LinkedList<Receipt> l0,LinkedList<Receipt> l1,LinkedList<Receipt> l2){

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