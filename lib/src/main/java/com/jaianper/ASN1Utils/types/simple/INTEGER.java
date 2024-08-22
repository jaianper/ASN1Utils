package com.jaianper.ASN1Utils.types.simple;

import com.jaianper.ASN1Utils.Tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * INTEGER
 *
 * Model integer variable values.
 *
 * @author jaianper
 */
public class INTEGER implements PrimitiveDO
{
    @Override
    public Object decode(byte[] array)
    {
        long dec = Tools.byteArrayToNum(array);

        if((array[0] & 0xFF) > 127)
        {
            dec--;
            array = Tools.numToByteArray(dec);

            for(int i=0; i<array.length; i++)
            {
                array[i] = (byte)(~array[i]&0xFF);
            }

            dec = Tools.byteArrayToNum(array);
            dec *= -1;
        }

        return dec;
    }

    @Override
    public byte[] encode(Object object)
    {
        long dec = (Long)object;
        byte[] bytes;

        if(dec < 0)
        {
            dec *= -1;

            bytes = Tools.numToByteArray(dec);

            if((bytes[0] & 0xFF) > 127)
            {
                bytes = Tools.addFirst((byte)0,bytes);
            }

            for(int i=0; i<bytes.length; i++)
            {
                bytes[i] = (byte)(~bytes[i]&0xFF);
            }
            dec = Tools.byteArrayToNum(bytes)+1;

            bytes = Tools.numToByteArray(dec);
        }
        else
        {
            bytes = Tools.numToByteArray(dec);

            if((bytes[0] & 0xFF) > 127)
            {
                bytes = Tools.addFirst((byte)0,bytes);
            }
        }
        return bytes;
    }
}