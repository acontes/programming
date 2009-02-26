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
package org.objectweb.proactive.extensions.annotation.callbacks.isready;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extensions.annotation.common.ErrorMessages;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;


public class VirtualNodeIsReadyCallbackVisitorCTree extends TreePathScanner<Void, Trees> {

    private Messager compilerOutput;

    public VirtualNodeIsReadyCallbackVisitorCTree(ProcessingEnvironment procEnv) {
        compilerOutput = procEnv.getMessager();
    }

    @Override
    public Void visitMethod(MethodTree methodNode, Trees trees) {

        boolean correctSignature = false;

        if (returnsVoid(methodNode) && methodNode.getParameters().size() == 1) {
            if (methodNode.getParameters().get(0).getType().toString().equals(String.class.getSimpleName())) {
                correctSignature = true;
            }
        }

        if (!correctSignature) {
            compilerOutput.printMessage(Diagnostic.Kind.ERROR,
                    ErrorMessages.INCORRECT_METHOD_SIGNATURE_FOR_ISREADY_CALLBACK, trees
                            .getElement(getCurrentPath()));
        }

        return super.visitMethod(methodNode, trees);
    }

    private boolean returnsVoid(MethodTree methodNode) {
        if (methodNode.getReturnType().getKind().equals(Tree.Kind.PRIMITIVE_TYPE)) {
            PrimitiveTypeTree retType = (PrimitiveTypeTree) methodNode.getReturnType();
            return retType.getPrimitiveTypeKind().equals(TypeKind.VOID);
        }
        return false;
    }

}