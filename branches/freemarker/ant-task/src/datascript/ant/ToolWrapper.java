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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

/**
 * The ToolWrapper decouples the execution of a main class in terms
 * of class dependencies from the ant task itself. 
 * 
 * Hence it is possible to compile the main class in the same ant file
 * in which the task is executed.
 * 
 * @author lgirndt
 *
 */
public class ToolWrapper {

    private String className;
    private Iterable<Path> classPath;
    
    public ToolWrapper(String className,Iterable<Path> classPath)
    {
        this.className = className;
        this.classPath = classPath;
    }
    
    /**
     * Puts all classpath items into a list of URLs
     * 
     * @return
     */
    private URL [] getUrls() throws BuildException
    {
        ArrayList<URL> urls = new ArrayList<URL>();
        
        for(Path p : classPath)
        {
            String [] files = p.list();
            for(String f : files)
            {
                String u = null;
                try
                {
                    
                    if(f.endsWith(".jar"))
                    {
                        u = "jar:file:"+f+"!/";
                    }
                    else
                    {
                        u = "file:"+f+"/";                        
                    }
                    
                    // System.out.println("add to classpath "+ u);
                    urls.add(new URL(u));
                    
                }catch(MalformedURLException e)
                {
                    throw new BuildException("Malformed URL: " + u);
                }
            }            
        }
        
        URL [] res = new URL[urls.size()];
        urls.toArray(res);
        
        return res;
    }
    
    public void callMain(String [] args) throws BuildException
    {
        try
        {
            URL [] urls = getUrls();
            
            URLClassLoader classLoader = 
                new URLClassLoader(urls,this.getClass().getClassLoader());
            
            Class clazz = Class.forName(className,true,classLoader);
            
            // and now this is java magic
            Class [] pTypes = new Class[1];
            pTypes[0] = String[].class;
            Method main = clazz.getDeclaredMethod("main", pTypes );
            
            Object [] allArgs = new Object[1];
            allArgs[0] = args;
            main.invoke(null, allArgs);
            
        }catch(ClassNotFoundException e)
        {
            throw new BuildException("Loading class "+ className + " failed.",e);
        }
        catch(NoSuchMethodException e)
        {
            throw new BuildException(
                    "Class " + className+ " has no main method.",e);
        } catch (IllegalArgumentException e) {
            throw new BuildException(
                    "Failed to exec main on "+ className +": " + e.getMessage(),e);
        } catch (IllegalAccessException e) {
            throw new BuildException(
                    "Failed to exec main on "+ className+": " + e.getMessage(),e);
        } catch (InvocationTargetException e) {
            throw new BuildException(
                    "Failed to exec main on "+ className+": " + e.getMessage(),e);
        }
    }
}
