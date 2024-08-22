package com.jaianper.ASN1Utils;

import com.jaianper.ASN1Utils.ber.BERMap;
import com.jaianper.ASN1Utils.ber.BERMapping;
import com.jaianper.ASN1Utils.datastructure.ASN1FormatNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jaianper
 */
public class Test3
{
    public Test3() throws Exception
    {
        testFile4();
        //testFile3();
    }

    public boolean testFile3() throws IOException
    {
        String drf  = "/home/jaianper/FILE002.asn";
        File file = new File(drf);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();

        BERMapping berMapping = new BERMapping();
        berMapping.processDataRecordFormat(bytes);
        BERMap ber = berMapping.getBERMap();

        ber.setASN1FormatNode(ber.genASN1FormatTree(ber.getFirstTag()));


        FileInputStream fileInput = new FileInputStream("/home/jaianper/FILE003.asn");
        FileOutputStream fileOutput = new FileOutputStream("/home/jaianper/FILE003_out.asn");

        ASN1Decoder decoder = new ASN1Decoder(fileInput, ber.getASN1FormatNode());

        try
        {
            decoder.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //System.out.println(decoder.getASN1DataNode().getXML());

        ASN1Encoder encoder = new ASN1Encoder(fileOutput);
        encoder.setASN1DataNode(decoder.getASN1DataNode());

        try
        {
            encoder.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return true;
    }

    private boolean testFile4() throws Exception
    {
        String drf = "/home/jaianper/FILE004.asn";

        File file = new File(drf);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();

        BERMapping berMapping = new BERMapping();
        berMapping.processDataRecordFormat(bytes);
        BERMap ber = berMapping.getBERMap();

        ber.setASN1FormatNode(ber.genASN1FormatTree(ber.getFirstTag()));
        System.out.println("");

        FileInputStream fileInput = new FileInputStream("/home/jaianper/FILE005.dat");
        FileOutputStream fileOutput = new FileOutputStream("/home/jaianper/FILE005_out.dat");

        ASN1Decoder decoder = new ASN1Decoder(fileInput, ber.getASN1FormatNode());
        decoder.execute();

        System.out.println(decoder.getASN1DataNode().getXML());

        ASN1Encoder encoder = new ASN1Encoder(fileOutput);
        encoder.setASN1DataNode(decoder.getASN1DataNode());
        //encoder.execute();
        
        return true;
    }

    public static void main(String[] args) throws IOException {
        try {
            new Test3();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*INTEGER inte = new INTEGER();

        long val = -25354240;

        byte[] bytes2 = inte.encode(val);

        long res = (Long)inte.decode(bytes2);

        System.out.println();*/

        //processTagValue(490);
    }
}

