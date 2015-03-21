package to.joe.strangeweapons.exception;

public class BadPlayerMatchException extends Exception
{

    private static final long serialVersionUID = 1L;

    public BadPlayerMatchException(String string)
    {
        super(string);
    }
}