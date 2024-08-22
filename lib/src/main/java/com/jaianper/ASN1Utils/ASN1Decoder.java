package com.jaianper.ASN1Utils;

import com.jaianper.ASN1Utils.datastructure.ASN1DataNode;
import com.jaianper.ASN1Utils.datastructure.ASN1FormatNode;
import com.jaianper.ASN1Utils.datastructure.TLVStructure;
import com.jaianper.ASN1Utils.datastructure.Tag;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * ASN1Decoder
 *
 * @author jaianper
 */
public class ASN1Decoder
{
    private FileInputStream inputStream;
    private FileChannel ch;
    private byte[] barray;
    private ByteBuffer bb;
    private int nRead;
    private int nCount;

    private long inicio = 0;
    private long processedBytes = 0;
    private boolean moreBytes = true;
    private HashMap<Integer,String> UniversalTypes = new HashMap<Integer, String>();
    private ASN1DataNode dataNode = null;      //
    private ASN1FormatNode asn1FormatNode;

    public ASN1Decoder(FileInputStream fileInput, ASN1FormatNode formatNode)
    {
        this.asn1FormatNode = formatNode;
        this.dataNode = new ASN1DataNode();
        inputStream = fileInput;
        ch = fileInput.getChannel();
        barray = new byte[262144];
        bb = ByteBuffer.wrap(barray);

        initializeUT();
    }

    public void execute() throws Exception
    {
        inicio = new GregorianCalendar().getTimeInMillis();

        // Se hace esto s�lo si el ROOT no tiene valor en la sintaxis de transferencia.
        if(asn1FormatNode.getTagValue() == -1)  // TODO EXPERIMENTAL: por el momento el ROOT no tiene valor y es CHOICE
        {
            TLVStructure firstTLV = new TLVStructure();
            Tag firstTag = new Tag();
            firstTag.setDATA_OBJECT(CodingTag.CONSTRUCTED_DATA_OBJECT); // TODO EXPERIMENTAL: cabe la posibilidad de que el TAG ROOT sea primitivo
            firstTLV.setTag(firstTag);

            dataNode.setTLV(firstTLV);
            dataNode.setTagName(asn1FormatNode.getTagName());

            byte b1;
            byte b2;
            ASN1DataNode subNode;

            while(hasMoreBytes())
            {
                subNode = getNextASN1Object(receiveByte(), asn1FormatNode);

                if (subNode != null) {
                    dataNode.getTLV().sumBytes(subNode.getTLV().getByteCounter());
                    dataNode.addNode(subNode);
                }
            }
        }
        else
        {
            if (hasMoreBytes())
            {
                ASN1FormatNode fn = new ASN1FormatNode();
                fn.subNodes.addLast(asn1FormatNode);
                dataNode = getNextASN1Object(receiveByte(), fn);
            }
        }

        System.out.println("Tiempo: " + ((new GregorianCalendar().getTimeInMillis()) - inicio));

        inputStream.close();
    }

    private ASN1DataNode getNextASN1Object(byte b, ASN1FormatNode fnParent) throws Exception
    {
        int dec = b & 0xFF;  // Unsigned 8 bit

        int tagClass = (b & 0xC0) >>> 6;      // xx000000
        int tagDataObject = (b & 0x20) >>> 5; // 00x00000
        int tagValue = b & 0x1F;              // 000xxxxx

        ASN1DataNode dn = new ASN1DataNode();
        TLVStructure tlv = new TLVStructure();
        Tag tag = new Tag();
        tag.setCLASS(tagClass);
        tag.setDATA_OBJECT(tagDataObject);

        if(tagClass == CodingTag.APPLICATION_CLASS || tagClass == CodingTag.CONTEXT_SPECIFIC_CLASS)
        {
            int[] countAndValue = decodeTag(tagValue);
            int[] countAndLength = decodeLength(receiveByte() & 0xFF); // Unsigned 8 bit

            tlv.setLength(countAndLength[1]);
            tag.setTagValue(countAndValue[1]);

            if(tagDataObject == CodingTag.PRIMITIV_DATA_OBJECT)
            {
                dn = processPrimitiveObject(tlv, tag, fnParent);
            }
            else if(tagDataObject == CodingTag.CONSTRUCTED_DATA_OBJECT)
            {
                dn = processConstructedObject(tlv, tag, fnParent);
            }

            dn.getTLV().sumBytes(countAndValue[0]);
            dn.getTLV().sumBytes(countAndLength[0]);
            dn.getTLV().countingByte();
        }
        else if(tagClass == CodingTag.UNIVERSAL_CLASS)
        {
            String tagType = UniversalTypes.get(dec);
            //tag.setTagType(tagType);
            tlv.setTag(tag);

            System.out.println("[UNIVERSAL] Value: " + dec + ", Type: " + tagType);
            // TODO: implementaci�n para clase universal
            return null;
        }
        else if(tagClass == CodingTag.PRIVATE_CLASS)
        {
            System.out.println("[PRIVATE] Value: " + dec + ", Length: " + tlv.getLength());
            // TODO: implementaci�n para clase privada
            return null;
        }

        return dn;
    }

