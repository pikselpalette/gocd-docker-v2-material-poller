/*
	The MIT License (MIT)

	Copyright (c) 2015 Manuel Kasiske

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
package com.braindrainpain.docker.httpsupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.go.plugin.api.logging.Logger;
import java.io.IOException;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

/**
* @author Manuel Kasiske
*/
public class HttpClientService
{
	private final Logger LOG = Logger.getLoggerFor(HttpClientService.class);
	private final HttpClient httpClient;
	private final GetMethod getMethod;

	public HttpClientService()
	{
		httpClient = createHttpClient();
		getMethod = new GetMethod();
	}

	protected HttpClientService(HttpClient httpClient, GetMethod getMethod)
	{
		this.httpClient = httpClient;
		this.getMethod = getMethod;
	}

	public void authenticateConnection(String username, String password)
	{
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		httpClient.getState().setCredentials(AuthScope.ANY, credentials);
	}

	public void checkConnection(String url, String username, String password)
	{
		LOG.debug("Checking: '" + url + "'");

		try
		{
			URI uri = new URI(url, false);
			this.getMethod.setURI(uri);
			getMethod.setFollowRedirects(false);

			if ((username != null) && (password != null))
			{
				authenticateConnection(username, password);
			}

			int status = httpClient.executeMethod(getMethod);
			if (status != HttpStatus.SC_OK)
			{
				LOG.error("Not ok from: '" + url + "'");
				LOG.error("status: "+status);

				throw new RuntimeException("Not ok from: '" + url + "'");
			}
		}
		catch (IOException e)
		{
			LOG.error("Error connecting to: '" + url + "'");

			throw new RuntimeException("Error connecting to: '" + url + "'");
		}
	}

	public String doGet(String url) throws IOException
	{
		URI uri = new URI(url, false);
		this.getMethod.setURI(uri);

		final int status = httpClient.executeMethod(getMethod);
		if (status != HttpStatus.SC_OK)
		{
			LOG.error("cannot connect to url " + url);

			throw new IOException("cannot connect to url " + url);
		}

		return getMethod.getResponseBodyAsString();
	}


	public JsonArray getJsonElementsFromUrl(String url, String memberName)
	{
		JsonArray result = null;
		try
		{
			GetMethod get = new GetMethod(url);
			if (httpClient.executeMethod(get) == HttpStatus.SC_OK)
			{
				String jsonString = get.getResponseBodyAsString();

				LOG.info("RECIEVED: " + jsonString);

				result = parseJson(jsonString, memberName);

				LOG.info("Build result: "+result);
			}
		}
		catch (IOException e)
		{
			// Wrap into a runtime. There is nothing useful to do here
			// when this happens.
			LOG.error("cannot fetch the tags from "+url);

			throw new RuntimeException("Cannot fetch the tags from " + url, e);
		}

		return result;
	}

	private JsonArray parseJson(String jsonString, String memberName)
	{
		return (JsonArray) ((JsonObject) new JsonParser().parse(jsonString)).get(memberName);
	}

	private HttpClient createHttpClient()
	{
		HttpClient client = new HttpClient();

		client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, this.getSystemProperty("docker.repo.connection.timeout", 10 * 1000));
		client.getParams().setSoTimeout(this.getSystemProperty("docker.repo.socket.timeout", 5 * 60 * 1000));

		return client;
	}

	private int getSystemProperty(final String key, final int defaultValue)
	{
		try
		{
			return Integer.parseInt(System.getProperty(key));
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
}