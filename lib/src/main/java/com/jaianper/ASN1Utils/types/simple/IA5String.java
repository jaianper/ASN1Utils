package com.jaianper.ASN1Utils.types.simple;

/**
 * @author jaianper
 */
public class IA5String implements PrimitiveDO
{
    @Override
    public Object decode(byte[] bytes)
    {
        return new String(bytes);
    }

    @Override
    public byte[] encode(Object object)
    {
        return object.toString().getBytes();
    }
}
