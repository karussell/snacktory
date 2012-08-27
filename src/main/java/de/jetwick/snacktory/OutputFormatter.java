package de.jetwick.snacktory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

/**
 * @author goose | jim
 * @author karussell
 *
 * this class will be responsible for taking our top node and stripping out junk we don't want and
 * getting it ready for how we want it presented to the user
 */
public class OutputFormatter {

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
     * If there are elements inside our top node that have a negative gravity score remove them
     */
    protected void removeNodesWithNegativeScores(Element topNode) {
        Elements gravityItems = topNode.select("*[gravityScore]");
        for (Element item : gravityItems) {
            int score = Integer.parseInt(item.attr("gravityScore"));
            if (score < 0 || item.text().length() < minParagraphText)
                item.remove();
        }
    }

    protected void append(Element node, StringBuilder sb, String tagName) {
        for (Element e : node.getElementsByTag(tagName)) {
            Element p = e.parent();
            if ((e.attr("class") != null && e.attr("class").toLowerCase().contains("caption"))
                    || (p.attr("class") != null && p.attr("class").toLowerCase().contains("caption")))
                continue;

            String text = e.text();
            if (text.isEmpty() || text.length() < minParagraphText || text.length() > SHelper.countLetters(text) * 2)
                continue;

            sb.append(text);
            sb.append("\n\n");
        }
    }
}
