import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * CRUD operations for the Purchase table in the theater database.
 * Provides methods to insert, delete, update, and retrieve purchase records.
 * 
 * @author Student
 * @version 1.0
 */
public class PurchaseCRUD
{
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    int i = 0;
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    /**
     * Inserts a new purchase into the database.
     * 
     * @param totalPrice The total price of the purchase
     */
    public void purchaseInsert(int totalPrice)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("INSERT INTO purchase (totalPrice, purchaseDate) VALUES (?, NOW())");
            pstat.setInt(1, totalPrice);
            i = pstat.executeUpdate();
            System.out.println(i + " record inserted");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstat.close();
                connection.close();
            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    /**
     * Deletes a purchase from the database.
     * 
     * @param purchaseID The ID of the purchase to be deleted
     */
    public void purchaseDelete(int purchaseID)
    {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("DELETE FROM purchase WHERE purchaseID=?");

            pstat.setInt(1, purchaseID);
            pstat.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstat.close();
                connection.close();
            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    /**
     * Updates the purchase date of a purchase.
     * 
     * @param purchaseID The ID of the purchase to be updated
     * @param purchaseDate The new purchase date to be set for the specified purchase
     */
    public void purchaseUpdate(int purchaseID, LocalDateTime purchaseDate)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE purchase SET purchaseDate = ? WHERE purchaseID = ?");
            pstat.setTimestamp(1, Timestamp.valueOf(purchaseDate));
            pstat.setInt(2, purchaseID);

            pstat.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstat.close();
                connection.close();
            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    /**
     * Retrieves and displays purchase details from the database.
     * 
     * @param purchaseID The ID of the purchase to be retrieved
     * @return Purchase details as formatted string, or null if not found
     */
    public String purchaseRetrieve(int purchaseID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("SELECT * FROM purchase WHERE purchaseID = ?");
            resultSet = pstat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Purchase table:\n");
            for(int i=1; i<=numberOfColumns; i++)
            {
                System.out.println(metaData.getColumnName(i));
                System.out.println();
            }
            while(resultSet.next()){
                for (int i = 1; i <= numberOfColumns; i++)
                    System.out.print(resultSet.getObject(i) + "\t\t");
                System.out.println();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstat.close();
                connection.close();
            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }
        return null;
    }
}