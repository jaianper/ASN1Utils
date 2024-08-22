package com.jaianper.ASN1Utils.ber;

/**
 * BERMapping
 *
 * @author jaianper
 */
public class BERMapping
{
    private static byte DLE = 10;
    private static byte DC3 = 13;
    private BERMap ber;

    public BERMapping()
    {
        ber = new BERMap();
    }

    public void processDataRecordFormat(byte[] fileContent)
    {
        StringBuffer sbAll = new StringBuffer();
        StringBuffer sbCollector = new StringBuffer();

        // Primer recorrido al formato BER
        for(int n=0; n<fileContent.length; n++)
        {
            byte ch = fileContent[n];

            sbCollector.append((char)ch);

            if(ch == DLE || ch == DC3)
            {
                if(sbCollector.toString().contains(ReservedWords.COMMENT))
                {
                    int coIndex = sbCollector.indexOf(ReservedWords.COMMENT);
                    sbCollector.delete(coIndex, sbCollector.length()-1); // removes comments

                    if(!sbCollector.toString().trim().isEmpty())
                    {
                        String[] sentences = normalizeSentence(sbCollector.toString().trim());

                        int len = sentences.length;

                        for(int i=0; i<len; i++)
                        {
                            String sentence = sentences[i].trim();
                            if(!sentence.isEmpty() && !sentence.startsWith(ReservedWords.EXTENSIBLE))
                            {
                                sbAll.append(sentence+"\n");

                                if(ber.isCollectContent())
                                {
                                    ber.extractConstructedObjects(sentence);
                                }
                                else
                                {
                                    ber.validateImplicitTag(sentence);
                                }
                            }
                        }
                    }
                }
                else
                {
                    String[] sentences = normalizeSentence(sbCollector.toString().trim());

                    int len = sentences.length;

                    for(int i=0; i<len; i++)
                    {
                        String sentence = sentences[i].trim();
                        if(!sentence.isEmpty() && !sentence.startsWith(ReservedWords.EXTENSIBLE))
                        {
                            sbAll.append(sentence+"\n");
                            if(ber.isCollectContent())
                            {
                                ber.extractConstructedObjects(sentence);
                            }
                            else
                            {
                                ber.validateImplicitTag(sentence);
                            }
                        }
                    }
                }

                sbCollector.delete(0, sbCollector.length());
            }
        }

        ber.organizeData();
    }

   public BERMap getBERMap()
   {
       return ber;
   }

    /**
     * M�todo que recibe una l�nea y determina si hay varias sentencias impl�citas.
     * @param line
     * @return
     */
    public String[] normalizeSentence(String line)
    {
        if(line.contains("(") || line.contains(")"))
        {
            line = line.replace(",","\n");
        }
        else
        {
            line = line.replace(",","\n").replace("{","\n{\n").replace("}","\n}\n");
        }

        return line.split("\n");
    }

    public static void main(String[] args) {
        new BERMapping();
    }
}

class ReservedWords
{
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String COMMENT = "--";
    public static final String EXTENSIBLE = "...";
}
