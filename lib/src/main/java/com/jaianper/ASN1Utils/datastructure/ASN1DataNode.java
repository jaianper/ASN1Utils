package com.jaianper.ASN1Utils.datastructure;

import com.jaianper.ASN1Utils.CodingTag;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ASN1FormatNode
 *
 * @author jaianper
 */
public class ASN1DataNode
{
    private TLVStructure tlv;
    private String tagName;
    private boolean closed;
    private LinkedList<ASN1DataNode> subNodes;  // for Structured Types

    public ASN1DataNode()
    {
        this.closed = false;
        this.subNodes = new LinkedList<ASN1DataNode>();
    }

    public void setTagName(String value)
    {
        this.tagName = value;
    }

    public String getTagName()
    {
        return this.tagName;
    }

    public void setTLV(TLVStructure tlv)
    {
        this.tlv = tlv;
    }

    public TLVStructure getTLV()
    {
        return tlv;
    }

    public void addNode(ASN1DataNode asn1DataNode)
    {
        subNodes.addLast(asn1DataNode);
    }

    public boolean addNode(String tagParent, ASN1DataNode asn1DataNode)
    {
        if(tagName.equals(tagParent) && !closed)
        {
            subNodes.addLast(asn1DataNode);

            return true;
        }
        else
        {
            Iterator<ASN1DataNode> it = subNodes.iterator();

            while(it.hasNext())
            {
                if(it.next().addNode(tagParent, asn1DataNode))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean closeNode(String tagName)
    {
        if(this.tagName.equals(tagName) && !closed)
        {
            closed = true;
            return true;
        }
        else
        {
            Iterator<ASN1DataNode> it = subNodes.iterator();

            while(it.hasNext())
            {
                if(it.next().closeNode(tagName))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public String getXML()
    {
        String xml = "";

        if(tlv.getTag().getDATA_OBJECT() == CodingTag.PRIMITIV_DATA_OBJECT)
        {
            xml = "\n<" + tagName + ">" + tlv.getDecodedValue() + "</" + tagName + ">";
        }
        else if(tlv.getTag().getDATA_OBJECT() == CodingTag.CONSTRUCTED_DATA_OBJECT)
        {
            if(subNodes.size() == 0)
            {
                xml = "\n<" + tagName + "/>";
            }
            else
            {
                xml = "\n<" + tagName + ">";

                Iterator<ASN1DataNode> it = subNodes.iterator();

                while(it.hasNext())
                {
                    xml += it.next().getXML().replace("\n","\n  ");
                }

                xml += "\n</" + tagName + ">";
            }
        }

        return xml;
    }

    public LinkedList<ASN1DataNode> getSubNodes()
    {
        return subNodes;
    }
}
