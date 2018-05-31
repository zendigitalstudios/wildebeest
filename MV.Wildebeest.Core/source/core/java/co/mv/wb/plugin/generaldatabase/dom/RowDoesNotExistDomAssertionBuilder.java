// Wildebeest Migration Framework
// Copyright © 2013 - 2018, Matheson Ventures Pte Ltd
//
// This file is part of Wildebeest
//
// Wildebeest is free software: you can redistribute it and/or modify it under
// the terms of the GNU General Public License v2 as published by the Free
// Software Foundation.
//
// Wildebeest is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with
// Wildebeest.  If not, see http://www.gnu.org/licenses/gpl-2.0.html

package co.mv.wb.plugin.generaldatabase.dom;

import co.mv.wb.Assertion;
import co.mv.wb.AssertionBuilder;
import co.mv.wb.Messages;
import co.mv.wb.PluginBuildException;
import co.mv.wb.V;
import co.mv.wb.plugin.base.dom.BaseDomAssertionBuilder;
import co.mv.wb.plugin.generaldatabase.RowDoesNotExistAssertion;
import co.mv.wb.plugin.generaldatabase.RowExistsAssertion;

import java.util.Optional;
import java.util.UUID;

import static co.mv.wb.framework.Util.coalesceWhiteSpaces;

/**
 * An {@link AssertionBuilder} that builds a {@link RowDoesNotExistAssertion} from a DOM {@link org.w3c.dom.Element}.
 * 
 * @author                                      Brendon Matheson
 * @since                                       1.0
 */
public class RowDoesNotExistDomAssertionBuilder extends BaseDomAssertionBuilder
{
	@Override public Assertion build(
		UUID assertionId,
		int seqNum) throws
			PluginBuildException
	{
		Optional<String> sql = this.tryGetString("sql");
		Optional<String> description = this.tryGetString("description");

		// Validate
		Messages messages = new Messages();
		if (!sql.isPresent())
		{
			V.elementMissing(messages, assertionId, "sql", RowExistsAssertion.class);
		}
		
		if (!description.isPresent())
		{
			V.elementMissing(messages, assertionId, "description", RowExistsAssertion.class);
		}

		if (messages.size() > 0)
		{
			throw new PluginBuildException(messages);
		}


		return new RowDoesNotExistAssertion(assertionId, coalesceWhiteSpaces(description.get()), seqNum, sql.get());
	}
}
