import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * CRUD operations for the Movie table in the theater database.
 * Provides methods to insert, delete, update, and retrieve movie records.
 * 
 * @author Student
 * @version 1.0
 */
public class MovieCRUD
{
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    int i=0;
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    /**
     * Inserts a new movie record into the database.
     * 
     * @param movieID Unique identifier for the movie
     * @param title Title of the movie
     * @param genre Genre of the movie
     * @param duration Duration in minutes
     * @param description Plot description of the movie
     */
    public void movieInsert(int movieID, String title, String genre, int duration, String description)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);

            pstat = connection.prepareStatement(
                "INSERT INTO movie (title, genre, duration, description) VALUES (?, ?, ?, ?)"
            );
            pstat.setString(1, title);
            pstat.setString(2, genre);
            pstat.setInt(3, duration);
            pstat.setString(4, description);

            int i = pstat.executeUpdate();
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
                if (pstat != null) pstat.close();
                if (connection != null) connection.close();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Deletes a movie from the database.
     * 
     * @param movieID The ID of the movie to delete
     */
    public void movieDelete(int movieID)
    {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("DELETE FROM movie WHERE movieID=?");

            pstat.setInt(1, movieID);
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
     * Updates the title of a movie.
     * 
     * @param movieID The ID of the movie to update
     * @param title New title for the movie
     */
    public void movieUpdateTitle(int movieID, String title)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE movie SET title = ? WHERE movieID = ?");
            pstat.setString(1, title);
            pstat.setInt(2, movieID);
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
     * Updates the description of a movie.
     * 
     * @param movieID The ID of the movie to update
     * @param description New description for the movie
     */
    public void movieUpdateDescription(int movieID, String description)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE movie SET description = ? WHERE movieID = ?");
            pstat.setString(1, description);
            pstat.setInt(2, movieID);
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
     * Retrieves and displays movie details from the database.
     * 
     * @param movieID The ID of the movie to retrieve
     * @return Movie details as formatted string, or null if not found
     */
    public String movieRetrieve(int movieID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("SELECT * FROM movie WHERE movieID = ?");
            resultSet = pstat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Movies table:\n");
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