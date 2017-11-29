package scrapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

/**
 *
 * @author abdulrohim
 */
public class Scrapter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, SQLException, IOException {
        new detik.com.detikScraper("https://m.detik.com/news/indeks");
    }
    
}
