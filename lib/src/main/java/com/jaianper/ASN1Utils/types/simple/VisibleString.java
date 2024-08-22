package com.jaianper.ASN1Utils.types.simple;

/**
 * International ASCII printing character sets
 *
 * @author jaianper
 */
public class VisibleString extends OCTET_STRING
{
    @Override
    public Object decode(byte[] array)
    {
        String value = "";
        int length = array.length;

        for(int i=0; i<length; i++)
        {
            value += (char)array[i];
        }

        return value;
    }

    @Override
    public byte[] encode(Object object)
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
