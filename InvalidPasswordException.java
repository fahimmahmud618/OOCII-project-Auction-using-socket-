public class InvalidPasswordException extends Exception{

    public InvalidPasswordException()
    {
        super("Wrong Password");
    }
    public InvalidPasswordException(String str)
    {
        super(str);
    }
}
