package com.comprovante.vinicius.comprovantebb;


import android.content.Context;
import android.util.Log;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.IOException;
public class Receipt {

    private int linesnum;
    private float value;
    private String contentOriginal, content;
    private File fname;



    public Receipt(File file, Context ctx) {
        fname = file;
        contentOriginal = "";
        content = "";
        linesnum = 0;
        value = 0.0f;

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


        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            contentOriginal = parser.processContent(i, new SimpleTextExtractionStrategy()).getResultantText();
            String lines[] = contentOriginal.split("\n");
            content += contentOriginal;


            if(lines[0].compareTo("SISBB  -  SISTEMA DE INFORMACOES BANCO DO BRASIL") == 0){
                content = content.substring(49);
                content = content.replace("AUTOATENDIMENTO","BANCO DO BRASIL");

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






            content = content.replace("Central de Atendimento BB","");
            content = content.replace("4004 0001 Capitais e regioes metropolitanas","");
            content = content.replace("0800 729 0001 Demais localidades","");
            content = content.replace("Consultas, informacoes e servicos transacionais.","");
            //content = content.replace("SAC","");
            content = content.replace("0800 729 0722","");
            content = content.replace("Informacoes, reclamacoes e cancelamento de","");
            content = content.replace("produtos e servicos.","");
            //content = content.replace("Ouvidoria","");
            content = content.replace("0800 729 5678","");
            content = content.replace("Reclamacoes nao solucionadas nos canais","");
            content = content.replace("habituais: agencia, SAC e demais canais de","");
            content = content.replace("atendimento.","");
            content = content.replace("Atendimento a Deficientes Auditivos ou de Fala","");
            content = content.replace("0800 729 0088","");
            content = content.replace("Informacoes, reclamacoes, cancelamento de","");
            content = content.replace("cartao, outros produtos e servicos de Ouvidoria.","");

            //content = content.replace("SAC","-------------");
            //content = content.replace("Ouvidoria","-------------");

            //content = content.replace("","");






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



            linesnum = content.split("\n").length;


        }
        reader.close();
    }





    public float getValue() {
        return value;
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
