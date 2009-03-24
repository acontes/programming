package org.objectweb.proactive.ic2d.replay.actions;

import org.eclipse.jface.action.Action;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;
import org.objectweb.proactive.ic2d.replay.data.ApplicationReferencer;


public class EmptyAction extends Action implements IActionExtPoint {

    public static final String EMPTYACTION = "REPLAY01_Empty Action";

    public EmptyAction() {
        setId(EMPTYACTION);
        setEnabled(true);
    }

    @Override
    public void setAbstractDataObject(AbstractData<?, ?> object) {
        if (object instanceof WorldObject) {
            WorldObject world = (WorldObject) object;
            ApplicationReferencer.getInstance().setWorldObject(world);
        }
    }

    @Override
    public void setActiveSelect(AbstractData<?, ?> ref) {
    }

}
