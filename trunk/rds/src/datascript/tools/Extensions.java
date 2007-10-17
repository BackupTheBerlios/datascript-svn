/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
 * All rights reserved.
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
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
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


package datascript.tools;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



/**
 * @author hwedekind
 * 
 */
public class Extensions implements Iterable<Extension>
{
    private final ArrayList<Extension> extensions = new ArrayList<Extension>();


    /**
     * Constructor for this class
     * 
     * @param extDir
     *            directory where the extensions exsists
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    Extensions(File extDir) throws IOException, InstantiationException,
            IllegalAccessException
    {
        Collection<File> extensionFiles = findExtensionsRecursively(extDir);
        if (extensionFiles == null || extensionFiles.size() <= 0)
            return;

        Collection<URL> urls = toURLs((File[]) extensionFiles
                .toArray(new File[] {}));
        ClassLoader classLoader = new URLClassLoader(
                urls.toArray(new URL[] {}), this.getClass().getClassLoader());

        for (File f : extensionFiles)
        {
            java.util.jar.JarFile rf = new java.util.jar.JarFile(f);
            java.util.jar.Manifest m = rf.getManifest();
            if (m != null)
            {
                String mc = m.getMainAttributes().getValue("Main-Class");
                try
                {
                    Class<?> clazz = Class.forName(mc, true, classLoader);
                    Extension extension = (Extension) clazz.newInstance();
                    extensions.add(extension);
                }
                catch (ClassNotFoundException e)
                {
                    System.err.println("Extension " + mc + " not found.");
                }
            }
        }
    }


    /**
     * This adds an Extension class
     * 
     * @param e
     *            Extension class to add
     */
    public void addExtension(Extension e)
    {
        extensions.add(e);
    }


    /**
     * Returns the number of elements in this list.
     * 
     * @return  the number of elements in this list.
     */
    public int size()
    {
        return extensions.size();
    }


    /************ section for interface implementations *********** */

    
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Extension> iterator()
    {
        return extensions.iterator();
    }


    /************ section for private memberfunctions *********** */


    /**
     * this method converts a list of filenames into a collection of URL
     * 
     * @param files
     *            list of files to convert
     * @return list of URLs
     */
    private Collection<URL> toURLs(File[] files)
    {
        Collection<URL> urls = new ArrayList<URL>(files.length);
        for (File file : files)
        {
            try
            {
                urls.add(new URL("jar:file:" + file.getPath() + "!/"));
            }
            catch (MalformedURLException e)
            {
                System.err.println("Could not load " + file.getPath());
                System.err.println(e.toString());
            }
        }
        return urls;
    }


    /**
     * This memberfunction scans a directory for extension files. A name of an
     * extension file starts with "rds" and ends with "Extension.jar" and is a
     * Java class that can be loaded by a class loader
     * 
     * @param directory
     *            File class to the directory
     * @return list of extension files in directory
     */
    private Collection<File> findExtensionsRecursively(File directory)
    {
        if (directory == null || !directory.isDirectory())
            return null;
        Collection<File> retVal = new ArrayList<File>();

        File[] extensionFiles = directory.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.isDirectory()
                        || (file.getName().endsWith("Extension.jar") && file
                                .getName().startsWith("rds"));
            }
        });

        for (File file : extensionFiles)
        {
            if (file.isDirectory())
            {
                Collection<File> eFiles = findExtensionsRecursively(file);
                if (eFiles != null && eFiles.size() > 0) retVal.addAll(eFiles);
            }
            if (file.isFile()) retVal.add(file);
        }
        return retVal;
    }
}
