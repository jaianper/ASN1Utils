package com.jaianper.ASN1Utils.datastructure;

import com.jaianper.ASN1Utils.CodingTag;
import com.jaianper.ASN1Utils.types.ASN1Type;
import com.jaianper.ASN1Utils.types.simple.PrimitiveDO;

import java.io.IOException;

/**
 * TLVStructure
 *
 * @author jaianper
 */
public class TLVStructure
{
    private Tag tag = null;
    private int length = -1;
    private int bytesDecoded = 0; // contador de bytes

    private byte[] encodedValue;            // for Simple Types
    private Object decodedValue;            // for Simple Types

    public Tag getTag()
    {
        return tag;
    }

    public int getLength()
    {
        return length;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public int getByteCounter() {
        return bytesDecoded;
    }

    public void sumBytes(int cant)
    {
        bytesDecoded+=cant;
    }

    public void setByteCounter(int bytesDecoded) {
        this.bytesDecoded = bytesDecoded;
    }

    public byte[] getEncodedValue() {
        return encodedValue;
    }

    public void setEncodedValue(byte[] encodedValue) {
        this.encodedValue = encodedValue;
    }

    public Object getDecodedValue() {
        return decodedValue;
    }

    public void setDecodedValue(Object decodedValue) {
        this.decodedValue = decodedValue;
    }

    public void countingByte()
    {
        bytesDecoded++;
    }

    public void encode() throws IOException
    {
        ASN1Type type = tag.getTagType();

        if(tag.getDATA_OBJECT() == CodingTag.PRIMITIV_DATA_OBJECT)
        {
            encodedValue = ((PrimitiveDO)tag.getTagType()).encode(decodedValue);
        }
        else
        {
            throw new IOException("The given value isn't primitive type.");
        }
    }

    public void decode() throws IOException
    {
        ASN1Type type = tag.getTagType();

        if(tag.getDATA_OBJECT() == CodingTag.PRIMITIV_DATA_OBJECT)
        {
            decodedValue = ((PrimitiveDO)tag.getTagType()).decode(encodedValue);
        }
        else
        {
            throw new IOException("The given value isn't primitive type.");
        }
    }
}
