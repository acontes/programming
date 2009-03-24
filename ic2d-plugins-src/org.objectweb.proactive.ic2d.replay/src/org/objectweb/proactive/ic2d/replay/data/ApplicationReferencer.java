package org.objectweb.proactive.ic2d.replay.data;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;


public class ApplicationReferencer {

    private static ApplicationReferencer instance;

    private WorldObject world;

    private ApplicationReferencer() {
    }

    public static ApplicationReferencer getInstance() {
        if (instance == null)
            instance = new ApplicationReferencer();
        return instance;
    }

    public void setWorldObject(WorldObject object) {
        if (object != world) {
            System.out.println("Replay: new world object received");
            this.world = object;
        }
    }

    public WorldObject getWorldObject() {
        return world;
    }
}
