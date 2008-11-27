/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.berlios.datascript.maven.plugin.sourcebundle;

import java.io.File;
import java.util.List;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Creates a source bundle for Eclipse 3.4 from the sources of the current
 * project. 
 * <p>
 * Eclipse will only recognize the bundle as a source bundle if it is
 * renamed to bundleSymbolicName_major.minor.micro[.qualifier].jar, so your
 * Maven assembly should take care of this.
 * <p>
 * Implementation based on org.apache.maven.plugin.jar.AbstractJarMojo.
 *
 * @goal sourcebundle
 * 
 * @phase package
 */
public class SourceBundleMojo
    extends AbstractMojo
{
    private static final String[] DEFAULT_EXCLUDES = 
    	new String[] { "**/package.html", "**/CVS/**", "**/.svn/**" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * List of files to include. Specified as fileset patterns.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Directory containing the generated JAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated JAR.
     *
     * @parameter alias="jarName" expression="${jar.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver jarArchiver;

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The archive configuration to use.
     * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Path to the default MANIFEST file to use. It will be used if
     * <code>useDefaultManifestFile</code> is set to <code>true</code>.
     *
     * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
     * @required
     * @readonly
     */
    private File defaultManifestFile;

    /**
     * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
     *
     * @parameter expression="${jar.useDefaultManifestFile}" default-value="false"
     *
     * @since 2.2
     */
    private boolean useDefaultManifestFile;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether creating the archive should be forced.
     *
     * @parameter expression="${jar.forceCreation}" default-value="false"
     */
    private boolean forceCreation;

    /**
     * The root directories for sources to be included.
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     */
    private List<String> compileSourceRoots;


    /**
     * The symbolic name of the binary OSGi bundle related to this source
     * bundle.
     *  
     * @parameter expression="${bundleName}" default-value="${project.artifactId}"
     * @required
     */
    private String bundleName;
    
    /**
     * The bundle symbolic name of the source bundle itself.
     * 
     * @parameter expression="${sourceBundleName}" default-value="${project.artifactId}.source"
     * @required
     */
    private String sourceBundleName;
    
    protected final MavenProject getProject()
    {
        return project;
    }

    /**
     * Overload this to produce a jar with another classifier, for example a test-jar.
     */
    protected String getClassifier()
    {
    	return "sources";
    }

    /**
     * Overload this to produce a test-jar, for example.
     */
    protected String getType()
    {
    	return "java-source";
    }

    protected static File getJarFile( File basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".jar" );
    }

    /**
     * Default Manifest location. Can point to a non existing file.
     * Cannot return null.
     */
    protected File getDefaultManifestFile()
    {
        return defaultManifestFile;
    }


    /**
     * Generates the JAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public File createArchive()
        throws MojoExecutionException
    {
        File jarFile = getJarFile( outputDirectory, finalName, getClassifier() );

        MavenArchiver archiver = new MavenArchiver();

        archiver.setArchiver( jarArchiver );

        archiver.setOutputFile( jarFile );

        archive.setForced( forceCreation );

        try
        {
        	// Add the source directories to the archiver
            for (String sourceRootName : compileSourceRoots)
            {
                File sourceDirectory = new File(sourceRootName);
                archiver.getArchiver().addDirectory( sourceDirectory, getIncludes(), getExcludes() );                
            }
            String version = getVersion();
            String sourceBundleHeader = String.format("%s;version=\"%s\"", bundleName, version);
            
            // Add the OSGi and Eclipse manifest headers
            archive.addManifestEntry("Bundle-ManifestVersion", "2");
            archive.addManifestEntry("Bundle-SymbolicName", sourceBundleName);
            archive.addManifestEntry("Bundle-Version", version);
            archive.addManifestEntry("Eclipse-SourceBundle", sourceBundleHeader);
            File existingManifest = getDefaultManifestFile();

            if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null )
            {
                getLog().info( "Adding existing MANIFEST to archive. Found under: " + existingManifest.getPath() );
                archive.setManifestFile( existingManifest );
            }

            archiver.createArchive( project, archive );

            return jarFile;
        }
        catch ( Exception e )
        {
            // TODO: improve error handling
            throw new MojoExecutionException( "Error assembling JAR", e );
        }
    }

    /**
     * Generates the JAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {
        File jarFile = createArchive();

        String classifier = getClassifier();
        if ( classifier != null )
        {
            projectHelper.attachArtifact( getProject(), getType(), classifier, jarFile );
        }
        else
        {
            getProject().getArtifact().setFile( jarFile );
        }
    }

    private String[] getIncludes()
    {
        if ( includes != null && includes.length > 0 )
        {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes()
    {
        if ( excludes != null && excludes.length > 0 )
        {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
    
    /**
     * Converts the Maven artifact version to a version in OSGi format
     * major.minor.micro.qualifier, such that major, minor and micro are
     * non-negative decimals. 
     * 
     * TODO: This implementation is not fool-proof and should probably be
     * shared with similar code in the maven-bundle-plugin, or the bnd tool,
     * or Eclipse PDE.
     * 
     * @return OSGi version string
     */
    private String getVersion()
    {
    	String[] parts = project.getVersion().split("\\.");
    	String major = "0";
    	String minor = "0";
    	String micro = "0";
    	String qualifier = null;
    	if (isNumber(parts[0]))
    	{
    		major = parts[0];
    	}
    	else
    	{
    		throw new IllegalArgumentException(project.getVersion());
    	}
    	if (parts.length > 1)
    	{
    		if (isNumber(parts[1]))
    		{
    			minor = parts[1];
    		}
    		else
    		{
    			qualifier = parts[1];
    		}
    	}
    	if (parts.length > 2)
    	{
    		if (isNumber(parts[2]))
    		{
    			minor = parts[2];
    		}
    		else
    		{
    			qualifier = parts[2];
    		}
    	}
    	if (parts.length > 3)
    	{
    		qualifier = parts[3];
    	}
    	
    	String version = String.format("%s.%s.%s", major, minor, micro);
    	if (qualifier != null && qualifier.length() > 0)
    	{
    		version += "." + qualifier;
    	}
    	return version;
    }
    
    /**
     * Checks if the String is composed of digits only.
     * @param s
     * @return
     */
    private boolean isNumber(String s)
    {
    	for (int i = 0; i < s.length(); i++)
    	{
    		if (! Character.isDigit(s.charAt(i)))
    			return false;	
    	}
    	return true;
    }
}
