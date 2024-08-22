package com.jaianper.ASN1Utils.ber;

import com.jaianper.ASN1Utils.datastructure.ASN1FormatNode;
import com.jaianper.ASN1Utils.datastructure.Tag;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to extract information from a BER file format; the extracted information is mapped to be used in
 * interpreting ASN.1 binaries files.
 *
 * @author jaianper
 */
public class BERMap implements Serializable
{
    public HashMap<String,ImplicitTag> tagsSimpleTypes;
    public LinkedHashMap<String,ImplicitTag> tagsStructuredTypes;

    private boolean collectContent;
    private ImplicitTag lastTag; // last type of implicit tag
    private ASN1FormatNode formatNode;

    public BERMap()
    {
        this.tagsSimpleTypes = new HashMap<String, ImplicitTag>();
        this.tagsStructuredTypes = new LinkedHashMap<String, ImplicitTag>();
        this.collectContent = false;
    }

    /**
     * Method for extracting tags representing primitive data objects .. For each tag is stored the respective type
     * of data.
     *
     * @param data
     */
    public void validateImplicitTag(String data)
    {
        if("{".equals(data))
        {
            collectContent = true;
            return;
        }

        ImplicitTag it = new ImplicitTag();
        String tagName, tagType, tagValue, tagRef;

        String pattern = "^([A-Za-z0-9\\-]+)\\s+::=\\s+([A-Za-z0-9\\- ]+)\\s+(\\()";   // NodeID ::= IA5String (SIZE(1..20))
        Matcher matcher = Pattern.compile(pattern).matcher(data);

        if(matcher.find())
        {
            tagName = matcher.group(1);
            tagType = matcher.group(2);

            it.setTagName(tagName);
            it.setTagType(tagType);
            lastTag = it;
            tagsSimpleTypes.put(tagName, it);

            return;
        }

        pattern = "^([A-Za-z0-9\\-]+)\\s+::=\\s+([A-Za-z0-9\\- ]+$)";     // FFDAppendIndicator ::= OCTECT STRING
        matcher = Pattern.compile(pattern).matcher(data);

        if(matcher.find())
        {
            tagName = matcher.group(1);

            it.setTagName(tagName);

            String implicitType = matcher.group(2);

            if(implicitType.startsWith("SEQUENCE OF") || implicitType.startsWith("SET OF"))
            {
                int splitPoint = implicitType.indexOf("OF") + 2;
                tagType = implicitType.substring(0, splitPoint);
                tagRef = implicitType.substring(splitPoint, implicitType.length()).trim();

                it.setTagType(tagType);
                it.setTagRef(tagRef);
                lastTag = it;
                tagsStructuredTypes.put(tagName, it);
            }
            else
            {
                tagType = implicitType;
                it.setTagType(tagType);
                lastTag = it;

                if("SEQUENCE".equals(tagType) || "SET".equals(tagType) || "CHOICE".equals(tagType) ||
                   "ANY".equals(tagType) || "SELECTION".equals(tagType))
                {
                    tagsStructuredTypes.put(tagName, it);
                }
                else
                {
                    tagsSimpleTypes.put(tagName, it);
                }
            }

            return;
        }

        pattern = "^([A-Za-z0-9\\-]+)\\s+::=\\s+\\[([A-Z]+)\\s+([0-9]+)\\]\\s+([A-Za-z0-9\\- ]+)\\s+(\\()"; // LocationArea ::= [APPLICATION 48] INTEGER (..
        matcher = Pattern.compile(pattern).matcher(data);

        if(matcher.find())
        {
            tagName = matcher.group(1);
            tagType = matcher.group(4);

            it.setTagName(tagName);
            it.setTagType(tagType);

            if("APPLICATION".equals(matcher.group(2)))
            {
                tagValue = matcher.group(3);
                it.setTagValue(Integer.parseInt(tagValue));
            }

            lastTag = it;
            tagsSimpleTypes.put(tagName, it);

            return;
        }

        pattern = "^([A-Za-z0-9\\-]+)\\s+::=\\s+\\[([A-Z]+)\\s+([0-9]+)\\]\\s+([A-Za-z0-9\\- ]+)$";  // LocationArea ::= [APPLICATION 48] INTEGER
        matcher = Pattern.compile(pattern).matcher(data);

        if(matcher.find())
        {
            tagName = matcher.group(1);

            it.setTagName(tagName);

            if("APPLICATION".equals(matcher.group(2)))
            {
                tagValue = matcher.group(3);
                it.setTagValue(Integer.parseInt(tagValue));
            }

            String implicitType = matcher.group(4);

            if(implicitType.startsWith("SEQUENCE OF") || implicitType.startsWith("SET OF"))
            {
                int splitPoint = implicitType.indexOf("OF") + 2;
                tagType = implicitType.substring(0, splitPoint);
                tagRef = implicitType.substring(splitPoint, implicitType.length()).trim();

                it.setTagType(tagType);
                it.setTagRef(tagRef);
                lastTag = it;
                tagsStructuredTypes.put(tagName, it);
            }
            else
            {
                tagType = implicitType;
                it.setTagType(tagType);
                lastTag = it;

                if("SEQUENCE".equals(tagType) || "SET".equals(tagType) || "CHOICE".equals(tagType) ||
                   "ANY".equals(tagType) || "SELECTION".equals(tagType))
                {
                    tagsStructuredTypes.put(tagName, it);
                }
                else
                {
                    tagsSimpleTypes.put(tagName, it);
                }
            }

            return;
        }
    }


