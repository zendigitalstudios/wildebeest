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

import co.mv.wb.AssertionFailedException;
import co.mv.wb.AssertionResponse;
import co.mv.wb.AssertionResult;
import co.mv.wb.AssertionType;
import co.mv.wb.FileLoadException;
import co.mv.wb.IndeterminateStateException;
import co.mv.wb.Instance;
import co.mv.wb.InvalidStateSpecifiedException;
import co.mv.wb.JumpStateFailedException;
import co.mv.wb.LoaderFault;
import co.mv.wb.Migration;
import co.mv.wb.MigrationFailedException;
import co.mv.wb.MigrationInvalidStateException;
import co.mv.wb.MigrationPlugin;
import co.mv.wb.MigrationType;
import co.mv.wb.MigrationTypeInfo;
import co.mv.wb.MissingReferenceException;
import co.mv.wb.OutputFormatter;
import co.mv.wb.PluginBuildException;
import co.mv.wb.PluginManager;
import co.mv.wb.Resource;
import co.mv.wb.ResourcePlugin;
import co.mv.wb.ResourceType;
import co.mv.wb.State;
import co.mv.wb.TargetNotSpecifiedException;
import co.mv.wb.UnknownStateSpecifiedException;
import co.mv.wb.Wildebeest;
import co.mv.wb.WildebeestApi;
import co.mv.wb.XmlValidationException;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.framework.Util;
import co.mv.wb.plugin.base.ImmutableAssertionResult;
import co.mv.wb.plugin.base.dom.DomInstanceLoader;
import co.mv.wb.plugin.base.dom.DomPlugins;
import co.mv.wb.plugin.base.dom.DomResourceLoader;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides a generic interface that can be adapted to different environments.  For example the WildebeestCommand
 * command-line interface delegates to WildebeestApiImpl to drive commands.
 *
 * @since 1.0
 */
public class WildebeestApiImpl implements WildebeestApi
{
	private static final String RESOURCE_XSD = "resource.xsd";
	private static final String INSTANCE_XSD = "instance.xsd";

	private final PrintStream output;
	private Map<ResourceType, ResourcePlugin> resourcePlugins = null;
	private PluginManager pluginManager = null;


	/**
	 * Creates a new WildebeestApiImpl using the supplied {@link PrintStream} for user output and the supplied
	 * ResourceHelper.
	 *
	 * @param output the PrintStream that should be used for output to the user.
	 * @since 1.0
	 */
	public WildebeestApiImpl(
		PrintStream output)
	{
		if (output == null) throw new ArgumentNullException("output");

		this.output = output;
	}

	private Map<ResourceType, ResourcePlugin> getResourcePlugins()
	{
		return this.resourcePlugins;
	}

	public void setResourcePlugins(Map<ResourceType, ResourcePlugin> value)
	{
		this.resourcePlugins = value;
	}

	public PluginManager getPluginManager()
	{
		return this.pluginManager;
	}

	public void setPluginManager(PluginManager value)
	{
		this.pluginManager = value;
	}


	public Resource loadResource(
		File resourceFile) throws
		FileLoadException,
		LoaderFault,
		PluginBuildException,
		XmlValidationException,
		MissingReferenceException
	{
		if (resourceFile == null) throw new ArgumentNullException("resourceFile");

		// Get the absolute file for this resource - this ensures that getParentFile works correctly
		resourceFile = resourceFile.getAbsoluteFile();

		// Load Resource
		String resourceXml;
		try
		{
			resourceXml = readAllText(resourceFile);
		}
		catch (IOException ex)
		{
			throw new FileLoadException(resourceFile);
		}

		Resource resource = null;

		if (resourceXml != null)
		{
			WildebeestApiImpl.validateResourceXml(resourceXml);
			DomResourceLoader resourceLoader = DomPlugins.resourceLoader(
				ResourceTypeServiceBuilder
					.create()
					.withFactoryResourceTypes()
					.build(),
				resourceXml);

			resource = resourceLoader.load(resourceFile.getParentFile());
		}

		return resource;
	}

	/**
	 * Validates the supplied XML string as a resource definition.
	 *
	 * @param resourceXml the XML document to validate as a resource.
	 * @throws XmlValidationException if the supplied XML document is not a valid resource according to the
	 *                                XML schema.
	 * @since 4.0
	 */
	public static void validateResourceXml(
		String resourceXml) throws
		XmlValidationException
	{
		if (resourceXml == null) throw new ArgumentNullException("resourceXml");

		WildebeestApiImpl.validateXml(resourceXml, WildebeestApiImpl.RESOURCE_XSD);
	}

