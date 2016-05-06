package utils;

import java.util.*;

import org.eclipse.mylyn.wikitext.core.parser.*;
import org.eclipse.mylyn.wikitext.textile.core.*;

public class Textile {

    public static String toHTML(String textile) {
        String html = new MarkupParser(new TextileLanguage()).parseToHtml(textile);
        html = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
        return html;
    }

    public static String getTitle(String textile) {
        if(textile.length() == 0) {
            return "";
        }
        return textile.split("\n")[0].substring(3).trim();
    }

}
