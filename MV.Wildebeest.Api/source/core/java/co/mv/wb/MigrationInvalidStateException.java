package co.mv.wb;

import java.util.UUID;

/**
 * Indicates that a Migration refers to state that does not exist.
 *
 * @since                                       ???
 */
public class MigrationInvalidStateException extends Exception
{
	/**
	 * Creates a new MigrationFailedException for the specified ID with the specified failure messages.
	 *
	 * @param       message                     the failure message
	 * @since                                   1.0
	 */
	public MigrationInvalidStateException(
		  String message)
	{
		super(message);

	}

}
