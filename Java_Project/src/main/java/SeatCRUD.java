import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * CRUD operations for the Seat table in the theater database.
 * Provides methods to insert, delete, update, and retrieve seat records.
 * 
 * @author Student
 * @version 1.0
 */
public class SeatCRUD
{
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    int i = 0;
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    /**
     * Inserts a new seat into the database.
     * 
     * @param seatID Unique identifier for the seat
     */
    public void seatInsert(int seatID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("INSERT INTO seat (seatID, isAvailable) VALUES (?, 1)");
            pstat.setInt(1, seatID);
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
     * Deletes a seat from the database.
     * 
     * @param seatID The ID of the seat to delete
     */
    public void seatDelete(int seatID)
    {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("DELETE FROM seat WHERE seatID=?");

            pstat.setInt(1, seatID);
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
     * Updates the availability status of a seat.
     * 
     * @param seatID The ID of the seat to update
     * @param isAvailable 1 if available, 0 if not available
     */
    public void seatUpdateAvailability(int seatID, int isAvailable)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE seat SET isAvailable = ? WHERE seatID = ?");
            pstat.setInt(1, isAvailable);
            pstat.setInt(2, seatID);

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
     * Retrieves and displays seat details from the database.
     * 
     * @param seatID The ID of the seat to retrieve
     * @return Seat details as formatted string, or null if not found
     */
    public String seatRetrieve(int seatID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("SELECT * FROM seat WHERE seatID = ?");
            resultSet = pstat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Seat table:\n");
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