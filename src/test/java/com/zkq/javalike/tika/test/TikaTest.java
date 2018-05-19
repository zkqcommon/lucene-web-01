package com.zkq.javalike.tika.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

public class TikaTest {
	public String fileToTxt(File file) {
        org.apache.tika.parser.Parser parser = new AutoDetectParser();
        try {
            InputStream inputStream = new FileInputStream(file);
            DefaultHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            parseContext.set(Parser.class, parser);
            parser.parse(inputStream,handler,metadata,parseContext);
            for (String string : metadata.names()) {
                System.out.println(string+":"+metadata.get(string));
            }
            inputStream.close();
            return handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	@Test
    public void test5() {
        System.out.println(fileToTxt(new File("/Volumes/MyDISK/从“借力”到“发力”.pdf")));
    }
}
