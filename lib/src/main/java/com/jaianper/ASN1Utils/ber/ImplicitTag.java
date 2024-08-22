package com.jaianper.ASN1Utils.ber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

/**
 * ImplicitTag
 *
 * @author jaianper
 */
public class ImplicitTag implements Serializable
{
    private String tagName = "";
    private int tagValue = -1;
    private String tagType = "";
    private String tagRef = "";
    private ArrayList<NodeTag> varCollection = new ArrayList<NodeTag>();
    private HashMap<String, String> enumCollection = new HashMap<String, String>();

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public String getTagRef() {
        return tagRef;
    }

    public void setTagRef(String tagRef) {
        this.tagRef = tagRef;
    }

    public void addVariable(NodeTag var)
    {
        varCollection.add(var);
    }

    public void addElement(String key, String value)
    {
        enumCollection.put(key, value);
    }

    public String getElement(String key)
    {
        return enumCollection.get(key);
    }

    public boolean isEnumerated()
    {
        return !enumCollection.isEmpty();
    }

    public ArrayList<NodeTag> getVarCollection()
    {
        return varCollection;
    }
}
