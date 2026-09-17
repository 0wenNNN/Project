
/**
 * Validates user credentials for login.
 * 
 * @author Student
 * @version 1.0
 */
public class verifier
{
    LoginWrongInfoException loginWrongInfoException = new LoginWrongInfoException();
    /**
     * @param username The username to be verified
     * @param password The password to be verified
     * @return true if the username and password are correct, false otherwise
     */
    public boolean verify(String username, String password)
    {
        if(username.equals("admin") && password.equals("admin"))
        {
            return true;
        }
        else
        {
            try
            {
                throw loginWrongInfoException;
            }
            catch(LoginWrongInfoException e)
            {
                System.out.println(e.getMessage());
                return false;
            }
        }
    }
}
