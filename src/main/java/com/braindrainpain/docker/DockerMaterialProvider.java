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

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.annotation.Load;
import com.thoughtworks.go.plugin.api.annotation.UnLoad;
import com.thoughtworks.go.plugin.api.info.PluginContext;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProvider;

/**
* GoCD extension point.
*
* @author Jan De Cooman
*/
@Extension
public class DockerMaterialProvider implements PackageMaterialProvider
{
	final private static Logger LOG = Logger.getLoggerFor(DockerMaterialProvider.class);

	@Load
	public void onLoad(PluginContext context)
	{
		LOG.info("Docker plugin loaded");
	}

	@UnLoad
	public void onUnLoad(final PluginContext context)
	{
		LOG.info("Removed Docker plugin" );
	}

	@Override
	public PackageMaterialConfiguration getConfig()
	{
		return new DockerMaterialConfiguration();
	}

	@Override
	public PackageMaterialPoller getPoller()
	{
		return new DockerMaterialPoller();
	}
}