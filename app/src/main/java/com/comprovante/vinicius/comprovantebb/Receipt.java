package com.comprovante.vinicius.comprovantebb;


import android.content.Context;
import android.util.Log;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.IOException;
public class Receipt {

    private int rd,rm,ry,pd,pm,py, linesnum;
    private float value;
    private String name, contentOriginal, content;
    private File fname;



    public Receipt(File file, Context ctx) {
        fname = file;
        contentOriginal = "";
        content = "";
        rd = rm = ry = pd = pm = py = linesnum = 0;
        value = 0.0f;
        name= "comp";

        try {
            extractText(file.getAbsolutePath(),"/storage/emulated/0/txt.txt",ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLinesnum() {
        return linesnum;
    }

    public void extractText(String src, String dest, Context ctx) throws IOException {
        PdfReader reader = new PdfReader(src);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);


        //Log.w(">>>>>>","iniciio");

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            contentOriginal = parser.processContent(i, new SimpleTextExtractionStrategy()).getResultantText();
            //Log.w(">>>", "++++++++++++++++++++++++++++++++++++++++++++++++++\n"+contentOriginal+"\n++++++++++++++++++++++++++++++++++++++++++++++++++");
            String lines[] = contentOriginal.split("\n");
            content += contentOriginal;


            if(lines[0].compareTo("SISBB  -  SISTEMA DE INFORMACOES BANCO DO BRASIL") == 0){
                content = content.substring(49);
                content = content.replace("AUTOATENDIMENTO","BANCO DO BRASIL");

                String[] coluns = lines[1].split(" ");
                coluns = coluns[0].split("/");
                /*rd = Integer.parseInt(coluns[0]);  NAO TA MAIS EXTRAINDO DATA
                rm = Integer.parseInt(coluns[1]);
                ry = Integer.parseInt(coluns[2]);*/
            }
            else{
                String[] coluns = lines[0].split(" ");
                coluns = coluns[0].split("/");
                /*rd = Integer.parseInt(coluns[0]);
                rm = Integer.parseInt(coluns[1]);
                ry = Integer.parseInt(coluns[2]);*/
            }
            content = content.replace("            COMPROVANTE DE PAGAMENTO\n","");
            content = content.replace("                                                \n" +
                                      "            COMPROVANTE DE PAGAMENTO            \n" +
                                      "                                                \n","");
            content = content.replace("     COMPROVANTE DE PAGAMENTO DE TITULOS\n","");
            content = content.replace("                                                \n" +
                                      "     COMPROVANTE DE PAGAMENTO DE TITULOS        \n" +
                                      "                                                \n","");
            content = content.replace("  COMPROVANTE DE PAGAMENTO DA FATURA DO CARTAO\n","");

            content = content.replace("Central de Atendimento BB                       \n" +
                    "4004 0001 Capitais e regioes metropolitanas     \n" +
                    "0800 729 0001 Demais localidades                \n" +
                    "Consultas, informacoes e servicos transacionais.\n" +
                    "                                                \n" +
                    "SAC                                             \n" +
                    "0800 729 0722                                   \n" +
                    "Informacoes, reclamacoes e cancelamento de      \n" +
                    "produtos e servicos.                            \n" +
                    "                                                \n" +
                    "Ouvidoria                                       \n" +
                    "0800 729 5678                                   \n" +
                    "Reclamacoes nao solucionadas nos canais         \n" +
                    "habituais: agencia, SAC e demais canais de      \n" +
                    "atendimento.                                    \n" +
                    "                                                \n" +
                    "Atendimento a Deficientes Auditivos ou de Fala  \n" +
                    "0800 729 0088                                   \n" +
                    "Informacoes, reclamacoes, cancelamento de       \n" +
                    "cartao, outros produtos e servicos de Ouvidoria.","");

            content = content.replace("Com Ourocard voce parcela em ate 18x nas lojas\n" +
                    "iPlace. Promocao valida ate 31/03/2019.\n" +
                    "Saiba mais em\n" +
                    "beneficiosourocard.com.br.","");

            content = content.replace("Pague suas compras com Ourocard Visa e apoie\n" +
                    "uma causa sem pagar nada a mais por isso.\n" +
                    "Escolha uma em vaidevisa.visa.com.br/causas","");

            String secondLine = content.substring(49,99);
            String secondLineNew = secondLine.replace("                             ","   COMPROVANTE DE PAGAMENTO  ");
            secondLineNew = secondLineNew.replace("      SEGUNDA VIA       ","COMPROVANTE DE PAGAMENTO");
            content = content.replace(secondLine,secondLineNew);

            //Log.w("<<<", "\n\n\n"+content+"\n\n\n");

            for(int j = 0; j < lines.length; j++){
                if(lines[j].contains("Convenio")){
                    name = lines[j].substring(10);
                }
                else if(lines[j].contains("Valor Total") || lines[j].contains("VALOR TOTAL") || lines[j].contains("VALOR COBRADO") || lines[j].contains("Valor     :")){
                    String[] coluns = lines[j].split(" ");
                    //value = Float.parseFloat(coluns[coluns.length-1].replace(',','.'));
                }
                else if(lines[j].contains("Data do pagamento") || lines[j].contains("DATA DA TRANSFERENCIA") || lines[j].contains("DATA DO PAGAMENTO") || lines[j].contains("Data      :")){
                    String[] coluns = lines[j].split(" ");
                    coluns = coluns[coluns.length-1].split("/");
                    /*pd = Integer.parseInt(coluns[0]);
                    pm = Integer.parseInt(coluns[1]);
                    py = Integer.parseInt(coluns[2]);*/
                }
            }

            //===========================
            if(content.contains("CENTRO INFANTIL DE INVESTIGACO") || content.contains("BOLDRINI")){
                name = "BOLDRINI";
            }
            /*else if(content.contains("BANCO SANTANDER") && name.compareTo("00") == 0){
                name = "SAMARITANO";
            }*/
            else if(content.contains("Pagamento Fatura Cartao")){
                name = "CARTAO DE CRED PETROBRAS";
            }
            /*else if(content.contains("CLARO TV")){
                name = "CLARO";
            }*/
            else if(content.contains("SANASA")){
                name = "SANASA";
            }
            else if(content.contains("CPFL")){
                name = "CPFL";
            }
            /*else if(content.contains("TELECOMUNICACOES")){
                name = "VIVO";
            }*/
            else if(content.contains("TIM")){
                name = "TIM";
            }



            linesnum = content.split("\n").length;


            //===========================




            //Log.w("+++", "\n\n\ngerado: "+rd+"/"+rm+"/"+ry+"\npago: "+pd+"/"+pm+"/"+py+"\nconvenio: "+name+"\nvalor: "+value+"\n____________________________________");
        }
        reader.close();
    }

    public int getRd() {
        return rd;
    }

    public int getRm() {
        return rm;
    }

    public int getRy() {
        return ry;
    }

    public int getPd() {
        return pd;
    }

    public int getPm() {
        return pm;
    }

    public int getPy() {
        return py;
    }

    public float getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getContentOriginal() {
        return contentOriginal;
    }

    public String getContent() {
        return content;
    }

    public File getFname() {
        return fname;
    }
}
