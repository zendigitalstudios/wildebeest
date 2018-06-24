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

package co.mv.wb.plugin.fake;

import co.mv.wb.Assertion;
import co.mv.wb.AssertionResponse;
import co.mv.wb.AssertionType;
import co.mv.wb.Instance;
import co.mv.wb.ModelExtensions;
import co.mv.wb.ResourceType;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.plugin.base.BaseAssertion;
import co.mv.wb.plugin.base.ImmutableAssertionResponse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * {@link Assertion} plugin for the Fake plugin implementation.
 *
 * @since 1.0
 */
@AssertionType(
	pluginGroupUri = "co.mv.wb:Fake",
	uri = "co.mv.wb.fake:TagAssertion",
	description = "Verifies that the Tag value of the FakeIntance is as expected",
	example =
		""
)
public class TagAssertion extends BaseAssertion
{
	private final String tag;
	private int calledNTimes = 0;

	public TagAssertion(
		UUID assertionId,
		int seqNum,
		String tag)
	{
		super(assertionId, seqNum);

		this.tag = tag;
	}

	@Override
	public String getDescription()
	{
		return "Tag";
	}

	public String getTag()
	{
		return this.tag;
	}

	public int getCalledNTimes()
	{
		return this.calledNTimes;
	}

	@Override
	public List<ResourceType> getApplicableTypes()
	{
		return Arrays.asList(
			FakeConstants.Fake);
	}

	@Override
	public AssertionResponse perform(Instance instance)
	{
		if (instance == null) throw new ArgumentNullException("instance");

		FakeInstance fake = ModelExtensions.As(instance, FakeInstance.class);
		if (fake == null)
		{
			throw new IllegalArgumentException("instance must be a FakeInstance");
		}

		this.calledNTimes += 1;
		AssertionResponse response;

		if (this.getTag().equals(fake.getTag()))
		{
			response = new ImmutableAssertionResponse(true, "Tag is as expected");
		}
		else
		{
			response = new ImmutableAssertionResponse(false, "Tag not as expected");
		}

		return response;
	}
}
