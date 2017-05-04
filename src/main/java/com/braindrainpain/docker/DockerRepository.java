/*
	The MIT License (MIT)

	Copyright (c) 2014 Jan De Cooman

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/
package com.braindrainpain.docker;

import com.braindrainpain.docker.httpsupport.HttpClientService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import java.text.MessageFormat;
import java.util.Iterator;

/**
* Docker Repository connector.
*
* @author Jan De Cooman
*/
public class DockerRepository
{
	private static final Logger LOG = Logger.getLoggerFor(DockerRepository.class);

	private final RepositoryConfiguration repositoryConfiguration;
	private final HttpClientService httpClientService = new HttpClientService();

	private DockerRepository(final RepositoryConfiguration repositoryConfiguration)
	{
		this.repositoryConfiguration = repositoryConfiguration;

		Property username = repositoryConfiguration.get(Constants.USERNAME);
		Property password = repositoryConfiguration.get(Constants.PASSWORD);

		if ((username != null) && (password != null))
		{
			this.httpClientService.authenticateConnection(username.getValue(), password.getValue());
		}
	}

	public static DockerRepository getInstance(final RepositoryConfiguration repositoryConfiguration)
	{
		return new DockerRepository(repositoryConfiguration);
	}

	public DockerTag getLatestRevision(final PackageConfiguration packageConfiguration)
	{
		String tagName = packageConfiguration.get(Constants.TAG).getValue();
		JsonArray allTagsJson = getAllTags(packageConfiguration);
		String revision = getRevisionFromHistory(tagName, packageConfiguration);

		LOG.info("found revision in manifests: "+revision);

		return this.getLatestTag(allTagsJson, tagName, revision);
	}

	private DockerTag getLatestTag(final JsonArray tags, final String tagName, final String revision)
	{
		DockerTag result = null;

		for (Iterator<JsonElement> iterator = tags.iterator(); iterator.hasNext(); )
		{
			String value = iterator.next().getAsString();

			LOG.info("looking for tag named: "+tagName);
			LOG.info("value: " + value);

			if (tagName.equals(value))
			{
				result = new DockerTag(value, revision);

				LOG.info("Found tag: " + result);
			}
		}

		return result;
	}

	/**
	* Call the Docker API.
	*
	* @param packageConfiguration
	* @return
	*/
	private JsonArray getAllTags(final PackageConfiguration packageConfiguration)
	{
		JsonArray result;

		LOG.info("getAllTags is called");

		String repository = MessageFormat.format(DockerAPI.V2.getUrl(), repositoryConfiguration.get(Constants.REGISTRY).getValue(), packageConfiguration.get(Constants.REPOSITORY).getValue());

		LOG.info("repository: "+repository);

		result = httpClientService.getJsonElementsFromUrl(repository, "tags");

		return result;
	}

	private JsonArray getHistory(final String tagName, final PackageConfiguration packageConfiguration)
	{
		String manifestsUrl = MessageFormat.format(DockerAPI.MANIFEST.getUrl(), repositoryConfiguration.get(Constants.REGISTRY).getValue(), packageConfiguration.get(Constants.REPOSITORY).getValue(), tagName);

		return httpClientService.getJsonElementsFromUrl(manifestsUrl, "history");
	}

	private String getRevisionFromHistory(final String tagName, final PackageConfiguration packageConfiguration)
	{
		JsonArray historyJson = getHistory(tagName, packageConfiguration);
		String firstHistoryEntry = historyJson.getAsJsonArray().get(0).getAsJsonObject().get("v1Compatibility").getAsString();

		return new JsonParser().parse(firstHistoryEntry).getAsJsonObject().get("id").getAsString();
	}
}