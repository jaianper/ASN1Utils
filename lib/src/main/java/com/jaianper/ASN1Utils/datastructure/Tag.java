package com.jaianper.ASN1Utils.datastructure;

import com.jaianper.ASN1Utils.CodingTag;
import com.jaianper.ASN1Utils.types.ASN1Type;
import com.jaianper.ASN1Utils.types.simple.PrimitiveDO;
import com.jaianper.ASN1Utils.types.structured.ConstructedDO;

/**
 * Tag
 *
 * @author jaianper
 */
public class Tag
{
    private int classVal;
    private int dataObjectVal;

    private int tagValue = -1;
    private ASN1Type tagType;

    public static final String PRIMITIVE_PATH = PrimitiveDO.class.getPackage().getName();
    public static final String CONSTRUCTED_PATH = ConstructedDO.class.getPackage().getName();

    public void setCLASS(int value)
    {
        this.classVal = value;
    }

    public void setDATA_OBJECT(int value)
    {
        this.dataObjectVal = value;
    }

    public void setTagValue(int tagValue)
    {
        this.tagValue = tagValue;
    }

    public int getCLASS()
    {
        return classVal;
    }

    public int getDATA_OBJECT()
    {
        return dataObjectVal;
    }

    public int getTagValue()
    {
        return tagValue;
    }

    public void setTagType(String tagType) throws Exception
    {
        Class clazz = null;
        String clazzPath = "";

        try
        {
            if(getDATA_OBJECT() == CodingTag.PRIMITIV_DATA_OBJECT)
            {
                clazzPath = PRIMITIVE_PATH + "." + (tagType.replace(" ","_").replace("-","_"));
                clazz = Class.forName(clazzPath);
                this.tagType = (PrimitiveDO)clazz.newInstance();
            }
            else if(getDATA_OBJECT() == CodingTag.CONSTRUCTED_DATA_OBJECT)
            {
                clazzPath = CONSTRUCTED_PATH + "." + (tagType.replace(" ","_").replace("-", "_"));
                clazz = Class.forName(clazzPath);
                this.tagType = (ConstructedDO)clazz.newInstance();
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new Exception("The " + tagType + " type is not implemented!", e);
        }
        catch (InstantiationException e)
        {
            throw e;
        }
        catch (IllegalAccessException e)
        {
            throw e;
        }
    }

    public ASN1Type getTagType()
    {
        return tagType;
    }
}
