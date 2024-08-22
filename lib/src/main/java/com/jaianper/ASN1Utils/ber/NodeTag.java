package com.jaianper.ASN1Utils.ber;

import java.io.Serializable;

/**
 * NodeTag
 *
 * @author jaianper
 */
public class NodeTag implements Serializable
{
    private String tagName = "";
    private boolean optional = false;
    private int tagValue = -1;
    private String tagType = "";
    private String tagRef = "";

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
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
}
