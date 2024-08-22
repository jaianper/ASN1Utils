package com.jaianper.ASN1Utils.types.simple;

import com.jaianper.ASN1Utils.Tools;

/**
 * BCDString
 *
 * @author jaianper
 */
public class BCDString extends OCTET_STRING
{
    @Override
    public Object decode(byte[] array)
    {
        return Tools.BCDToString(array);
    }

    @Override
    public byte[] encode(Object object)//, int len
    {
        return Tools.stringToBCD(object.toString());
    }
}
