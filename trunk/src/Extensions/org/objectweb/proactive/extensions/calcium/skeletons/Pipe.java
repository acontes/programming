/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extensions.calcium.skeletons;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;


/**
 * The Pipe skeleton represents staged computation.
 * A Pipe will execute each skeleton in sequence of the next.
 *
 * @author The ProActive Team (mleyton)
 */
@PublicAPI
public class Pipe<P extends java.io.Serializable, R extends java.io.Serializable>
    implements Skeleton<P, R> {
    Vector<Skeleton<?, ?>> stages;

    public <X extends Serializable>Pipe(Skeleton<P, X> child1,
        Skeleton<X, R> child2) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(child1);
        stages.add(child2);
    }

    public <X extends Serializable>Pipe(Execute<P, X> child1,
        Execute<X, R> child2) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(new Seq<P, X>(child1));
        stages.add(new Seq<X, R>(child2));
    }

    public <X extends Serializable, Y extends Serializable>Pipe(
        Skeleton<P, X> child1, Skeleton<X, Y> child2, Skeleton<Y, R> child3) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(child1);
        stages.add(child2);
        stages.add(child3);
    }

    public <X extends Serializable, Y extends Serializable>Pipe(
        Execute<P, X> child1, Execute<X, Y> child2, Execute<Y, R> child3) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(new Seq<P, X>(child1));
        stages.add(new Seq<X, Y>(child2));
        stages.add(new Seq<Y, R>(child3));
    }

    public <X extends Serializable, Y extends Serializable, Z extends Serializable>Pipe(
        Skeleton<P, X> child1, Skeleton<X, Y> child2, Skeleton<Y, Z> child3,
        Skeleton<Z, R> child4) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(child1);
        stages.add(child2);
        stages.add(child3);
        stages.add(child4);
    }

    public <X extends Serializable, Y extends Serializable, Z extends Serializable>Pipe(
        Execute<P, X> child1, Execute<X, Y> child2, Execute<Y, Z> child3,
        Execute<Z, R> child4) {
        stages = new Vector<Skeleton<?, ?>>();

        stages.add(new Seq<P, X>(child1));
        stages.add(new Seq<X, Y>(child2));
        stages.add(new Seq<Y, Z>(child3));
        stages.add(new Seq<Z, R>(child4));
    }

    public void accept(SkeletonVisitor visitor) {
        visitor.visit(this);
    }
}
