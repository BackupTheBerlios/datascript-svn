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

/**
 * Goal for generating Java sources from a DataScript model
 * 
 * @goal java
 * 
 * @phase generate-sources
 */
public class RdsJavaMojo extends AbstractRdsMojo 
{
    /**
	 * Specifies the root source directory for DataScript files.
	 * 
	 * @parameter expression="${basedir}/src/main/ds"
	 * @required
	 */
	private File sourceDirectory;

	protected void registerSourceRoot()
	{
		project.addCompileSourceRoot(outputDirectory.toString());
	}
	
	protected File getSourceDirectory()
	{
		return sourceDirectory;
	}
	
}