	public Instance loadInstance(
		File instanceFile) throws
		FileLoadException,
		LoaderFault,
		PluginBuildException,
		XmlValidationException
	{
		if (instanceFile == null) throw new ArgumentNullException("instanceFile");

		// Load Instance
		String instanceXml;
		try
		{
			instanceXml = readAllText(instanceFile);
		}
		catch (IOException ex)
		{
			throw new FileLoadException(instanceFile);
		}

		Instance instance = null;

		if (instanceXml != null)
		{
			validateInstanceXml(instanceXml);
			DomInstanceLoader instanceLoader = new DomInstanceLoader(
				DomPlugins.instanceBuilders(),
				instanceXml);
			instance = instanceLoader.load();
		}

		return instance;
	}

	/**
	 * Validates the supplied XML string as an instance definition.
	 *
	 * @param instanceXml the XML document to validate as an instance.
	 * @throws XmlValidationException if the supplied XML document is not a valid instance according to the
	 *                                XML schema.
	 * @since 4.0
	 */
	public static void validateInstanceXml(
		String instanceXml) throws
		XmlValidationException
	{
		if (instanceXml == null) throw new ArgumentNullException("instanceXml");

		WildebeestApiImpl.validateXml(instanceXml, WildebeestApiImpl.INSTANCE_XSD);
	}

	public List<AssertionResult> assertState(
		Resource resource,
		Instance instance) throws
		IndeterminateStateException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		State state = resourcePlugin.currentState(
			resource,
			instance);

		List<AssertionResult> result = new ArrayList<>();

		state.getAssertions().forEach(
			assertion ->
			{
				output.println(OutputFormatter.assertionStart(assertion));

				AssertionResponse response = assertion.perform(instance);

				output.println(OutputFormatter.assertionComplete(
					assertion,
					response));

				result.add(new ImmutableAssertionResult(
					assertion.getAssertionId(),
					response.getResult(),
					response.getMessage()));
			});

