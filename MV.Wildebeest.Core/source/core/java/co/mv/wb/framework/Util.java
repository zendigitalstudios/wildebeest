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

package co.mv.wb.framework;

/**
 * Utility functions.
 *
 * @since 4.0
 */
public class Util
{
	/**
	 * Extracts the name portion from an identifier URI.
	 *
	 * @param uri the identifier URI to extract the name from.
	 * @return the name extracted from the identifier URI.
	 * @since 4.0
	 */
	public static String nameFromUri(String uri)
	{
		if (uri == null) throw new ArgumentNullException("uri");

		// Use the final component of the URI (after the colon) as the name of the plugin
		int index = uri.lastIndexOf(":");

		if (index == -1)
		{
			throw new RuntimeException(String.format("invalid URI: %s", uri));
		}

		return uri.substring(index + 1);
	}

	/**
	 * Collapses each contiguous sequence of whitespace characters in a string down into a single printable space
	 * character.
	 *
	 * @param text the source text within which whitespace should be coalesced.
	 * @return the result of coalescing whitespace within the source text.
	 * @since 4.0
	 */
	public static String coalesceWhitespace(String text)
	{
		if (text == null) throw new ArgumentNullException("text");

		return text.replaceAll("\\s+", " ").trim();
	}

	/**
	 * Looks up the ResourcePlugin for the supplied ResourceType.
	 *
	 * @param value String that we check is UUID
	 * @return true if UUID can be created from param value
	 * @since 4.0
	 */
	public static boolean isUUID(String value)
	{
		final String UUIDmatcher = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
		if (value.matches(UUIDmatcher))
		{
			return true;
		}
		return false;
	}
}
