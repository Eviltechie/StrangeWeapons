package to.joe.strangeweapons.datastorage;

public class DataStorageException extends Exception
{

    private static final long serialVersionUID = 1L;

    public DataStorageException(Throwable cause)
    {
        super(cause);
    }

    public DataStorageException(String cause)
    {
        super(cause);
    }

}
