package detik.com;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import master.scraper;

/**
 *
 * @author abdulrohim
 */
public final class detikScraper extends scraper {

    Pattern LINKSISIP = Pattern.compile("<table class=\"linksisip\">(.*?)</table>");
    Pattern PICSISIP = Pattern.compile("<table align=\"center\" class=\"pic_artikel_sisip_table\">(.*?)</table>");
    Pattern SCRIPT = Pattern.compile("<script type='text/javascript'>(.*?)</script>");
    Pattern NEWS_1 = Pattern.compile("<div id=\"comp-rich-text23\">(.*?)</div>");
    
    public detikScraper(String URL) throws MalformedURLException, SQLException, IOException {
        super(URL);
        this.LINK = Pattern.compile("<a  data-category=\"Subkanal detikNews\" data-action=\"Indeks\" data-label=\"List Berita\" href=\"(.*?)\" class=\"list\">");
        this.TITLE = Pattern.compile("<h1 class=\"jdl\">(.*?)</h1>");
        this.PUBLISHED = Pattern.compile("<div class=\"date\">(.*?)</div>");
        this.AUTHOR = Pattern.compile("<div class=\"author\">(.*?)</div>");
        this.NEWS = Pattern.compile("<div class=\"text_detail detail_area\" id=\"detikdetailtext\" >(.*?)</div>");

        ArrayList<String> tagValues;

        String str = this.getIndexPage();
        tagValues = this.getLink(this.LINK, str);
        System.out.println(tagValues.size());
        for (String tagValue : tagValues) {
//            this.insert(tagValue);
            String strb = this.getHTML(tagValue);
            System.out.println(tagValue);
            System.out.println(getTitle(strb));
            System.out.println(getNews(strb));
            System.out.println("======================================\n");
        }
    }

    private void insert(String url) throws SQLException {
        ResultSet rs = this.CON.getResult("select link from news where link = '" + url + "';");
        if (!rs.next()) {
            this.store(this.getData(url), this.URL.toString());
        }
    }

    @Override
    protected String getTitle(String str) {
        return this.matcher(this.TITLE, str);
    }

    @Override
    protected String getPublished(String str) {
        return this.matcher(this.PUBLISHED, str);
    }

    @Override
    protected String getAuthor(String str) {
        String author = this.matcher(this.AUTHOR, str);
        author = author.replaceAll("<strong>", "");
        author = author.replaceAll("</strong>", " ");
        return author;
    }

    @Override
    protected String getNews(String str) {
        str = str.replace('?', ' ');
        if (getPicSisip(str).size() > 0) {
            for (int i = 0; i < getPicSisip(str).size(); i++) {
                str = str.replaceAll(getPicSisip(str).get(i), "");
            }
        }
//        
        if (getLinkSisip(str).size() > 0) {
            for (int i = 0; i < getLinkSisip(str).size(); i++) {
                str = str.replaceAll("<table class=\"linksisip\">" + getLinkSisip(str).get(i) + "</table>", "");
            }
        }

        String news = this.matcher(this.NEWS, str);
        news = news.replace('[', ' ');
        news = news.replace(']', ' ');
        news = news.replace('(', ' ');
        news = news.replace(')', ' ');
        news = news.replaceAll("<table align=\"center\" class=\"pic_artikel_sisip_table\"></table>", "");

        if (getScript(news).size() > 0) {
            for (int i = 0; i < getScript(news).size(); i++) {
                news = news.replaceAll("<script type='text/javascript'>" + getScript(news).get(i) + "</script>", "");
            }
        }
        
        if (news.contains("<table align=\"center\" class=\"pic_artikel_sisip_table\"></table>")) {
            System.out.println("ada");
        }
        return news;
    }

    @Override
    protected String[] getData(String url) {
        String data[] = new String[6];
        String str;
        try {
            str = this.getHTML(url);
            data[0] = this.getTitle(str);
            data[0] = data[0].replaceAll("'", "");
            data[1] = this.getPublished(str);
            String[] in = data[1].split("\\s");
//            System.out.println(Arrays.toString(in));
            String month;
            switch (in[2]) {
                case ("Januari"):
                    month = "01";
                    break;
                case ("Februari"):
                    month = "02";
                    break;
                case ("Maret"):
                    month = "03";
                    break;
                case ("April"):
                    month = "04";
                    break;
                case ("Mei"):
                    month = "05";
                    break;
                case ("Juni"):
                    month = "06";
                    break;
                case ("Juli"):
                    month = "07";
                    break;
                case ("Agustus"):
                    month = "08";
                    break;
                case ("September"):
                    month = "09";
                    break;
                case ("Oktober"):
                    month = "10";
                    break;
                case ("November"):
                    month = "11";
                    break;
                default:
                    month = "12";
                    break;
            }
            in[3] = in[3].substring(0, in[3].length() - 1);
            data[2] = in[3] + "-" + month + "-" + in[1] + " " + in[4] + ":00";
            data[3] = this.getAuthor(str);
            data[4] = this.getNews(str);
            data[5] = url;
        } catch (IOException ex) {
            Logger.getLogger(detikScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    private ArrayList<String> matcherArray(Pattern pattern, String str) {
        ArrayList<String> tagValues = new ArrayList<>();
        Matcher matcherArticle = pattern.matcher(str);
        while (matcherArticle.find()) {
            tagValues.add(matcherArticle.group(1));
        }
        return tagValues;
    }

    private ArrayList<String> getLinkSisip(String str) {
        return this.matcherArray(LINKSISIP, str);
    }

    private ArrayList<String> getPicSisip(String str) {
        return this.matcherArray(this.PICSISIP, str);
    }

    private ArrayList<String> getScript(String str) {
        return this.matcherArray(this.SCRIPT, str);
    }

}
