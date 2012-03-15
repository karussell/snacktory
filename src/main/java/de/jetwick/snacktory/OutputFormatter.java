package de.jetwick.snacktory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author goose | jim
 * 
 * this class will be responsible for taking our top node and stripping out junk we don't want
 * and getting it ready for how we want it presented to the user
 */
public class OutputFormatter {
    private static final Logger logger = LoggerFactory.getLogger(OutputFormatter.class);
    public static final int MIN_PARAGRAPH_TEXT = 50;
    private static final List<String> NODES_TO_REPLACE = Arrays.asList("strong", "b", "i");
    protected final int minParagraphText;
    protected final List<String> nodesToReplace;

    public OutputFormatter() {
        this(MIN_PARAGRAPH_TEXT, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minParagraphText) {
        this(minParagraphText, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minParagraphText, List<String> nodesToReplace) {
        this.minParagraphText = minParagraphText;
        this.nodesToReplace = nodesToReplace;
    }

    /**
     * takes an element and turns the P tags into \n\n     
     */
    public String getFormattedText(Element topNode) {
        removeNodesWithNegativeScores(topNode);
        convertLinksToText(topNode);
        replaceTagsWithText(topNode);

        StringBuilder sb = new StringBuilder();
        append(topNode, sb, "p");
        String str = SHelper.innerTrim(sb.toString());
        if (str.length() > 100)
            return str;

        // no subelements
        if (str.isEmpty() || !topNode.text().isEmpty() && str.length() <= topNode.ownText().length())
            str = topNode.text();

        // if jsoup failed to parse the whole html now parse this smaller 
        // snippet again to avoid html tags disturbing our text:
        return Jsoup.parse(str).text();
    }

    /**
     * cleans up and converts any nodes that should be considered text into text
     * @param topNode
     */
    protected void convertLinksToText(Element topNode) {
        Elements links = topNode.getElementsByTag("a");
        for (Element item : links) {
            if (item.getElementsByTag("img").isEmpty()) {
                TextNode tn = new TextNode(" " + item.text() + " ", topNode.baseUri());
                item.replaceWith(tn);
            } else if (item.text().isEmpty())
                item.remove();
        }
    }

    /**
     * if there are elements inside our top node that have a negative gravity score, let's
     * give em the boot
     * @param topNode
     */
    protected void removeNodesWithNegativeScores(Element topNode) {
        Elements gravityItems = topNode.select("*[gravityScore]");
        for (Element item : gravityItems) {
            int score = Integer.parseInt(item.attr("gravityScore"));
            if (score < 0 || item.text().length() < minParagraphText)
                item.remove();
        }
    }

    /**
     * replace common tags with just text so we don't have any crazy formatting issues
     * so replace <br>, <i>, <strong>, etc.... with whatever text is inside them
     * @param topNode
     */
    protected void replaceTagsWithText(Element topNode) {
        for (String tag : nodesToReplace) {
            Elements elems = topNode.getElementsByTag(tag);
            for (Element item : elems) {
                // jsoup calls trim although not appropriate. see tests
                TextNode tn = new TextNode(" " + item.text() + " ", topNode.baseUri());
                item.replaceWith(tn);
            }
        }
    }

    protected void append(Element node, StringBuilder sb, String tagName) {
        for (Element e : node.getElementsByTag(tagName)) {
            if(e.attr("class") != null && e.attr("class").contains("caption"))
                continue;
            
            String text = e.text();
            if (text.isEmpty() || text.length() < minParagraphText)
                continue;

            sb.append(text);
            sb.append("\n\n");
        }
    }
}
