package kelly.springboot.test;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ExportDocImpl {

    @Test
    public void testWord() {
        try {
            FileInputStream in = new FileInputStream("/Users/a1800101471/Documents/welink.doc");//载入文档
            POIFSFileSystem pfs = new POIFSFileSystem(in);
            HWPFDocument hwpf = new HWPFDocument(pfs);
            Range range = hwpf.getRange();//得到文档的读取范围
            TableIterator it = new TableIterator(range);
            //迭代文档中的表格
            while (it.hasNext()) {
                Table tb = (Table) it.next();
                //迭代行，默认从0开始  
                for (int i = 0; i < tb.numRows(); i++) {
                    TableRow tr = tb.getRow(i);
                    //迭代列，默认从0开始  
                    for (int j = 0; j < tr.numCells(); j++) {
                        TableCell td = tr.getCell(j);//取得单元格
                        //取得单元格的内容  
                        for (int k = 0; k < td.numParagraphs(); k++) {
                            org.apache.poi.hwpf.usermodel.Paragraph para = td.getParagraph(k);
                            String s = para.text();//列值
                            System.out.print(s + "\t");
                        } //end for
                    }   //end for
                    System.out.println();//行结束
                }   //end for
                System.out.println();//表格结束
            } //end while
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end method  


    @Test
    public void testWord1() {
        try {
            //word 2003： 图片不会被读取     
            InputStream is = new FileInputStream(new File("/Users/a1800101471/Documents/welink.doc"));
            WordExtractor ex = new WordExtractor(is);
            String text2003 = ex.getText();
            System.out.println(text2003);
            //word 2007 图片不会被读取， 表格中的数据会被放在字符串的最后     
            OPCPackage opcPackage = POIXMLDocument.openPackage("/Users/a1800101471/Documents/welink.doc");
            POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            String text2007 = extractor.getText();
            System.out.println(text2007);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 