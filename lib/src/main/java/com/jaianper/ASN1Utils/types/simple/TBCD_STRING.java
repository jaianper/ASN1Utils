package com.jaianper.ASN1Utils.types.simple;

import com.jaianper.ASN1Utils.Tools;

/**
 * TBCD_STRING
 *
 * @author jaianper
 */
public class TBCD_STRING extends OCTET_STRING
{
    @Override
    public Object decode(byte[] array)
    {
        return Tools.TBCDToString(array);
    }

    @Override
    public byte[] encode(Object object)
    {
        return Tools.stringToTBCD(object.toString());
    }
}

