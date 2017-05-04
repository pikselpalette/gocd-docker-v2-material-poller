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
package com.braindrainpain.docker;

import com.braindrainpain.docker.httpsupport.WebMockTest;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import java.util.Date;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

/**
* @author Manuel Kasiske
*/
@RunWith(MockitoJUnitRunner.class)
public class DockerMaterialPollerTest extends WebMockTest
{
	private DockerMaterialPoller dockerMaterialPoller = new DockerMaterialPoller();

	@Test
	public void testRepositoryConfigurationShouldCheckedSuccessfully()
	{
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();

		assertTrue(dockerMaterialPoller.checkConnectionToRepository(repositoryConfiguration).isSuccessful());
	}

	@Test
	public void testPackageConfigurationShouldCheckedSuccessfully()
	{
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
		PackageConfiguration packageConfiguration = getPackageConfiguration();

		assertTrue(dockerMaterialPoller.checkConnectionToPackage(packageConfiguration, repositoryConfiguration).isSuccessful());
	}

	@Test
	public void testGetLatestRevisionShouldBeReturnLatestHashCode()
	{
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
		PackageConfiguration packageConfiguration = getPackageConfiguration();
		assertEquals("21047564fb9c509d92641d4202d38642eb353ac1c021925e34850ac213dcda7d", dockerMaterialPoller.getLatestRevision(packageConfiguration, repositoryConfiguration).getRevision());
	}

	@Test
	public void testGetLatestRevisionWithWrongRepositoryShouldFailWithNullPointerException()
	{
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
		PackageConfiguration packageConfiguration = getPackageConfiguration();
		packageConfiguration.get(Constants.REPOSITORY).withDefault("pharmacy-ser");

		try
		{
			dockerMaterialPoller.getLatestRevision(packageConfiguration, repositoryConfiguration).getRevision();
			fail();
		}
		catch (NullPointerException e) {}
	}

	@Test
	public void testLatestModificationSince()
	{
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
		PackageConfiguration packageConfiguration = getPackageConfiguration();
		PackageRevision packageRevision = new PackageRevision("x", new Date(), "docker");
		PackageRevision latest = dockerMaterialPoller.latestModificationSince(packageConfiguration, repositoryConfiguration, packageRevision);

		assertNotNull(latest);
	}

	private PackageConfiguration getPackageConfiguration()
	{
		DockerMaterialConfiguration dockerMaterialConfiguration = new DockerMaterialConfiguration();
		PackageConfiguration packageConfiguration = dockerMaterialConfiguration.getPackageConfiguration();
		packageConfiguration.get(Constants.REPOSITORY).withDefault("pharmacy-service");
		packageConfiguration.get(Constants.TAG).withDefault("latest");

		return packageConfiguration;
	}

	private RepositoryConfiguration getRepositoryConfiguration()
	{
		DockerMaterialConfiguration dockerMaterialConfiguration = new DockerMaterialConfiguration();
		RepositoryConfiguration repositoryConfiguration = dockerMaterialConfiguration.getRepositoryConfiguration();
		repositoryConfiguration.get(Constants.REGISTRY).withDefault("http://localhost:5000");
		repositoryConfiguration.get(Constants.USERNAME).withDefault("foo");
		repositoryConfiguration.get(Constants.PASSWORD).withDefault("foo");

		return repositoryConfiguration;
	}
}