/**
 * Exception thrown when login credentials are invalid.
 * 
 * @author Student
 * @version 1.0
 */
public class LoginWrongInfoException extends Exception
{
    public LoginWrongInfoException()
    {
        super("Wrong username or password");
    }
    public LoginWrongInfoException(String message)
    {
        super(message);
    }
}