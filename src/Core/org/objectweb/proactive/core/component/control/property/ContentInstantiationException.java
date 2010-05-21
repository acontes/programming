/***
 * OW2 FraSCAti Tinfi
 * Copyright (C) 2009-2010 INRIA, USTL
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Lionel Seinturier
 */

package org.objectweb.proactive.core.component.control.property;

//import org.ow2.frascati.tinfi.TinfiException;

/**
 * Exception thrown whenever a problem occurs when instantiating the content
 * instance of a component.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @since 1.1.1
 */
public class ContentInstantiationException extends Exception {

    private static final long serialVersionUID = 5495540959701653688L;

    public ContentInstantiationException() {
        super();
    }

    public ContentInstantiationException(String msg) {
        super(msg);
    }

    public ContentInstantiationException(Throwable t) {
        super(t);
    }

    public ContentInstantiationException(String s, Throwable t) {
        super(s, t);
    }
}