    private ASN1DataNode processPrimitiveObject(TLVStructure tlv, Tag tag, ASN1FormatNode fnParent) throws Exception
    {
        int tagLength = tlv.getLength();
        ASN1FormatNode fn = getFormatNode(tag.getTagValue(), fnParent);

        if(fn.getTagValue() == -1)
        {
            ASN1DataNode subNode = processPrimitiveObject(tlv, tag, fn);

            Tag nestedTag = new Tag();
            TLVStructure nestedTLV = new TLVStructure();

            nestedTag.setDATA_OBJECT(CodingTag.CONSTRUCTED_DATA_OBJECT);
            nestedTag.setTagType(fn.getTagType());
            nestedTLV.setTag(nestedTag);
            nestedTLV.sumBytes(subNode.getTLV().getByteCounter());

            ASN1DataNode nestedDN = new ASN1DataNode();
            nestedDN.setTagName(fn.getTagName());
            nestedDN.setTLV(nestedTLV);
            nestedDN.addNode(subNode);

            return nestedDN;
        }

        String tagName = fn.getTagName();
        String tagType = fn.getTagType();
        tag.setTagType(tagType);
        tlv.setTag(tag);

        byte[] bytes = new byte[tagLength];

        for(int i=0; i<tagLength; i++)
        {
            bytes[i] = receiveByte();
            tlv.countingByte();
        }

        tlv.setEncodedValue(bytes);
        tlv.decode();

        ASN1DataNode dn = new ASN1DataNode();

        /**
         * Cuando el valor pertenece a un tipo enumerado se obtiene el �ndice, pero el significado ha de ser
         * interpretado por el receptor.
         */

        dn.setTLV(tlv);
        dn.setTagName(tagName);

        System.out.println("[PRIMITIVE] Value: "+tag.getTagValue()+", Name: "+tagName+", Type: "+tagType+
                ", Content: "+tlv.getDecodedValue()+", Length: "+tagLength);
        return dn;
    }

    private ASN1DataNode processConstructedObject(TLVStructure tlv, Tag tag, ASN1FormatNode fnParent) throws Exception
    {
        int tagLength = tlv.getLength();
        ASN1FormatNode fn = getFormatNode(tag.getTagValue(), fnParent);

        if(fn.getTagValue() == -1)
        {
            ASN1DataNode subNode = processConstructedObject(tlv, tag, fn);

            Tag nestedTag = new Tag();
            TLVStructure nestedTLV = new TLVStructure();

            nestedTag.setDATA_OBJECT(CodingTag.CONSTRUCTED_DATA_OBJECT);
            nestedTag.setTagType(fn.getTagType());
            nestedTLV.setTag(nestedTag);
            nestedTLV.sumBytes(subNode.getTLV().getByteCounter());

            ASN1DataNode nestedDN = new ASN1DataNode();
            nestedDN.setTagName(fn.getTagName());
            nestedDN.setTLV(nestedTLV);
            nestedDN.addNode(subNode);

            return nestedDN;
        }

        String tagName = fn.getTagName();
        String tagType = fn.getTagType();

        tag.setTagType(tagType);
        tlv.setTag(tag);

        ASN1DataNode dn = new ASN1DataNode();
        dn.setTLV(tlv);
        dn.setTagName(tagName);

        System.out.println("[CONSTRUCTED] Value: "+tag.getTagValue()+", Name: "+tagName+
                ", Type: "+tagType+", Length: "+(tagLength == 128 ? "Indefinite length" : tagLength));

        if(tagLength == 128)
        {
            byte b1;
            byte b2;

            ASN1DataNode subNode;

            while(hasMoreBytes())
            {
                b1 = receiveByte();

                if((b1 & 0xFF) == 0)
                {
                    b2 = receiveByte();

                    if((b2 & 0xFF) == 0)
                    {
                        // Se omiten 2 ceros
                        break;
                    }
                    else
                    {
                        subNode = getNextASN1Object(b1, fn);
                        dn.getTLV().sumBytes(subNode.getTLV().getByteCounter());
                        dn.addNode(subNode);

                        subNode = getNextASN1Object(b2, fn);
                        dn.getTLV().sumBytes(subNode.getTLV().getByteCounter());
                        dn.addNode(subNode);
                    }
                }
                else
                {
                    subNode = getNextASN1Object(b1, fn);
                    dn.getTLV().sumBytes(subNode.getTLV().getByteCounter());
                    dn.addNode(subNode);
                }
            }
        }
        else
        {
            ASN1DataNode subNode;

            while(tagLength != dn.getTLV().getByteCounter())
            {
                subNode = getNextASN1Object(receiveByte(), fn);
                if(subNode != null)
                {
                    dn.getTLV().sumBytes(subNode.getTLV().getByteCounter());
                    dn.addNode(subNode);
                }
                else
                {
                    dn.getTLV().countingByte();
                }
            }
        }

        return dn;
    }

