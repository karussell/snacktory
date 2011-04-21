package de.jetwick.snacktory;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author goose | jim
 * 
 * this class will be responsible for taking our top node and stripping out junk we don't want
 * and getting it ready for how we want it presented to the user
 */
public class OutputFormatter {

    public static final int MIN_PARAGRAPH_TEXT = 50;
    private static final Logger logger = LoggerFactory.getLogger(OutputFormatter.class);
    private Element topNode;
    
    /**
     * takes an element and turns the P tags into \n\n     
     */
    public String getFormattedText(Element topNode) {
        this.topNode = topNode;
        removeNodesWithNegativeScores();
        convertLinksToText();
        replaceTagsWithText();
        
        StringBuilder sb = new StringBuilder();
        append(topNode, sb, "p");
        String str = Helper.innerTrim(sb.toString());

        // no subelements
        if (str.length() > topNode.ownText().length())
            return str;

        return topNode.text();
    }

    /**
     * cleans up and converts any nodes that should be considered text into text
     */
    private void convertLinksToText() {
        Elements links = topNode.getElementsByTag("a");
        for (Element item : links) {
            if (item.getElementsByTag("img").isEmpty()) {
                TextNode tn = new TextNode(item.text(), topNode.baseUri());
                item.replaceWith(tn);
            }
        }
    }

    /**
     * if there are elements inside our top node that have a negative gravity score, let's
     * give em the boot
     */
    private void removeNodesWithNegativeScores() {
        Elements gravityItems = this.topNode.select("*[gravityScore]");
        for (Element item : gravityItems) {
            int score = Integer.parseInt(item.attr("gravityScore"));
            if (score < 0 || item.text().length() < MIN_PARAGRAPH_TEXT)
                item.remove();                                        
        }
    }

    /**
     * replace common tags with just text so we don't have any crazy formatting issues
     * so replace <br>, <i>, <strong>, etc.... with whatever text is inside them
     */
    private void replaceTagsWithText() {
        Elements strongs = topNode.getElementsByTag("strong");
        for (Element item : strongs) {
            TextNode tn = new TextNode(item.text(), topNode.baseUri());
            item.replaceWith(tn);
        }

        Elements bolds = topNode.getElementsByTag("b");
        for (Element item : bolds) {
            TextNode tn = new TextNode(item.text(), topNode.baseUri());
            item.replaceWith(tn);
        }

        Elements italics = topNode.getElementsByTag("i");
        for (Element item : italics) {
            TextNode tn = new TextNode(item.text(), topNode.baseUri());
            item.replaceWith(tn);
        }
    }

    private void append(Element node, StringBuilder sb, String tagName) {
        for (Element e : node.getElementsByTag(tagName)) {
            if (e.tagName().equals(tagName)) {
                String text = e.text().trim();
                if (text.isEmpty() || text.length() < 50)
                    continue;

                sb.append(text);
                sb.append("\n\n");
            }
        }
    }
}
