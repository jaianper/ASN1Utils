package com.jaianper.ASN1Utils.datastructure;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * ASN1FormatNode
 *
 * @author jaianper
 */
public class ASN1FormatNode implements Serializable
{
    private String tagName;
    private int tagValue;
    private String tagType;
    private String tagRef;
    private boolean optional;
    private String nodeChosen; // for choice type
    public LinkedList<ASN1FormatNode> subNodes;  // for Structured Types

    public ASN1FormatNode()
    {
        this.subNodes = new LinkedList<ASN1FormatNode>();
    }

    public void setTagName(String value)
    {
        this.tagName = value;
    }

    public String getTagName()
    {
        return this.tagName;
    }

    public LinkedList<ASN1FormatNode> getSubNodes()
    {
        return subNodes;
    }

    public int getTagValue() {
        return tagValue;
    }

    public void setTagValue(int tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getTagRef() {
        return tagRef;
    }

    public void setTagRef(String tagRef) {
        this.tagRef = tagRef;
    }

    /*public ASN1FormatNode getNodeChosen()
    {
        Iterator<ASN1FormatNode> it = subNodes.iterator();
        ASN1FormatNode nodeChosen = null;

        while(it.hasNext())
        {
            nodeChosen = it.next();

            if(this.nodeChosen.equals(nodeChosen.getTagName())) break;
        }

        return nodeChosen;
    }

    public void setNodeChosen(String nodeChosen) {
        this.nodeChosen = nodeChosen;
    } */
}
