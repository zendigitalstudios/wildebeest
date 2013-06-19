package co.mv.stm.cli;

import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;

public class WildebeestCommandTests
{
	@Test public void parseForValidStateCommandWithLongArgsSucceeds()
	{
		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "state", "--resource:foo", "--instance:bar" });
		
		Assert.assertEquals("wb.command", CommandType.State, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
	}

	@Test public void parseForValidStateCommandWithShortArgsSucceeds()
	{
		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "state", "-r:foo", "-i:bar" });
		
		Assert.assertEquals("wb.command", CommandType.State, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
	}
	
	@Test public void parseForValidMigrateCommandByLabelWithLongArgsSucceeds()
	{
		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "migrate", "--resource:foo", "--instance:bar", "--targetState:created" });
		
		Assert.assertEquals("wb.command", CommandType.Migration, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
		Assert.assertEquals("wb.targetStateLabel", "created", wb.getTargetStateLabel());
	}

	@Test public void parseForValidMigrateCommandByLabelWithShortArgsSucceeds()
	{
		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "migrate", "-r:foo", "-i:bar", "-t:created" });
		
		Assert.assertEquals("wb.command", CommandType.Migration, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
		Assert.assertEquals("wb.targetStateLabel", "created", wb.getTargetStateLabel());
	}
	
	@Test public void parseForValidMigrateCommandByIdWithLongArgsSucceeds()
	{
		UUID targetStateId = UUID.randomUUID();

		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "migrate", "--resource:foo", "--instance:bar",
			"--targetState:" + targetStateId.toString() });
		
		Assert.assertEquals("wb.command", CommandType.Migration, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
		Assert.assertEquals("wb.targetStateId", targetStateId, wb.getTargetStateId());
	}

	@Test public void parseForValidMigrateCommandByIdWithShortArgsSucceeds()
	{
		UUID targetStateId = UUID.randomUUID();

		WildebeestCommand wb = new WildebeestCommand();
		wb.parse(new String[] { "migrate", "-r:foo", "-i:bar", "-t:" + targetStateId.toString() });
		
		Assert.assertEquals("wb.command", CommandType.Migration, wb.getCommand());
		Assert.assertEquals("wb.resource", "foo", wb.getResource());
		Assert.assertEquals("wb.instance", "bar", wb.getInstance());
		Assert.assertEquals("wb.targetStateId", targetStateId, wb.getTargetStateId());
	}
}