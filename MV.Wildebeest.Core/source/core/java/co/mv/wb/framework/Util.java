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

public class Util
{
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

	public static String coalesceWhiteSpaces(String text){
		return text.replaceAll("\\s+", " ").trim();
	}
}

