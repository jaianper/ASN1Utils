package com.jaianper.ASN1Utils;

/**
 * CodingTag
 *
 * @author jaianper
 */
public class CodingTag
{
    public static final int UNIVERSAL_CLASS        = 0; // 00
    public static final int APPLICATION_CLASS      = 1; // 01
    public static final int CONTEXT_SPECIFIC_CLASS = 2; // 10
    public static final int PRIVATE_CLASS          = 3; // 11

    public static final int PRIMITIV_DATA_OBJECT    = 0; // 0
    public static final int CONSTRUCTED_DATA_OBJECT = 1; // 1

    public static final int SBWTV = 31; // 11111 -> if there is a 2nd byte with tag value
}
