package org.objectweb.proactive.core.component.adl.components;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.components.Component;
import org.objectweb.fractal.adl.components.ComponentBuilder;
import org.objectweb.fractal.adl.components.ComponentContainer;
import org.objectweb.fractal.adl.components.ComponentPair;
import org.objectweb.fractal.adl.components.PrimitiveComponentCompiler;
import org.objectweb.fractal.task.core.Task;
import org.objectweb.fractal.task.core.TaskMap;
import org.objectweb.fractal.task.deployment.api.InstanceProviderTask;
import org.objectweb.fractal.task.deployment.lib.AbstractInitializationTask;
import org.objectweb.fractal.task.deployment.lib.AbstractRequireInstanceProviderTask;


public class PAPrimitiveComponentCompiler extends PrimitiveComponentCompiler {
    @Override
    public void compile(final List path, final ComponentContainer container, final TaskMap tasks,
            final Map context) throws ADLException {

        TaskMap.TaskHole createTaskHole = tasks.getTaskHole("create", container);

        StartTask startTask = new StartTask(builder);

        startTask.setInstanceProviderTask(createTaskHole);
        tasks.addTask("start", container, startTask);

        startTask.addDependency(createTaskHole, Task.PREVIOUS_TASK_ROLE, context);

        Component[] comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            int nbComponents = 1;
            String domain = ((PAComponent) comps[i]).getDomain();
            if (domain != null) {
                nbComponents = Integer.parseInt(domain);
            }

            for (int j = 0; j < nbComponents; j++) {
                if (domain != null) {
                    comps[i].setName(comps[i].getName() + (j+1));
                }
                TaskMap.TaskHole createSubComponentTaskHole = tasks.getTaskHole("create", comps[i]);

                ComponentPair pair = new ComponentPair(container, comps[i]);
                try {
                    // the task may already exist, in case of a shared component
                    tasks.getTask("add", pair);
                } catch (NoSuchElementException e) {
                    AddTask addTask = new AddTask(builder, comps[i].getName());

                    addTask.setInstanceProviderTask(createTaskHole);
                    addTask.setSubInstanceProviderTask(createSubComponentTaskHole);

                    TaskMap.TaskHole addTaskHole = tasks.addTask("add", pair, addTask);

                    addTask.addDependency(createTaskHole, Task.PREVIOUS_TASK_ROLE, context);
                    addTask.addDependency(createSubComponentTaskHole, Task.PREVIOUS_TASK_ROLE, context);

                    startTask.addDependency(addTaskHole, Task.PREVIOUS_TASK_ROLE, context);
                }

            }

        }
    }
}
