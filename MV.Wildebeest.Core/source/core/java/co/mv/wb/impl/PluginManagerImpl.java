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

package co.mv.wb.impl;

import co.mv.wb.AssertionType;
import co.mv.wb.MigrationPlugin;
import co.mv.wb.MigrationPluginType;
import co.mv.wb.MigrationType;
import co.mv.wb.MigrationTypeInfo;
import co.mv.wb.PluginGroup;
import co.mv.wb.PluginManager;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.framework.Util;
import org.reflections.Reflections;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link PluginManager}.
 *
 * @since 4.0
 */
public class PluginManagerImpl implements PluginManager
{
	private static final String SCAN_PACKAGE = "co.mv.wb";

	private final List<PluginGroup> pluginGroups;
	private final Map<String, MigrationPlugin> migrationPlugins;

	/**
	 * Constructs a new PluginManagerImpl with the specified plugin groups and migration plugins registered.
	 *
	 * @param pluginGroups     the plugin groups to be registered in this PluginManager.
	 * @param migrationPlugins the migration plugins to be registered in this PluginManager.
	 */
	public PluginManagerImpl(
		List<PluginGroup> pluginGroups,
		List<MigrationPlugin> migrationPlugins)
	{
		if (pluginGroups == null) throw new ArgumentNullException("pluginGroups");
		if (migrationPlugins == null) throw new ArgumentNullException("migrationPlugins");

		this.pluginGroups = pluginGroups;

		this.migrationPlugins = migrationPlugins
			.stream()
			.collect(Collectors.toMap(
				x ->
				{
					MigrationPluginType migrationPluginType = x.getClass().getAnnotation(MigrationPluginType.class);

					if (migrationPluginType == null)
					{
						throw new RuntimeException(String.format(
							"MigrationPlugin %s doesn't have a MigrationPluginType",
							x.getClass().getName()));
					}

					return migrationPluginType.uri();
				},
				x -> x));
	}

	@Override
	public List<PluginGroup> getPluginGroups()
	{
		return pluginGroups;
	}

	@Override
	public List<MigrationTypeInfo> getMigrationTypeInfos()
	{
		Reflections reflections = new Reflections(SCAN_PACKAGE);

		return reflections
			.getTypesAnnotatedWith(MigrationType.class)
			.stream()
			.map(
				migrationClass ->
				{
					MigrationType migrationType = migrationClass.getAnnotation(MigrationType.class);

					return new MigrationTypeInfo(
						migrationType.pluginGroupUri(),
						migrationType.uri(),
						Util.nameFromUri(migrationType.uri()),
						migrationType.description(),
						migrationType.example(),
						migrationClass);
				})
			.collect(Collectors.toList());
	}

	@Override
	public MigrationPlugin getMigrationPlugin(
		String uri)
	{
		if (uri == null) throw new ArgumentNullException("uri");

		if (!migrationPlugins.containsKey(uri))
		{
			throw new RuntimeException(String.format("no MigrationPlugin found for uri: %s", uri));
		}

		return migrationPlugins.get(uri);
	}

	@Override
	public List<AssertionType> getAssertionTypes()
	{
		Reflections reflections = new Reflections(SCAN_PACKAGE);

		return reflections
			.getTypesAnnotatedWith(AssertionType.class)
			.stream()
			.map(assertionClass -> assertionClass.getAnnotation(AssertionType.class))
			.collect(Collectors.toList());
	}
}
