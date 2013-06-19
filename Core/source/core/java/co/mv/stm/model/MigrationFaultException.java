package co.mv.stm.model;

public class MigrationFaultException extends RuntimeException
{
	public MigrationFaultException(Exception cause)
	{
		super(cause);
	}
	
	public MigrationFaultException(
		String message)
	{
		super(message);
	}
}