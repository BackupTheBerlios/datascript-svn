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


package datascript.ast;


import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import antlr.Token;
import datascript.antlr.util.TokenAST;



@SuppressWarnings("serial")
public class Container extends TokenAST
{
    // / set of compound types that can contain this type
    private final SortedSet<Container> containers = new TreeSet<Container>();


    public Container()
    {
        super();
    }


    public Container(Token tok)
    {
        super(tok);
    }


    public void addContainer(Container f)
    {
        if (!containers.contains(f))
        {
            containers.add(f);
        }
    }


    public Set<Container> getContainers()
    {
        return containers;
    }


    /**
     * @return true if 'this' is contained in compound type 'f'
     */
    public boolean isContainedIn(Container f)
    {
        return isContainedIn(f, new Stack<Container>());
    }


    /**
     * The "is contained" relationship may contain cycles use a stack to avoid
     * them. This is a simple DFS path finding algorithm that finds a path from
     * 'this' to 'f'.
     */
    private boolean isContainedIn(Container f, Stack<Container> seen)
    {
        if (containers.contains(f))
        {
            return true;
        }

        /* check whether any container of 'this' is contained in 'f' */
        for (Container c : containers)
        {
            if (seen.search(c) == -1)
            {
                seen.push(c);
                if (c.isContainedIn(f, seen))
                {
                    return true;
                }
                seen.pop();
            }
        }
        return false;
    }

}
