/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */

package org.objectweb.proactive.extra.annotation;

import java.lang.annotation.ElementType;

import javax.lang.model.element.ElementKind;

import org.objectweb.proactive.core.ProActiveRuntimeException;


/**
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class UtilsCTree {
    public static ElementType convertToElementType(ElementKind kind) {

        switch (kind) {
            case ANNOTATION_TYPE:
                return ElementType.ANNOTATION_TYPE;
            case CLASS:
                return ElementType.TYPE;
            case CONSTRUCTOR:
                return ElementType.CONSTRUCTOR;
            case FIELD:
                return ElementType.FIELD;
            case INTERFACE:
                return ElementType.TYPE;
            case LOCAL_VARIABLE:
                return ElementType.LOCAL_VARIABLE;
            case METHOD:
                return ElementType.METHOD;
            case PACKAGE:
                return ElementType.PACKAGE;
            case PARAMETER:
                return ElementType.PARAMETER;
                // no match for the following fields
            case INSTANCE_INIT:
            case ENUM:
            case ENUM_CONSTANT:
            case EXCEPTION_PARAMETER:
            case STATIC_INIT:
            case TYPE_PARAMETER:
            case OTHER:
        }

        throw new ProActiveRuntimeException("Cannot match from java.lang.annotation.ElementType." + kind +
            " to java.lang.annotation.ElementType");
    }
}
