package com.jaianper.ASN1Utils.types.simple;

import com.jaianper.ASN1Utils.types.ASN1Type;

/**
 * PrimitiveDO
 *
 * Represents ASN.1's built-in simple types.
 */
public interface PrimitiveDO extends ASN1Type
{
    public Object decode(byte[] bytes);

    public byte[] encode(Object object); //, int len
}