    /**
     * Method for extracting tags that represent data objects built .. For each tag is stored the structure
     * type and the containing tags.
     *
     * @param data
     */
    public void extractConstructedObjects(String data)
    {
        if("}".equals(data))
        {
            collectContent = false;
            return;
        }

        String parentName = lastTag.getTagName();
        String parentType = lastTag.getTagType();

        if(("SEQUENCE".equals(parentType) || "SET".equals(parentType) || "CHOICE".equals(parentType)) && collectContent)
        {
            NodeTag nt = new NodeTag();
            String tagName, tagType, tagValue, tagRef;

            String pattern = "^([A-Za-z0-9\\-]+)\\s+([A-Za-z0-9\\-]+)\\s*(OPTIONAL)*$"; // accountingInfo  AccountingInfo  OPTIONAL
            Matcher matcher = Pattern.compile(pattern).matcher(data);

            if(matcher.find())
            {
                tagName = matcher.group(1);
                tagType = matcher.group(2);
                nt.setTagName(tagName);
                nt.setTagType(tagType);

                if("OPTIONAL".equals(matcher.group(3)))
                {
                    nt.setOptional(true);
                }

                tagsStructuredTypes.get(parentName).addVariable(nt);

                return;
            }

            pattern = "^([A-Za-z0-9\\-]+)\\s+\\[([0-9]+)\\]\\s+([A-Za-z0-9\\- ]+)\\s+(\\()"; // iPTextV4Address [2] IA5String (SIZE(7..15))
            matcher = Pattern.compile(pattern).matcher(data);

            if(matcher.find())
            {
                tagName = matcher.group(1);
                tagValue = matcher.group(2);
                tagType = matcher.group(3);
                nt.setTagName(tagName);
                nt.setTagValue(Integer.parseInt(tagValue));
                nt.setTagType(tagType);

                tagsStructuredTypes.get(parentName).addVariable(nt);

                return;
            }

            pattern = "^([A-Za-z0-9\\-]+)\\s+\\[([0-9]+)\\]\\s+((([A-Za-z0-9\\- ]+)(?= OPTIONAL))|(([A-Za-z0-9\\- ]+)))\\s*(OPTIONAL)*"; // flowNumber [2] SEQUENCE OF INTEGER OPTIONAL
            matcher = Pattern.compile(pattern).matcher(data);

            if(matcher.find())
            {
                tagName = matcher.group(1);
                tagValue = matcher.group(2);
                nt.setTagName(tagName);
                nt.setTagValue(Integer.parseInt(tagValue));

                String implicitType = matcher.group(3);

                if(implicitType.startsWith("SEQUENCE OF") || implicitType.startsWith("SET OF"))
                {
                    int splitPoint = implicitType.indexOf("OF") + 2;
                    tagType = implicitType.substring(0, splitPoint);
                    tagRef = implicitType.substring(splitPoint, implicitType.length()).trim();

                    nt.setTagType(tagType);
                    nt.setTagRef(tagRef);
                }
                else
                {
                    tagType = implicitType;
                    nt.setTagType(tagType);
                }

                if("OPTIONAL".equals(matcher.group(8)))
                {
                    nt.setOptional(true);
                }

                tagsStructuredTypes.get(parentName).addVariable(nt);

                return;
            }
        }
        else // ENUMERATED
        {
            String tagName, tagValue;

            String pattern = "^([A-Za-z0-9\\-]+)\\s+\\(([0-9]+)\\)$"; // mSorNetworkProvidedSubscriptionVerified  (0)
            Matcher matcher = Pattern.compile(pattern).matcher(data);

            if(matcher.find())
            {
                tagName = matcher.group(1);
                tagValue = matcher.group(2);

                tagsSimpleTypes.get(parentName).addElement(tagValue, tagName);

                return;
            }
        }
    }

    public ImplicitTag getLastTag()
    {
        return lastTag;
    }

    public ImplicitTag getFirstTag()
    {
        Iterator<String> names = tagsStructuredTypes.keySet().iterator();

        if(names.hasNext())
        {
            return tagsStructuredTypes.get(names.next());
        }

        return null;
    }

