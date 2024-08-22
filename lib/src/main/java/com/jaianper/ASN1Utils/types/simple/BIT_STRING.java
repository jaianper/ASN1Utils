package com.jaianper.ASN1Utils.types.simple;

/**
 * @author jaianper
 */
public class BIT_STRING extends INTEGER
{
    private static final char[] table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    @Override
    public Object decode(byte[] array)
    {
        StringBuffer buf = new StringBuffer("#");
        for (int i = 0; i != array.length; i++)
        {
            buf.append(table[((array[i] >>> 4) % 15)]);
            buf.append(table[(array[i] & 0xF)]);
        }
        return buf.toString();
    }
}
