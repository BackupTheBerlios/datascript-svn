package net.sf.dstools.maven.rds;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import datascript.tools.DataScriptTool;

/**
 * Goal for generating Java sources from a DataScript model
 * 
 */
public abstract class AbstractRdsMojo extends AbstractMojo {
	// ----------------------------------------------------------------------
	// Mojo parameters
	// ----------------------------------------------------------------------

	/**
	 * The enclosing project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	// ----------------------------------------------------------------------
	// rds parameters
	// ----------------------------------------------------------------------

	/**
	 * Path to source file of the top package, relative to sourceDirectory.
	 * 
	 * @parameter expression="${root}"
	 * @required
	 */
	protected String modelRoot;

	/**
	 * Activates debugging features in the generated code.
	 * 
	 * @parameter expression="${debug}" default-value="false"
	 */
	private boolean debug;

	public void execute() throws MojoExecutionException 
	{
		File f = getOutputDirectory();

		if (!f.exists()) {
			f.mkdirs();
		}
		List<String> arguments = new ArrayList<String>();
		arguments.add("-out");
		arguments.add(getOutputDirectory().getPath());
		arguments.add("-src");
		arguments.add(getSourceDirectory().getPath());
		if (debug)
		{
			arguments.add("-debug");
		}
		arguments.add(modelRoot);
		DataScriptTool.main(arguments.toArray(new String[0]));
		
		registerSourceRoot();
	}
	
	protected abstract void registerSourceRoot();
	
	protected abstract File getSourceDirectory();

	protected abstract File getOutputDirectory();
}
