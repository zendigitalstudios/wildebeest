package co.mv.wb;

import java.util.UUID;

/**
 * Indicates that a Migration refers to state that does not exist.
 *
 * @since                                       ???
 */
public class MigrationInvalidStateException extends Exception
{
	private UUID migrationId = null;
	private boolean migrationIdSet = false;
	/**
	 * Creates a new MigrationFailedException for the specified ID with the specified failure messages.
	 *
	 * @param       message                     the failure message
	 * @since                                   1.0
	 */
	public MigrationInvalidStateException(
		  UUID migrationId,
		  String message)
	{
		super(message);
		this.setMigrationId(migrationId);

	}

	/**
	 * Gets the ID of the Migration that failed
	 *
	 * @return                                  the ID of the Migration that failed
	 * @since                                   1.0
	 */
	public UUID getMigrationId() {
		if(!migrationIdSet) {
			throw new IllegalStateException("migrationId not set.  Use the HasMigrationId() method to check its state before accessing it.");
		}
		return migrationId;
	}

	private void setMigrationId(
		  UUID value) {
		if(value == null) {
			throw new IllegalArgumentException("migrationId cannot be null");
		}
		boolean changing = !migrationIdSet || migrationId != value;
		if(changing) {
			migrationIdSet = true;
			migrationId = value;
		}
	}

}
