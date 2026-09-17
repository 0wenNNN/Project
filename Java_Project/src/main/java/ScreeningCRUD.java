import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * CRUD operations for the Screening table in the theater database.
 * Provides methods to insert, delete, update, and retrieve screening records.
 * 
 * @author Student
 * @version 1.0
 */
public class ScreeningCRUD
{
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    int i = 0;
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    /**
     * Inserts a new screening into the database.
     * 
     * @param startTime The start time of the screening to be inserted
     */
    public void screeningInsert(Date startTime)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("INSERT INTO screening (startTime) VALUES (?)");
            pstat.setDate(1, startTime);
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
     * Deletes a screening from the database.
     * 
     * @param screeningID The ID of the screening to be deleted
     */
    public void screeningDelete(int screeningID)
    {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("DELETE FROM screening WHERE screeningID=?");

            pstat.setInt(1, screeningID);
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
     * Updates the start time of a screening.
     * 
     * @param screeningID The ID of the screening to be updated
     * @param startTime The new start time to be set for the specified screening
     */
    public void screeningUpdate(int screeningID, LocalDateTime startTime)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE screening SET startTime = ? WHERE screeningID = ?");
            pstat.setTimestamp(1, Timestamp.valueOf(startTime));
            pstat.setInt(2, screeningID);

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
     * Retrieves and displays screening details from the database.
     * 
     * @param screeningID The ID of the screening to be retrieved
     * @return Screening details as formatted string, or null if not found
     */
    public String screenRetrieve(int screeningID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("SELECT * FROM screening WHERE screeningID = ?");
            resultSet = pstat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Screening table:\n");
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