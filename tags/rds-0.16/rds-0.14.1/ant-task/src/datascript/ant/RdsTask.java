/* BSD License
 *
 * Copyright (c) 2007, Lars Girndt, Thomas Heike,
 * Advanced Driver Information Technology GmbH
 * All rights reserved.  
 *
 * This software is derived from previous work
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Advanced Driver Information Technology GmbH
 *       nor Harman/Becker Automotive Systems nor Godmar Back nor the names 
 *       of their contributors may be used to endorse or promote products 
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package datascript.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * 
 * An ant task that handles our sophistated code generation step in ant.
 * 
 * There are several requirements, whether or not the code should be generated
 * 
 * - If one of the input datascript files has changed
 * - During our development cycle also the generator implementation itself
 *   changes, hence new files should be generated
 * 
 * Furthermore there is need to delete already existing files, since the an
 * according datascript type is no longer present.
 * 
 * When using the task you have to define a list of dependencies. These are 
 * all files, which should force a generation of source classes, if they have
 * changed.
 * 
 * On the other hand output covers all files which are generated and which
 * will be deleted and will be compared to the dependencies timestamp.
 * 
 * With the contained classpath element the task solves a severe issue:
 * 
 * RDS is loaded during execution time of this target rather
 * than ant's bootstrapping. This has a simple reason: Ant
 * loads all its classes during bootstrapping. If we would just
 * use ant's classpath, we could not load rds since it is
 * created by the task just before test-classes are called.
 * We avoid this situation by allowing the task to have
 * its own classpath.
 * 
 * @author lgirndt
 *
 */
public class RdsTask extends Task {

	private String srcfile;
	private File path;
	private File out;
	private String pkg;
	private boolean clean = false;
	
    private File ext;
    
    private Vector<Argument> arguments = new Vector<Argument>();

	private Vector<Path> dependencies = new Vector<Path>();
	private Vector<Path> output = new Vector<Path>();
    private Vector<Path> classpath = new Vector<Path>();
	
	public String getSrcfile() {
		return srcfile;
	}

	public void setSrcfile(String srcfile) {
		this.srcfile = srcfile;
	}
	
	public File getOut() {
		return out;
	}

	public void setOut(File out) {
		this.out = out;
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}
    
    /**
     * An alias for Path
     * @param src
     */
    public void setSrc(File src)
    {
        setPath(src);
    }
    
    /**
     * An alias for Path
     * @return
     */
    public File getSrc()
    {
        return getPath();
    }

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}	
	
	public File getExt() {
        return ext;
    }

    /**
     * The extension directory, this parameter is optional
     * @param ext
     */
    public void setExt(File ext) {
        this.ext = ext;
    }

    public void addDependencies(Path dep)
	{
		this.dependencies.add(dep);
	}
			
	public void addOutput(Path output)
	{		
		this.output.add(output);
	}		
	
    public void addClasspath(Path cp)
    {
        this.classpath.add(cp);
    }
    
	public boolean isClean() {
		return clean;
	}

	/**
	 * Clean is optional. If it is set, all files matching output
	 * are deleted. Hence you have to use it carefully.
	 * 
	 * If not set, the default value is false
	 * 
	 * @param clean is the output deleted before building?
	 */
	public void setClean(boolean clean) {
		this.clean = clean;
	}

	public Argument createArg()
    {
	    Argument arg = new Argument();
        arguments.add(arg);
        return arg;
    }
	
	/**
	 * validates the input
	 * @throws BuildException
	 */
	private void validate() throws BuildException
	{
		if( getPath() == null )
		{
			throw new BuildException("Path not set.");
		}
		
		if( getPkg() == null)
		{
			throw new BuildException("Package not set");
		}
		
		if( getOut() == null)
		{
			throw new BuildException("Out not set");
		}
		
		if( getSrcfile() == null)
		{
			throw new BuildException("Srcfile not set");
		}               
		
		/* currently type is optional
		if( getType() == null)
		{
			throw new BuildException("Type not set.");
		}
		*/
		
		if( ! getPath().exists() )
		{
			throw new BuildException(getPath().toString()+" does not exist.");
		}
        
        if( ! getExt().exists() )
        {
            throw new BuildException(getExt().toString()+" does not exist.");
        }
						
	}
	
	/**
	 * This method is called from ant to execute the task
	 */
	public void execute() throws BuildException {
		
		validate();
	                            
		if( shouldCompile())
		{
			System.out.println("There are changes in depending files.");
			
			if(clean)
			{
				cleanPreviousOutput();
			}
			 		
            ToolWrapper tool = 
                new ToolWrapper("datascript.tools.DataScriptTool", classpath);
			
            tool.callMain(buildArgs());
            
            // DataScriptTool.main(buildArgs());
		}
		else
		{
			System.out.println("the generate sources are up to date.");
		}
	}
    
    /**
     * Creates the arguments required by DataScriptTool
     * @return
     */
    private String [] buildArgs()
    {
        ArrayList<String> argsList = new ArrayList<String>();
        
        argsList.add("-src");
        argsList.add(getPath().toString());
        argsList.add("-pkg");
        argsList.add(getPkg());
        argsList.add("-out");
        argsList.add(getOut().toString());
        if( getExt() != null )
        {
            argsList.add("-ext");
            argsList.add(getExt().toString());
        }
        
        for( Argument a : arguments)
        {            
            argsList.add("-" + a.getName());
            if( a.hasValue())
            {
                argsList.add(a.getValue());
            }
        }
        
        argsList.add(getSrcfile());
        
        
        String [] args = new String[argsList.size()];
        argsList.toArray(args);
        
        return args;
    }
	
	/**
	 * Tests whether the source code generation should be exexuted.
	 */
	private boolean shouldCompile()
	{		
		verbose( "check if output is empty");
		if( isOutputEmpty())
		{
			verbose("output is empty.");
			return true;
		}
		
		long latestDep = calcLatestDependency();
		long earliestOutput = calcEarliestOutput();
		
		verbose("latest dependency: " + latestDep
				+ ", earliest output: "	+ earliestOutput);
		
		if( latestDep > earliestOutput )
		{
			return true;
		}
		
		return false;
	}
	
	private boolean isOutputEmpty()
	{	
		verbose("output size : " + output.size());
		for(Path p : output)
		{
			if(p.list().length > 0)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private long calcLatestDependency()
	{
		long lastModified= 0;
		
		for(Path p : dependencies)
		{
			String [] files = p.list();
			for(int i = 0 ; i < files.length; i++)
			{			
				long m = new File(files[i]).lastModified();
				
				verbose("ea: " + files[i] + " " + m);
				
				if( m > lastModified)
				{
					lastModified = m;
				}
			}
		}
		
		return lastModified;
	}
	
	private long calcEarliestOutput()
	{
		long lastModified= Long.MAX_VALUE;
		for( Path p : output)
		{
			String [] files = p.list();
			for(int i = 0; i < files.length; i++)
			{
				long m = new File(files[i]).lastModified();				
				
				if( m < lastModified)
				{
					lastModified = m;
				}
			}
		}
		return lastModified;
	}
	
	private void verbose(String msg)
	{
		getProject().log(msg, Project.MSG_VERBOSE);
	}
	
	private void cleanPreviousOutput()
	{
		int count = 0;
		for(Path p : output)
		{
			String [] files = p.list();
			for(int i = 0; i < files.length; i++)
			{
				verbose("delete: " + files[i]);
				new File(files[i]).delete();
				count++;
			}
		}
		System.out.println("Deleted " + count + " files.");
	}
}
