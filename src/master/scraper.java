package master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author abdulrohim
 */
public abstract class scraper {

    protected URL URL;
    protected String HTML = "";
    protected Pattern TITLE;
    protected Pattern PUBLISHED;
    protected Pattern AUTHOR;
    protected Pattern LINK;
    protected Pattern NEWS;
    protected koneksi CON;

    public scraper(String URL) throws MalformedURLException, SQLException {
        this.URL = new URL(URL);
        this.CON = new koneksi();
    }

    protected String getIndexPage() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.URL.openStream(), "UTF-8"));) {
            for (String line; (line = reader.readLine()) != null;) {
                HTML = HTML + line;
            }
        }
        return HTML;
    }

    protected String getHTML(String link) throws IOException {
        URL page = new URL(link);
        String html = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(page.openStream(), "UTF-8"));) {
            for (String line; (line = reader.readLine()) != null;) {
                html = html + line;
            }
        }
        return html;
    }

    protected boolean store(String data[], String site_url) throws SQLException {
        ResultSet rs = CON.getResult("select id from website where site_link = '" + site_url + "';");
        rs.next();
        String id = rs.getString(1);
        String query = "insert into news values(null," + id + ",'" + data[0] + "','" + data[1] + "','" + data[2] + "','" + data[3] + "','" + data[4] + "','" + data[5] + "', CURRENT_TIMESTAMP);";
        try {
            this.CON.executeQuery(query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(scraper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    protected String matcher(Pattern pattern, String str) {
        String tagValues = "";
        Matcher matcherArticle = pattern.matcher(str);
        while (matcherArticle.find()) {
            tagValues = matcherArticle.group(1);
        }
        return tagValues;
    }

    protected ArrayList<String> getLink(Pattern pattern, String str) {
        ArrayList<String> tagValues = new ArrayList<>();
        Matcher matcherArticle = pattern.matcher(str);
        while (matcherArticle.find()) {
            tagValues.add(matcherArticle.group(1));
        }
        return tagValues;
    }

    protected abstract String getTitle(String str);

    protected abstract String getPublished(String str);

    protected abstract String getAuthor(String str);

    protected abstract String getNews(String str);

    protected abstract String[] getData(String url);

}