    private byte receiveByte() throws IOException
    {
        byte b = 0x00;

        while(true)
        {
            if(nCount<nRead)
            {
                b = barray[nCount];
                nCount++;

                processedBytes++;
                break;
            }
            else
            {
                bb.clear();
                moreBytes = ((nRead=ch.read(bb)) != -1);

                if(!moreBytes)
                {
                    break;
                }

                nCount = 0;
            }
        }

        return b;
    }

    private boolean hasMoreBytes()
    {
        return moreBytes;
    }

    private int[] decodeLength(int firstLength) throws IOException
    {
        int[] countAndLength = new int[2];
        int length = 128;  // indefinite length

        if(firstLength < 128) // 0xxxxxxx
        {
            countAndLength[0] = 1;
            length = firstLength;
        }
        else if(firstLength == 129)  // 10000001 xxxxxxxx
        {
            countAndLength[0] = 2;
            length = (receiveByte() & 0xFF); // Unsigned 8 bit
        }
        else if(firstLength == 130)  // 10000010 xxxxxxxx xxxxxxxx
        {
            countAndLength[0] = 3;
            length = ((receiveByte() & 0xFF) << 8) | (receiveByte() & 0xFF);
        }

        countAndLength[1] = length;
        return countAndLength;
    }

    private int[] decodeTag(int tagValue) throws IOException
    {
        int[] countAndTagValue = new int[2];

        if(tagValue == CodingTag.SBWTV)
        {
            byte[] bytes = new byte[0];
            byte b;
            int val;

            while(true)
            {
                b = receiveByte();
                countAndTagValue[0]++;
                val = (b & 0x7F);
                bytes = Tools.addLast((byte)val, bytes);

                if((b & 0xFF) < 128) break;    // TODO EXPERIMENTAL: END LOOP
            }

            int comp = 0;
            int n = bytes.length-1;
            for(int i=0; n >= 0; i+=7)
            {
                comp |= ((bytes[n] & 0x7F) << i);
                n--;
            }

            tagValue = comp;
        }

        countAndTagValue[1] = tagValue;
        return countAndTagValue;
    }

    private ASN1FormatNode getFormatNode(int tagValue, ASN1FormatNode fnParent) throws Exception
    {
        Iterator<ASN1FormatNode> iterator = fnParent.getSubNodes().iterator();

        while(iterator.hasNext())
        {
            ASN1FormatNode formatNode = iterator.next();
            if(formatNode.getTagValue() == tagValue)
            {
                return formatNode;
            }
            else if("CHOICE".equals(formatNode.getTagType()))
            {
                Iterator<ASN1FormatNode> it = formatNode.getSubNodes().iterator();

                while (it.hasNext())
                {
                    ASN1FormatNode formNode = it.next();

                    if (formNode.getTagValue() == tagValue)
                    {
                        if("CHOICE".equals(fnParent.getTagType()))
                        {
                            return formatNode;
                        }
                        else
                        {
                            return formNode;
                        }
                    }
                }
            }
        }

        return null;
    }

    public ASN1DataNode getASN1DataNode()
    {
        return dataNode;
    }

    private void initializeUT()
    {
        UniversalTypes.put(0, "reserved for BER");
        UniversalTypes.put(1, "BOOLEAN");
        UniversalTypes.put(2, "INTEGER");
        UniversalTypes.put(3, "BIT STRING");
        UniversalTypes.put(4, "OCTET STRING");
        UniversalTypes.put(5, "NULL");
        UniversalTypes.put(6, "OBJECT IDENTIFIER");
        UniversalTypes.put(7, "ObjectDescriptor");
        UniversalTypes.put(8, "INSTANCE OF, EXTERNAL");
        UniversalTypes.put(9, "REAL");
        UniversalTypes.put(10, "ENUMERATED");
        UniversalTypes.put(11, "EMBEDDED PDV");
        UniversalTypes.put(12, "UTF8String");
        UniversalTypes.put(13, "RELATIVE-OID");
        UniversalTypes.put(16, "SEQUENCE");//, SEQUENCE OF");
        UniversalTypes.put(17, "SET");//, SET OF");
        UniversalTypes.put(18, "NumericString");
        UniversalTypes.put(19, "PrintableString");
        UniversalTypes.put(20, "TeletexString, T61String");
        UniversalTypes.put(21, "VideotexString");
        UniversalTypes.put(22, "IA5String");
        UniversalTypes.put(23, "UTCTime");
        UniversalTypes.put(24, "GeneralizedTime");
        UniversalTypes.put(25, "GraphicString");
        UniversalTypes.put(26, "VisibleString, ISO646String");
        UniversalTypes.put(27, "GeneralString");
        UniversalTypes.put(28, "UniversalString");
        UniversalTypes.put(29, "CHARACTER STRING");
        UniversalTypes.put(30, "BMPString");
    }
}
