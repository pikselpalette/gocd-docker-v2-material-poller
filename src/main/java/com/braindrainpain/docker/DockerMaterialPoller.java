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

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import java.util.Date;

/**
* Docker Material Poller
*
* @author Jan De Cooman
*/
public class DockerMaterialPoller implements PackageMaterialPoller
{
	final private static Logger LOG = Logger.getLoggerFor(DockerMaterialPoller.class);

	@Override
	public PackageRevision getLatestRevision(final PackageConfiguration packageConfiguration, final RepositoryConfiguration repositoryConfiguration)
	{
		LOG.info("get last revision is called");
		// the fields must be valid
		this.validateData(repositoryConfiguration, packageConfiguration);

		// fetch the latest tag
		DockerTag tag = DockerRepository.getInstance(repositoryConfiguration).getLatestRevision(packageConfiguration);
		LOG.info("tag.getRevision(): "+tag.getRevision());
		LOG.info("DockerTag.toString():"+tag.toString());

		return new PackageRevision(tag.getRevision(), new Date(), "docker");
	}

	@Override
	public PackageRevision latestModificationSince(final PackageConfiguration packageConfiguration, final RepositoryConfiguration repositoryConfiguration, final PackageRevision packageRevision)
	{
		LOG.info("last modification since is called");

		PackageRevision latestRevision = this.getLatestRevision(packageConfiguration, repositoryConfiguration);
		if (!latestRevision.getRevision().contentEquals(packageRevision.getRevision()))
		{
			return latestRevision;
		}

		return null;
	}

	/**
	* Check the validation of the fields and then check the real connection
	*
	* @param repositoryConfiguration
	* @return
	*/
	@Override
	public Result checkConnectionToRepository(final RepositoryConfiguration repositoryConfiguration)
	{
		LOG.info("check connection repository is called");

		Result result = this.validateRepository(repositoryConfiguration);
		if (!result.isSuccessful())
		{
			return result;
		}

		DockerRegistry instance = DockerRegistry.getInstance(repositoryConfiguration);
		try
		{
			instance.checkConnection();
			result = new Result().withSuccessMessages(String.format("Successfully connected to %s", instance.getUrl()));
		}
		catch (Exception e)
		{
			LOG.warn(String.format("Check connection for %s failed with exception - %s", instance.getUrl(), e));

			result = new Result().withErrorMessages(String.format("Check connection failed for %s", instance.getUrl()));
		}

		return result;
	}

	/**
	* Check if the package is valid.
	*
	* @param packageConfiguration
	* @param repositoryConfiguration
	* @return
	*/
	@Override
	public Result checkConnectionToPackage(final PackageConfiguration packageConfiguration, final RepositoryConfiguration repositoryConfiguration)
	{
		LOG.info("check connection to package is called");

		Result checkConnectionResult = this.checkConnectionToRepository(repositoryConfiguration);
		if (!checkConnectionResult.isSuccessful())
		{
			return checkConnectionResult;
		}

		try
		{
			LOG.info("start package validation");

			Result packageConfigurationValidationResult = packageValidation(packageConfiguration, repositoryConfiguration);
			if (!packageConfigurationValidationResult.isSuccessful())
			{
				LOG.info("package validation not successful.");

				return packageConfigurationValidationResult;
			}

			PackageRevision latestRevision = this.getLatestRevision(packageConfiguration, repositoryConfiguration);

			return new Result().withSuccessMessages(String.format("Found package '%s'.", latestRevision.getRevision()));
		}
		catch (Exception e)
		{
			String message = String.format("Could not find any package that matched '%s'.", packageConfiguration.get(Constants.REPOSITORY).getValue());

			LOG.info(message + e);

			return new Result().withErrorMessages(message);
		}
	}

	/**
	* Validate the repository configuration
	*
	* @param repositoryConfiguration
	* @return
	*/
	private Result validateRepository(final RepositoryConfiguration repositoryConfiguration)
	{
		LOG.info("validate repository is called");

		ValidationResult validationResult = new DockerMaterialConfiguration().isRepositoryConfigurationValid(repositoryConfiguration);

		return createValidationResult(validationResult);
	}

	/**
	* Validate the package configuration
	*
	* @param packageConfigurations
	* @param repositoryPackageConfiguration
	* @return
	*/
	private Result packageValidation(final PackageConfiguration packageConfigurations, final RepositoryConfiguration repositoryPackageConfiguration)
	{
		LOG.info("package validation is called");

		ValidationResult validationResult = new DockerMaterialConfiguration().isPackageConfigurationValid(packageConfigurations, repositoryPackageConfiguration);

		return createValidationResult(validationResult);
	}

	private Result createValidationResult(ValidationResult validationResult)
	{
		final Result result;

		if (!validationResult.isSuccessful())
		{
			result = new Result().withErrorMessages(validationResult.getMessages());
		}
		else
		{
			result = new Result();
		}

		return result;
	}

	/**
	* Validate the data of both configuration.
	*
	* @param repositoryConfigurations
	* @param packageConfigurations
	*/
	private void validateData(final RepositoryConfiguration repositoryConfigurations, final PackageConfiguration packageConfigurations)
	{
		LOG.info("validate data is called");

		ValidationResult validationResult = new ValidationResult();

		new DockerMaterialConfiguration().validate(packageConfigurations, repositoryConfigurations, validationResult);

		if (!validationResult.isSuccessful())
		{
			StringBuilder stringBuilder = new StringBuilder();
			for (ValidationError validationError : validationResult.getErrors())
			{
				stringBuilder.append(validationError.getMessage()).append("; ");
			}

			String errorString = stringBuilder.toString();
			String message = errorString.substring(0, errorString.length() - 2);

			LOG.warn(String.format("Data validation failed: %s", message));

			throw new RuntimeException(message);
		}
	}
}