package com.jaianper.ASN1Utils.types.simple;

/**
 * @author jaianper
 * 
 * 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, and SPACE
 */
public class NumericString extends OCTET_STRING
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
    public byte[] encode(Object object)//int len
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