		return result;
	}

	public void state(
		Resource resource,
		Instance instance) throws
		IndeterminateStateException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		State state = resourcePlugin.currentState(
			resource,
			instance);

		if (state == null)
		{
			output.println("Current state: non-existent");
		}
		else
		{
			if (state.getLabel().isPresent())
			{
				output.println(String.format("Current state: %s", state.getLabel()));
			}
			else
			{
				output.println(String.format("Current state: %s", state.getStateId().toString()));
			}

			this.assertState(
				resource,
				instance);
		}
	}

	public void migrate(
		Resource resource,
		Instance instance,
		Optional<String> targetState) throws
		AssertionFailedException,
		TargetNotSpecifiedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");
		if (targetState == null) throw new ArgumentNullException("targetState");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		// Resolve the target state
		Optional<String> ts = targetState.isPresent()
			? targetState
			: resource.getDefaultTarget();

		// Perform migration
		if (!ts.isPresent())
		{
			throw new TargetNotSpecifiedException();
		}

		UUID targetStateId = Wildebeest.findState(resource, ts.get()).getStateId();

		State currentState = resourcePlugin.currentState(
			resource,
			instance);

		UUID currentStateId = currentState == null ? null : currentState.getStateId();

		List<List<Migration>> paths = new ArrayList<>();
		List<Migration> thisPath = new ArrayList<>();

		WildebeestApiImpl.findPaths(resource, paths, thisPath, currentStateId, targetStateId);

		if (paths.size() != 1)
		{
			throw new RuntimeException("multiple possible paths found");
		}

		List<Migration> path = paths.get(0);

		validateMigrationStates(resource);

		if (currentState != null)
		{
			List<AssertionResult> initialAssertionResults = this.assertState(
				resource,
				instance);

			WildebeestApiImpl.throwIfFailed(currentState.getStateId(), initialAssertionResults);
		}

		for (Migration migration : path)
		{
			String migrationTypeUri = migration.getClass().getAnnotation(MigrationType.class).uri();
			MigrationPlugin migrationPlugin = this.getPluginManager().getMigrationPlugin(migrationTypeUri);

			Optional<State> fromState = migration.getFromState().map(stateId -> Wildebeest.findState(
				resource,
				stateId));
			Optional<State> toState = migration.getToState().map(stateId -> Wildebeest.findState(
				resource,
				stateId));

			// Migrate to the next state
			output.println(OutputFormatter.migrationStart(
				resource,
				migration,
				fromState,
				toState));

			migrationPlugin.perform(
				output,
				migration,
				instance);

			output.println(OutputFormatter.migrationComplete(
				resource,
				migration));

			// Update the state
			resourcePlugin.setStateId(
				output,
				resource,
				instance,
				toState.get().getStateId());

			// Assert the new state
			List<AssertionResult> assertionResults = this.assertState(
				resource,
				instance);

			WildebeestApiImpl.throwIfFailed(toState.get().getStateId(), assertionResults);
		}
	}

	public void jumpstate(
		Resource resource,
		Instance instance,
		String targetState) throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		JumpStateFailedException,
		UnknownStateSpecifiedException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");
		if (targetState != null && "".equals(targetState.trim()))
			throw new IllegalArgumentException("targetState cannot be empty");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		UUID targetStateId = Wildebeest.findState(resource, targetState).getStateId();

		State state = Wildebeest.findState(
			resource,
			targetStateId.toString());

		if (targetState == null)
		{
			throw new JumpStateFailedException("This resource does not have a state with ID " +
				targetStateId.toString());
		}

		// Assert the new state
		List<AssertionResult> assertionResults = this.assertState(
			resource,
			instance);

		WildebeestApiImpl.throwIfFailed(state.getStateId(), assertionResults);

		resourcePlugin.setStateId(
			output,
			resource,
			instance,
			targetStateId);
	}

	@Override
	public String describePlugins()
	{
		StringBuilder output = new StringBuilder();

		output.append("<manifest>");

		output.append("<groups>");

		this.getPluginManager()
			.getPluginGroups()
			.stream()
			.forEach(x -> output
				.append("<group ")
				.append("uri=\"").append(x.getUri()).append("\" ")
				.append("name=\"").append(x.getName()).append("\" ")
				.append("title=\"").append(x.getTitle()).append("\" ")
				.append(" />"));

		output.append("</groups>");

		output.append("<plugins>");

		// Migrations
		for (MigrationTypeInfo info : this.getPluginManager().getMigrationTypeInfos())
		{
			WildebeestApiImpl.pluginElement(
				output,
				"migration",
				info.getPluginGroupUri(),
				info.getUri(),
				info.getName(),
				info.getDescription(),
				info.getExample());
		}

		// Assertions
		for (AssertionType info : this.getPluginManager().getAssertionTypes())
		{
			WildebeestApiImpl.pluginElement(
				output,
				"assertion",
				info.pluginGroupUri(),
				info.uri(),
				Util.nameFromUri(info.uri()),
				info.description(),
				info.example());
		}

		output.append("</plugins>");

		output.append("</manifest>");

		return output.toString();
	}

	private static void pluginElement(
		StringBuilder output,
		String type,
		String groupUri,
		String uri,
		String name,
		String description,
		String example)
	{
		String exampleEsc = example
			.replaceAll("\\<", "&lt;")
			.replaceAll("\\>", "&gt;")
			.replaceAll("\"", "&quot;")
			.replaceAll("'", "&apos;");

		output
			.append("<plugin type=\"").append(type).append("\">")
			.append("<group>").append(groupUri).append("</group>")
			.append("<uri>").append(uri).append("</uri>")
			.append("<name>").append(name).append("</name>")
			.append("<description>");

		String[] descriptionLines = description.split("\\n");

		for (String descriptionLine : descriptionLines)
		{
			output.append("<line>").append(descriptionLine).append("</line>");
		}

		output
			.append("</description>")
			.append("<example>").append(exampleEsc).append("</example>")
			.append("</plugin>");
	}

	private static UUID stateIdForLabel(
		Resource resource,
		String label)
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (label == null) throw new ArgumentNullException("label");
		if ("".equals(label)) throw new IllegalArgumentException("label cannot be empty");

		State result = null;

		for (State check : resource.getStates())
		{
			if (check.getLabel().map(label::equals).orElse(false))
			{
				result = check;
			}
		}

		return result == null ? null : result.getStateId();
	}

	private static UUID getTargetStateId(
		Resource resource,
		String targetState) throws
		InvalidStateSpecifiedException,
		UnknownStateSpecifiedException
	{
		if (resource == null) throw new ArgumentNullException("resource");

		final String stateSpecificationRegex = "[a-zA-Z0-9][a-zA-Z0-9\\-\\_ ]+[a-zA-Z0-9]";
		if (targetState != null && !targetState.matches(stateSpecificationRegex))
		{
			throw new InvalidStateSpecifiedException(targetState);
		}

		UUID targetStateId = null;
		if (targetState != null)
		{
			try
			{
				targetStateId = UUID.fromString(targetState);
			}
			catch (IllegalArgumentException e)
			{
				targetStateId = WildebeestApiImpl.stateIdForLabel(
					resource,
					targetState);
			}

			// If we still could not find the specified state, then throw
			if (targetStateId == null)
			{
				throw new UnknownStateSpecifiedException(targetState);
			}
		}

		return targetStateId;
	}

	private static String readAllText(File file) throws
		IOException
	{
		if (file == null) throw new ArgumentNullException("file");

		if (!file.isFile())
		{
			throw new IllegalArgumentException(String.format(
				"%s is not a plain file",
				file.getAbsolutePath()));
		}

		String result;

		BufferedReader br = null;
		try
		{
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null)
			{
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			result = sb.toString();
		}
		finally
		{
			if (br != null)
			{
				br.close();
			}
		}

		return result;
	}

	/**
	 * Looks up the ResourcePlugin for the supplied ResourceType.
	 *
	 * @param resourcePlugins the set of available ResourcePlugins.
	 * @param resourceType    the ResourceType for which a plugin should be retrieved.
	 * @return the ResourcePlugin that corresponds to the supplied ResourceType.
	 * @since 4.0
	 */
	private static ResourcePlugin getResourcePlugin(
		Map<ResourceType, ResourcePlugin> resourcePlugins,
		ResourceType resourceType)
	{
		if (resourcePlugins == null) throw new ArgumentNullException("resourcePlugins");
		if (resourceType == null) throw new ArgumentNullException("resourceType");

		ResourcePlugin resourcePlugin = resourcePlugins.get(resourceType);

		if (resourcePlugin == null)
		{
			throw new RuntimeException(String.format(
				"resource plugin for resource type %s not found",
				resourceType.getUri()));
		}

		return resourcePlugin;
	}

	private static void throwIfFailed(
		UUID stateId,
		List<AssertionResult> assertionResults) throws AssertionFailedException
	{
		if (stateId == null) throw new ArgumentNullException("stateId");
		if (assertionResults == null) throw new ArgumentNullException("assertionResults");

		// If any assertions failed, throw
		for (AssertionResult assertionResult : assertionResults)
		{
			if (!assertionResult.getResult())
			{
				throw new AssertionFailedException(stateId, assertionResults);
			}
		}
	}

	private static void findPaths(
		Resource resource,
		List<List<Migration>> paths,
		List<Migration> thisPath,
		UUID fromState,
		UUID targetState)
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (paths == null) throw new ArgumentNullException("paths");
		if (thisPath == null) throw new ArgumentNullException("thisPath");

		// Have we reached the target state?
		if ((fromState == null && targetState == null) ||
			(fromState != null && fromState.equals(targetState)))
		{
			paths.add(thisPath);
		}

		// If we have not reached the target state, keep traversing the graph
		else
		{
			resource.getMigrations()
				.stream()
				.filter(m ->
					(!m.getFromState().isPresent() && fromState == null) ||
						(m.getFromState().isPresent() && m.getFromState().get().equals(fromState.toString())))
				.forEach(
					migration ->
					{
						State toState = Wildebeest.findState(
							resource,
							migration.getToState().get());
						List<Migration> thisPathCopy = new ArrayList<>(thisPath);
						thisPathCopy.add(migration);
						findPaths(resource, paths, thisPathCopy, toState.getStateId(), targetState);
					});
		}
	}

	private static void validateXml(
		String xml,
		String xsdResourceName) throws
		XmlValidationException
	{
		if (xml == null) throw new ArgumentNullException("xml");
		if (xsdResourceName == null) throw new ArgumentNullException("xsdResourceName");

		try
		{
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
			File xsdLocation = new File(WildebeestApiImpl.class.getResource(xsdResourceName).toURI());
			Schema schema = factory.newSchema(xsdLocation);

			Validator validator = schema.newValidator();
			Source source = new StreamSource(new StringReader(xml));
			validator.validate(source);
		}
		catch (URISyntaxException | IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (SAXException e)
		{
			// Validation failed
			throw new XmlValidationException(e.getMessage());
		}
	}

	/**
	 * Retrives all migrations from plugin and throws an error if migrations refer to state that does not exist
	 *
	 * @param resource Resource that is used to perform migration .
	 * @since 4.0
	 */
	private static void validateMigrationStates(
		Resource resource) throws MigrationInvalidStateException
	{
		if (resource == null) throw new ArgumentNullException("resource");

		List<Migration> migrations = resource.getMigrations();
		List<State> states = resource.getStates();

		for (Migration m : migrations
			)
		{
			boolean migrationToStateValid = false;
			boolean migrationFromStateValid = false;

			//check do states exist in migration, if they don't set them to true so they don't throw errors
			if (!m.getFromState().isPresent())
			{
				migrationFromStateValid = true;
			}
			if (!m.getToState().isPresent())
			{
				migrationToStateValid = true;
			}

			for (State s : states
				)
			{
				if (m.getToState().isPresent() && (m.getToState().get().equals(s.getStateId().toString()) || m.getToState().get().equals(s.getLabel())))
				{
					migrationToStateValid = true;
				}
				if (m.getFromState().isPresent() && (m.getFromState().get().equals(s.getStateId().toString()) || m.getFromState().get().equals(s.getLabel())))
				{
					migrationFromStateValid = true;
				}

				if (migrationFromStateValid == true && migrationToStateValid == true)
				{
					break;
				}
			}

			if (migrationFromStateValid == false || migrationToStateValid == false)
			{
				throw new MigrationInvalidStateException(
					m.getMigrationId(),
					"Migration " + m.getMigrationId().toString() + " has invalid state, " +
						"please fix this before restarting migration");
			}
		}
	}

}
