package bowlgames;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.IOException;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Ryan
 */
class BowlPick {

    int weight;
    boolean cover;

    @Override
    public String toString() {
        return ("Weight:" + weight + " :: Cover:" + cover);
    }
}

public class BowlXMLParser {
    private static Document getDocument(String url){
        DOMParser parser = new DOMParser();
        try {
            parser.parse(url);
        } catch (SAXException | IOException e) {
            System.out.println("Could not parse file " + url);
            System.err.print(e);
            System.exit(-1);
        }
        Document doc = parser.getDocument();
        return doc;
    }
    
    public static String[] buildBowlsTable(String url){
        Document doc = getDocument(url);
        return buildBowlsTable(doc);
    }
    public static String[] buildBowlsTable(Document doc){
        NodeList nl = doc.getElementsByTagName("bowl");
        Element e;
        Node n;
        NamedNodeMap nnm;
        String attrname;
        String attrval;
        int i, len;
        
        len = nl.getLength();
        
        String[] bowls = new String[len];

        for (int j = 0; j < len; j++) {
            e = (Element) nl.item(j);
            nnm = e.getAttributes();
            if (nnm != null) {
                for (i = 0; i < nnm.getLength(); i++) {
                    n = nnm.item(i);
                    attrname = n.getNodeName();
                    attrval = n.getNodeValue();
                    if (attrname.equals("name")) {
                        bowls[j] = attrval;
                    }
//                    else
//                        System.out.println("No Name in tag...");
                }
            }
        }
        return bowls;
    }
    /*
     * Returns a HashMap<String, HashMap<String,BowlPick>>
     *  HashMap <playerName, HashMap<bowlName, {cover, weight}>
     */
    public static HashMap buildPickTable(String url){
        Document doc = getDocument(url);
        return buildPickTable(doc);
    }
    
    public static HashMap buildPickTable(Document doc) {
        NodeList nl = doc.getElementsByTagName("bowl");
        Element e;
        Node n;
        NamedNodeMap nnm;
        HashMap<String, HashMap<String,BowlPick>> pickTable = new HashMap();
        String attrname;
        String attrval;
        int i, len;

        len = nl.getLength();

        for (int j = 0; j < len; j++) {
            e = (Element) nl.item(j);
            String player = e.getParentNode().getAttributes().getNamedItem("name").getNodeValue();
            if(pickTable.get(player) == null)
                pickTable.put(player, new HashMap<String, BowlPick>());

            HashMap map = pickTable.get(player);
            nnm = e.getAttributes();
            if (nnm != null && nnm.getLength() > 2) {
                BowlPick pick;
                pick = new BowlPick();
                String name = null;
                for (i = 0; i < nnm.getLength(); i++) {
                    n = nnm.item(i);
                    attrname = n.getNodeName();
                    attrval = n.getNodeValue();
                    if (attrname.equals("name")) {
                        name = attrval;
                    }
                    if (attrname.equals("weight")) {
                        if(!attrval.equals(""))
                            pick.weight = Integer.parseInt(attrval);
                    }
                    if (attrname.equals("pick")) {
                        pick.cover = attrval.equals("Fav");
                    }
                }
                //System.out.println(pick);
                if (name != null) {
                    if (pick.weight > 0) {
                        map.put(name, pick);
                    } else {
                        System.out.println("Weight not valid. Assigning 0!");
                        map.put(name, pick);
                    }
                } else {
                    System.out.println("No bowl name entered. Skipping entry!");
                }
            }
        }
        return pickTable;
    }
    
    /*
     * Returns a HashMap<String, Boolean>
     *  HashMap<bowlName, outcome>
     */
    static HashMap buildResultsTable(String url){
        Document doc = getDocument(url);
        return buildResultsTable(doc);
    }
    static HashMap buildResultsTable(Document doc) {
        NodeList nl = doc.getElementsByTagName("bowl");
        Element e;
        Node n;
        NamedNodeMap nnm;
        HashMap<String, Boolean> map = new HashMap();
        String attrname;
        String attrval;
        int i, len;

        len = nl.getLength();

        for (int j = 0; j < len; j++) {
            e = (Element) nl.item(j);
            nnm = e.getAttributes();
            if (nnm != null) {
                String name = null;
                boolean cover = false;
                for (i = 0; i < nnm.getLength(); i++) {
                    n = nnm.item(i);
                    attrname = n.getNodeName();
                    attrval = n.getNodeValue();
                    if (attrname.equals("name")) {
                        name = attrval;
                    }
                    if (attrname.equals("outcome")) {
                        cover = attrval.equals("Fav");
                    }
                }

                if (name != null) {
                    map.put(name, cover);
                } else {
                    System.out.println("No bowl name entered. Skipping entry!");
                }
            }
        }
        return map;
    }

    //test
    public static void main(String[] args) {

        Document doc = getDocument("./src/bowlpicks.xml");
        System.out.println(buildPickTable(doc));

//        doc = getDocument("./src/results.xml");
//        HashMap<String, BowlPick> map = buildResultsTable(doc);
//        System.out.println(map);
//       
//        String[] keys = new String[map.size()];
//        map.keySet().toArray(keys);
//        for(String s: keys)
//            System.out.println(s);
//        
//        doc = getDocument("./src/bowls.xml");
//        String[] names = buildBowlsTable(doc);
//        for(String s:names)
//            System.out.println(s);
    }
}
