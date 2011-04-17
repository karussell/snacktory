package com.jreadability.main;

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

    private static final Logger logger = LoggerFactory.getLogger(OutputFormatter.class);
    private Element topNode;

    public OutputFormatter init(Element topNode) {
        this.topNode = topNode;
        topNode.text();
        removeNodesWithNegativeScores();
        convertLinksToText();
        replaceTagsWithText();
        return this;
    }

    /**
     * takes an element and turns the P tags into \n\n     
     */
    public String getFormattedText() {
        StringBuilder sb = new StringBuilder();
        append(topNode.getAllElements(), sb, "p");
        String str = Helper.innerTrim(sb.toString());

        // no subelements
        if (str.length() < 100 && topNode.ownText().length() > 100)
            return topNode.text();

        if (str.isEmpty()) {
            append(topNode.getAllElements(), sb, "span");
            str = Helper.innerTrim(sb.toString());
        }

        if (str.isEmpty()) {
            append(topNode.getAllElements(), sb, "pre");
            str = Helper.innerTrim(sb.toString());
        }

        return str;
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
            if (score < 1) {
                item.remove();
            }
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

    private void append(Elements nodes, StringBuilder sb, String tagName) {
        for (Element e : nodes) {
            if (e.tagName().equals(tagName)) {
                String text = e.text().trim();
                if (text.isEmpty())
                    continue;

                sb.append(text);
                sb.append("\n\n");
            }
        }
    }
}
