package com.jaianper.ASN1Utils.types.simple;

/**
 * OCTET_STRING
 *
 * @author jaianper
 */
public class OCTET_STRING implements PrimitiveDO
{
    @Override
    public Object decode(byte[] array)
    {
        String value = "";
        int length = array.length;

        for(int i=0; i<length; i++)
        {
            value += (i+1 == length ? String.format("%02X", array[i]) : String.format("%02X", array[i]) + " ");
        }

        return value;
    }

    @Override
    public byte[] encode(Object object) //int len
    {
        String str = object.toString();
        int l = str.length();
        byte[] b = new byte[l];

        for(int i=0; i<l; i++)
        {
            b[i] = (byte)str.charAt(i);
        }

        return b;
    }
}
