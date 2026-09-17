import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * CRUD operations for the Review table in the theater database.
 * Provides methods to insert, delete, update, and retrieve review records.
 * 
 * @author Student
 * @version 1.0
 */
public class ReviewCRUD
{
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    int i = 0;
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    /**
     * Inserts a new review into the database.
     * 
     * @param userID The ID of the user submitting the review
     * @param movieID The ID of the movie being reviewed
     * @param rating The rating given by the user (e.g., 1-5)
     * @param comment The comment provided by the user about the movie
     */
    public void reviewInsert(int userID, int movieID, int rating, String comment)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement(
                "INSERT INTO review (userID, movieID, rating, comment, reviewDate) VALUES (?,?,?,?, NOW())");
            pstat.setInt(1, userID);
            pstat.setInt(2, movieID);
            pstat.setInt(3, rating);
            pstat.setString(4, comment);
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
     * Deletes a review from the database.
     * 
     * @param reviewID The ID of the review to be deleted
     */
    public void reviewDelete(int reviewID)
    {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("DELETE FROM review WHERE reviewID=?");

            pstat.setInt(1, reviewID);
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
     * Updates the rating of a review.
     * 
     * @param reviewID The ID of the review to be updated
     * @param rating The new rating to be set for the specified review
     */
    public void reviewUpdateRating(int reviewID, int rating)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE review SET rating = ? WHERE reviewID = ?");
            pstat.setInt(1, rating);
            pstat.setInt(2, reviewID);
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
     * Updates the comment of a review.
     * 
     * @param reviewID The ID of the review to be updated
     * @param comment The new comment to be set for the specified review
     */
    public void reviewUpdateComment(int reviewID, String comment)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("UPDATE review SET comment = ? WHERE reviewID = ?");
            pstat.setString(1, comment);
            pstat.setInt(2, reviewID);
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
     * Retrieves and displays review details from the database.
     * 
     * @param reviewID The ID of the review to be retrieved
     * @return Review details as formatted string, or null if not found
     */
    public String reviewRetrieve(int reviewID)
    {
        try
        {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement("SELECT * FROM review WHERE reviewID = ?");
            resultSet = pstat.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Reviews table:\n");
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

    /**
     * Retrieves all reviews for a specific movie.
     * 
     * @param movieID The ID of the movie for which reviews are to be retrieved
     * @return A list of formatted review strings for the specified movie
     */
    public java.util.List<String> getReviewsForMovie(int movieID) {
        java.util.List<String> reviews = new java.util.ArrayList<>();
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            pstat = connection.prepareStatement(
                "SELECT review.rating, review.comment, review.reviewDate, users.userName FROM review " +
                "INNER JOIN users ON review.userID = users.userID WHERE review.movieID = ? ORDER BY review.reviewDate DESC");
            pstat.setInt(1, movieID);
            resultSet = pstat.executeQuery();
            while (resultSet.next()) {
                reviews.add("Rating: " + resultSet.getInt("review.rating") + "/5 - " + resultSet.getString("review.comment") + 
                    " (by " + resultSet.getString("users.userName") + " on " + resultSet.getTimestamp("review.reviewDate") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (pstat != null) pstat.close(); if (connection != null) connection.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return reviews;
    }
}