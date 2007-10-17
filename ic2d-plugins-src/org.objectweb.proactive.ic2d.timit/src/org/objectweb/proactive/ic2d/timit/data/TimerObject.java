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
package org.objectweb.proactive.ic2d.timit.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.objectweb.proactive.benchmarks.timit.util.basic.BasicTimer;


public class TimerObject extends AbstractObject {
    public static final String P_CHILDREN = "0";
    public static final String P_LABEL = "1";
    public static final String P_SELECTION = "2";
    public static final String P_VIEW = "3";
    public static final String P_EXPAND_STATE = "4";
    public static final String P_SORT = "5";
    protected double percentageFromParent;
    protected double percentageFromTotal;
    protected double currentTotalTimeInMsInDouble;
    protected String labelName;
    protected TimerObject parent;
    protected TimerObject totalTimer;
    protected BasicTimer currentTimer;
    protected List<TimerObject> children;

    /** boolean used during the build of a chart **/
    protected boolean isViewed;

    public TimerObject(String name, boolean isViewed) {
        this.labelName = name;
        this.isViewed = isViewed;
        this.children = new ArrayList<TimerObject>();
    }

    public TimerObject(BasicTimer currentTimer, TimerObject parent) {
        this.parent = parent;
        this.currentTimer = currentTimer;
        this.labelName = currentTimer.getName();
        this.isViewed = true;
        if (parent != null) {
            parent.children.add(this);
        }
        this.children = new ArrayList<TimerObject>();
    }

    public final void setTotalTimerAndCompute(final TimerObject totalTimer) {
        this.totalTimer = totalTimer;
        if (this.currentTimer.getTotalTime() != 0L) {
            this.compute();
        }
    }

    public final void updateCurrentTimerAndCompute(
        final BasicTimer currentTimer) {
        this.currentTimer = currentTimer;
        if (this.currentTimer.getTotalTime() != 0L) {
            this.compute();
        }
    }

    /**
     * TODO : OPTIMIZE ME
     */
    private final void compute() {
        // Compute current time (convert from ns to ms)
        Long tempTotal = new Long(this.currentTimer.getTotalTime());
        this.currentTotalTimeInMsInDouble = tempTotal.doubleValue() / 1000000d;

        // Compute total percentage
        this.percentageFromTotal = (this.currentTotalTimeInMsInDouble * 100d) / this.totalTimer.currentTotalTimeInMsInDouble;

        // Compute parent percentage
        if ((this.parent != null) &&
                (this.parent.currentTimer.getTotalTime() != 0)) {
            this.percentageFromParent = (this.currentTotalTimeInMsInDouble * 100d) / this.parent.currentTotalTimeInMsInDouble;
        }
    }

    public List<TimerObject> getChildren() {
        return this.children;
    }

    public String getLabelName() {
        return this.labelName;
    }

    public String toString() {
        return this.labelName;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean isViewedNew) {
        if (this.parent == null) {
            return;
        }
        if (this.isViewed == true) {
            if (isViewedNew == false) {
                // Remove this from parent			
                this.parent.children.remove(this);
            }
        } else {
            if ((isViewedNew == true) && !this.parent.children.contains(this)) {
                // Add this to parent
                this.parent.children.add(this);
            }
        }

        this.isViewed = isViewedNew;
    }

    public TimerObject getParent() {
        return parent;
    }

    public void setParent(TimerObject parent) {
        this.parent = parent;
    }

    public BasicTimer getCurrentTimer() {
        return currentTimer;
    }

    public double getCurrentTotalTimeInDouble() {
        return currentTotalTimeInMsInDouble;
    }

    public final String getFormatedCurrentTotalTimeInDouble() {
        return String.format(Locale.US, "%1.2f",
            this.currentTotalTimeInMsInDouble);
    }

    public final String getFormatedPercentageFromParent() {
        return String.format(Locale.US, "%1.2f", this.percentageFromParent);
    }

    public final String getFormatedPercentageFromTotal() {
        return String.format(Locale.US, "%1.2f", this.percentageFromTotal);
    }

    // SORT CHILDREN UTILITY METHODS
    public final void sortChildrenByTime(final boolean up) {
        Collections.sort(this.children,
            new Comparator<TimerObject>() {
                public final int compare(final TimerObject t1,
                    final TimerObject t2) {
                    return (up
                    ? Double.compare(t1.currentTotalTimeInMsInDouble,
                        t2.currentTotalTimeInMsInDouble)
                    : Double.compare(t2.currentTotalTimeInMsInDouble,
                        t1.currentTotalTimeInMsInDouble));
                }
            });
    }

    public final void sortChildrenByTotalPercent(final boolean up) {
        Collections.sort(this.children,
            new Comparator<TimerObject>() {
                public final int compare(final TimerObject t1,
                    final TimerObject t2) {
                    return (up
                    ? Double.compare(t1.currentTotalTimeInMsInDouble,
                        t2.currentTotalTimeInMsInDouble)
                    : Double.compare(t2.currentTotalTimeInMsInDouble,
                        t1.currentTotalTimeInMsInDouble));
                }
            });
    }

    public final void sortChildrenByInvocations(final boolean up) {
        Collections.sort(this.children,
            new Comparator<TimerObject>() {
                public final int compare(final TimerObject t1,
                    final TimerObject t2) {
                    return (up
                    ? ((Integer) t1.currentTimer.getStartStopCoupleCount()).compareTo(t2.currentTimer.getStartStopCoupleCount())
                    : ((Integer) t2.currentTimer.getStartStopCoupleCount()).compareTo(t1.currentTimer.getStartStopCoupleCount()));
                }
            });
    }

    public final void sortChildrenByParentPercent(final boolean up) {
        Collections.sort(this.children,
            new Comparator<TimerObject>() {
                public final int compare(final TimerObject t1,
                    final TimerObject t2) {
                    return (up
                    ? Double.compare(t1.percentageFromParent,
                        t2.percentageFromParent)
                    : Double.compare(t2.percentageFromParent,
                        t1.percentageFromParent));
                }
            });
    }
}