    public void organizeData()
    {
        Iterator<String> keys = tagsStructuredTypes.keySet().iterator();
        String key;
        ImplicitTag it;
        ArrayList<NodeTag> lNode;
        int len;

        while(keys.hasNext())
        {
            key = keys.next();
            it = tagsStructuredTypes.get(key);
            lNode = it.getVarCollection();

            len = lNode.size();
            NodeTag nt;
            for(int i=0; i<len; i++)
            {
                nt = lNode.get(i);

                if(nt.getTagValue() == -1)
                {
                    nt.setTagValue(getTagValue(nt.getTagType()));
                    tagsStructuredTypes.get(key).getVarCollection().set(i, nt);
                }
            }
        }
    }

    public ASN1FormatNode getASN1FormatNode()
    {
        return formatNode;
    }

    public void setASN1FormatNode(ASN1FormatNode formatNode)
    {
        this.formatNode = formatNode;
    }

    public ASN1FormatNode genASN1FormatTree(Object node)
    {
        ASN1FormatNode fn = new ASN1FormatNode();

        if(node instanceof ImplicitTag)
        {
            ImplicitTag it = (ImplicitTag)node;

            fn.setTagType(it.getTagType());

            if("SEQUENCE OF".equals(it.getTagType()) || "SET OF".equals(it.getTagType()))  // SEQUENCE OF or SET OF
            {
                fn.setTagRef(it.getTagRef());

                ImplicitTag itAux = getReferencedTag(it.getTagRef());
                fn.subNodes.addLast(genASN1FormatTree(itAux));
            }
            else if(!it.getVarCollection().isEmpty()) // SEQUENCE or SET or CHOICE
            {
                ArrayList<NodeTag> lstNodes = it.getVarCollection();

                for (int i = 0; i < lstNodes.size(); i++)
                {
                    fn.subNodes.addLast(genASN1FormatTree(lstNodes.get(i)));
                }
            }
            else
            {
                if(!typeExist(it.getTagType()))
                {
                    ImplicitTag itAux = getReferencedTag(it.getTagType());
                    fn = genASN1FormatTree(itAux);
                }
            }

            fn.setTagName(it.getTagName());
            fn.setTagValue(it.getTagValue());
        }
        else
        {
            if(node instanceof  NodeTag)
            {
                NodeTag nt = (NodeTag)node;

                if("SET OF".equals(nt.getTagType()) || "SEQUENCE OF".equals(nt.getTagType()))
                {
                    ImplicitTag itAux = getReferencedTag(nt.getTagRef());
                    fn = genASN1FormatTree(itAux);
                    fn.setTagName(nt.getTagName());
                    fn.setTagValue(nt.getTagValue());
                    fn.setOptional(nt.isOptional());
                }
                else
                {
                    ImplicitTag itAux = getReferencedTag(nt.getTagType());

                    if(itAux == null)
                    {
                        if(typeExist(nt.getTagType()))
                        {
                            fn.setTagType(nt.getTagType());
                            fn.setOptional(nt.isOptional());
                            fn.setTagName(nt.getTagName());
                            fn.setTagValue(nt.getTagValue());
                        }
                    }
                    else
                    {
                        fn = genASN1FormatTree(itAux);
                        fn.setTagName(nt.getTagName());
                        fn.setTagValue(nt.getTagValue());
                        fn.setOptional(nt.isOptional());
                    }
                }
            }
        }

        return fn;
    }

    public ImplicitTag getReferencedTag(String tagType)
    {
        ImplicitTag res = new ImplicitTag();

        if(tagsStructuredTypes.containsKey(tagType))
        {
            res = tagsStructuredTypes.get(tagType);
        }
        else if(tagsSimpleTypes.containsKey(tagType))
        {
            res = tagsSimpleTypes.get(tagType);
        }
        else
        {
            res = null; // TODO s�lo se deber�a dar si el archivo de Sintaxis de transferencia est� malformado
        }

        return res;
    }

    public int getTagValue(String tagName)
    {
        int tagValue = -1;

        if(tagsSimpleTypes.containsKey(tagName))
        {
            tagValue = tagsSimpleTypes.get(tagName).getTagValue();
        }
        else if(tagsStructuredTypes.containsKey(tagName))
        {
            tagValue = tagsStructuredTypes.get(tagName).getTagValue();
        }

        return tagValue;
    }

    private boolean typeExist(String tagType)
    {
        Class clazz = null;
        String clazzPath = "";
        boolean exist = true;

        try
        {
            clazzPath = Tag.PRIMITIVE_PATH + "." + (tagType.replace(" ","_").replace("-","_"));
            clazz = Class.forName(clazzPath);
        }
        catch (ClassNotFoundException e)
        {
            exist = false;
        }

        return exist;
    }

    public boolean isCollectContent()
    {
        return collectContent;
    }
}
