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

/**
* Hold the docker metadata.
*
* @author Jan De Cooman
*/
public class DockerTag
{
	final private String tag;
	final private String revision;

	public DockerTag(final String tag, final String revision)
	{
		this.tag = tag;
		this.revision = revision;
	}

	public String getTag()
	{
		return this.tag;
	}

	public String getRevision()
	{
		return this.revision;
	}

	@Override
	public String toString()
	{
		return this.getTag() + "@" + this.getRevision();
	}
}