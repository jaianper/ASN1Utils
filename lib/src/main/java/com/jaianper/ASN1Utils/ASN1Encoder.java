package com.jaianper.ASN1Utils;

import com.jaianper.ASN1Utils.datastructure.ASN1DataNode;
import com.jaianper.ASN1Utils.datastructure.Tag;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * ASN1Encoder
 *
 * @author jaianper
 */
public class ASN1Encoder
{
    private BufferedOutputStream bufferedOutput = null;
    private ASN1DataNode dataNode = null;

    public ASN1Encoder(FileOutputStream fileOutput)
    {
        bufferedOutput = new BufferedOutputStream(fileOutput);
    }

    public void execute() throws Exception
    {
        encodeASN1Object(dataNode);
        bufferedOutput.flush();
        bufferedOutput.close();
    }

    private void encodeASN1Object(ASN1DataNode dn) throws IOException
    {
        if(dn.getTLV().getLength() != -1)
        {
            int tagCode;

            int tagClass = dn.getTLV().getTag().getCLASS();
            int tagDataObject = dn.getTLV().getTag().getDATA_OBJECT();

            // 00xxxxxx, when x=0 -> complete tagCode with 0     UNIVERSAL_CLASS
            // 01xxxxxx, when x=0 -> complete tagCode with 64    APPLICATION_CLASS
            // 10xxxxxx, when x=0 -> complete tagCode with 128   CONTEXT_SPECIFIC_CLASS
            // 11xxxxxx, when x=0 -> complete tagCode with 192   PRIVATE_CLASS
            tagCode = (64*tagClass);
            // xx0xxxxx, when x=0 -> complete tagCode with 0    PRIMITIV_DATA_OBJECT
            // xx1xxxxx, when x=0 -> complete tagCode with 32   CONSTRUCTED_DATA_OBJECT
            tagCode += (32*tagDataObject);

            if(tagClass == CodingTag.APPLICATION_CLASS || tagClass == CodingTag.CONTEXT_SPECIFIC_CLASS)
            {
                encodeTag(tagCode, dn.getTLV().getTag().getTagValue());
                encodeLength(dn.getTLV().getLength());
                encodeValue(dn);
            }
            else if(tagClass == CodingTag.PRIVATE_CLASS)
            {
                //TODO private class
            }
            else if(tagClass == CodingTag.UNIVERSAL_CLASS)
            {
                //TODO universal class
            }
        }
        else
        {
            Iterator<ASN1DataNode> it = dn.getSubNodes().iterator();

            while(it.hasNext())
            {
                encodeASN1Object(it.next());
            }
        }
    }

    private void encodeTag(int tagCode, int tagValue) throws IOException
    {
        /**** Tratamiento del identificador del tag (tagValue) ****/

        if(tagValue < CodingTag.SBWTV)
        {
            tagCode += tagValue;
            writeByte((byte)tagCode);
        }
        else
        {
            tagCode += CodingTag.SBWTV;
            writeByte((byte)tagCode);

            byte[] bytes = new byte[0];
            int comp = 0;

            byte b;

            for(int i=0; true; i+=7)
            {
                b = (byte)(tagValue >>> i);
                bytes = Tools.addFirst(b, bytes);

                comp |= ((b & 0x7F) << i);
                if(comp == tagValue) break;  // TODO EXPERIMENTAL: END LOOP
            }

            int lim = bytes.length-1;

            for(int i=0; i<bytes.length; i++)
            {
                if(i != lim)
                {
                    writeByte((byte)((bytes[i] & 0x7F) + 128));
                }
                else
                {
                    writeByte((byte)(bytes[i] & 0x7F));
                }
            }
        }
    }

    private void encodeLength(int tagLength) throws IOException
    {
        /**** Tratamiento del tama�o del tag (tagLength) ****/

        if(tagLength < 129) // 0xxxxxxx, but can also write the indefinite length byte (10000000).
        {
            writeByte((byte)tagLength);
        }
        else if(tagLength > 128 && tagLength < 256)  // 10000001 xxxxxxxx
        {
            writeByte((byte) 129);
            writeByte((byte) tagLength);
        }
        else if(tagLength > 255 && tagLength < 65536)  // 10000010 xxxxxxxx xxxxxxxx
        {
            writeByte((byte) 130);
            writeByte((byte)(tagLength >>> 8));
            writeByte((byte)tagLength);
        }
    }

    private void encodeValue(ASN1DataNode dn) throws IOException
    {
        /**** Tratamiento del contenido del Tag ****/

        if(dn.getTLV().getTag().getDATA_OBJECT() == CodingTag.PRIMITIV_DATA_OBJECT)
        {
            dn.getTLV().encode();
            byte[] b = dn.getTLV().getEncodedValue();

            for(int i=0; i<b.length; i++)
            {
                writeByte(b[i]);
            }
        }
        else if(dn.getTLV().getTag().getDATA_OBJECT() == CodingTag.CONSTRUCTED_DATA_OBJECT)
        {
            Iterator<ASN1DataNode> it = dn.getSubNodes().iterator();

            while(it.hasNext())
            {
                encodeASN1Object(it.next());
            }

            if(dn.getTLV().getLength() == 128)
            {
                writeByte((byte)0);
                writeByte((byte)0);
            }
        }
    }

    private void writeByte(byte b) throws IOException
    {
        bufferedOutput.write(b);
    }

    public void setASN1DataNode(ASN1DataNode dataNode)
    {
        this.dataNode = dataNode;
    }
}
