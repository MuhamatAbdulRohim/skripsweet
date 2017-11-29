package master;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author abdulrohim
 */
public final class koneksi {

    Connection con;
    Statement stm;

    public koneksi() throws SQLException {
        this.openConnection();
    }

    public void executeQuery(String query) throws SQLException {
        this.stm.execute(query);
    }

    public ResultSet getResult(String query) throws SQLException {
        ResultSet rs = stm.executeQuery(query);
        return rs;
    }

    public Connection getConnection() {
        return this.con;
    }

    public void closeConnection() throws SQLException {
        this.con.close();
    }

    public void openConnection() throws SQLException {
        this.con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/db_news", "root", "");
        this.stm = (Statement) this.con.createStatement();
    }
    
}
