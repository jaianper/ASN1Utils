package com.jaianper.ASN1Utils;

/**
 * Tools
 *
 * @author jaianper
 */
public class Tools
{
    private static char[] cTBCDSymbols = "0123456789*#abc".toCharArray();

    /**
     * Converts a decimal to the binary expression. Use this method only if the value is always between 0 and 15;
     * if is zero then the initial array is returned.
     *
     * @param dec decimal value between 0 and 15
     * @return character array where each character represents a bit 0 or 1
     */
    public static char[] dec2BinL4(int dec)
    {
        char[] bin = new char[]{'0','0','0','0'};
        int i=3;

        while(dec > 0)
        {
            if(dec % 2 != 0) bin[i] = '1';
            dec /= 2;
            i--;
        }

        return bin;
    }

    public static int binToDec(char[] bin)
    {
        int res = 0;
        int j = bin.length;
        int l = bin.length;

        for(int i=0; i<l; i++)
        {
            j--;

            if(bin[i] == '1')
            {
                res += StrictMath.pow(2.0, j);
            }
        }

        return res;
    }

    public static char[] concat(char[] arr1, char[] arr2)
    {
        int len1 = arr1.length;
        int len2 = arr2.length;
        int len = len1 + len2;
        char[] res = new char[len];

        for(int i=0; i<len1; i++)
        {
            res[i] = arr1[i];
        }

        int j = 0;
        for(int i=len1; i<len; i++)
        {
            res[i] = arr2[j];
            j++;
        }

        return res;
    }

    public static byte[] addFirst(byte b, byte[] arr)
    {
        byte[] res = new byte[arr.length+1];
        res[0] = b;

        for(int i=0; i<arr.length; i++)
        {
            res[i+1] = arr[i];
        }

        return res;
    }

    public static byte[] addLast(byte b, byte[] arr)
    {
        byte[] res = new byte[arr.length+1];

        int i;
        for(i=0; i<arr.length; i++)
        {
            res[i] = arr[i];
        }
        res[i] = b;

        return res;
    }

    /**
     *
     * @param arr
     * @return Valor entero positivo
     */
    public static long byteArrayToNum(byte[] arr)
    {
        long res = 0;
        long x;
        int n = 0;

        for(int i=arr.length-1; i>=0; i--)
        {
            x = (arr[i] & 0xFF);
            res |= (x << n);
            n+=8;
        }

        return res;
    }

    /**
     *
     * @param dec Valor entero positivo
     * @return
     */
    public static byte[] numToByteArray(long dec)
    {
        byte[] bytes = new byte[0];
        long comp = 0;

        byte b;
        long x;
        for(int i=0; true; i+=8)
        {
            b = (byte)(dec >>> i);
            bytes = Tools.addFirst(b, bytes);

            x = (b & 0xFF);
            comp |= (x << i);
            if(comp == dec) break; // TODO END LOOP
        }

        return bytes;
    }

    public static String BCDToString(byte[] array)
    {
        String value = "";
        int length = array.length;
        String sb = "";

        for(int i=0; i<length; i++)
        {
            sb += String.format("%02X", array[i]);
        }

        value += sb;

        return value;
    }

    public static byte[] stringToBCD(String str)
    {
        byte[] bytes = new byte[str.length()/2];
        char[] s = new char[0];
        int j=0;
        int n=0;
        for(int i=0; i<str.length(); i++)
        {
            j++;

            if(s.length == 0)
            {
                s = Tools.dec2BinL4(repBCD(str.charAt(i)));
            }
            else
            {
                s = concat(s, Tools.dec2BinL4(repBCD(str.charAt(i))));
            }

            if(j==2)
            {
                bytes[n] = (byte)Tools.binToDec(s);
                n++;
                s=new char[0];
                j=0;
            }
        }

        return bytes;
    }

    public static int repBCD(char c)
    {
        if(c == 'A') return 10;
        else if(c == 'B') return 11;
        else if(c == 'C') return 12;
        else if(c == 'D') return 13;
        else if(c == 'E') return 14;
        else if(c == 'F') return 15;
        else return c-48;
    }

    public static String TBCDToString(byte[] array)
    {
        int size = (array == null ? 0 : array.length);
        StringBuffer buffer = new StringBuffer(2*size);
        for (int i=0; i<size; ++i) {
            int octet = array[i];
            int n2 = (octet >> 4) & 0xF;
            int n1 = octet & 0xF;

            if (n1 == 15) {
                throw new NumberFormatException("Illegal filler in octet n=" + i);
            }
            buffer.append(cTBCDSymbols[n1]);

            if (n2 == 15) {
                if (i != size-1)
                    throw new NumberFormatException("Illegal filler in octet n=" + i);
            } else
                buffer.append(cTBCDSymbols[n2]);
        }

        return buffer.toString();
    }

    public static byte[] stringToTBCD(String str)
    {
        int length = (str == null ? 0:str.length());
        int size = (length + 1)/2;
        byte[] buffer = new byte[size];

        for (int i=0, i1=0, i2=1; i<size; ++i, i1+=2, i2+=2) {

            char c = str.charAt(i1);
            int n2 = getTBCDNibble(c, i1);
            int octet = 0;
            int n1 = 15;
            if (i2 < length) {
                c = str.charAt(i2);
                n1 = getTBCDNibble(c, i2);
            }
            octet = (n1 << 4) + n2;
            buffer[i] = (byte)(octet & 0xFF);
        }

        return buffer;
    }

    private static int getTBCDNibble(char c, int i1)
    {
        int n = Character.digit(c, 10);

        if (n < 0 || n > 9) {
            switch (c) {
                case '*':
                    n = 10;
                    break;
                case '#':
                    n = 11;
                    break;
                case 'a':
                    n = 12;
                    break;
                case 'b':
                    n = 13;
                    break;
                case 'c':
                    n = 14;
                    break;
                default:
                    throw new NumberFormatException("Bad character '" + c
                            + "' at position " + i1);
            }
        }
        return n;
    }

    /**
     * Converts a decimal to the binary expression.
     *
     * @param dec decimal value between 0 and Integer.MAX_VALUE
     * @return String where each character represents a bit 0 or 1
     */
    public static String decToBin(int dec) // Herramienta para imprimir en expresi�n de bits
    {
        if(dec == 0) return "0";

        String bin = "";

        while(dec > 0)
        {
            bin = (dec % 2 != 0 ? '1' : '0') + bin;
            dec /= 2;
        }

        return bin;
    }
}