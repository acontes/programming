package org.objectweb.proactive.extra.vfs.PAProvider;

import java.util.ArrayList;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Active;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.*;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

public class PAFileProviderService 
implements ProActiveInternalObject, InitActive

{

	ArrayList<Object> siblings = new ArrayList<Object>();
	
	public void siblingActiveObjects(PAActiveObject AO)
	{
		try {
			for(int i=0;i< (AO.getNode().getActiveObjects()).length;i++)
            siblings.add(i);
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void getPAProviderOutPut(ArrayList<Object> AOList)
	{
		for(int i=0;i<siblings.size();i++)
		{
		  try {
			Node node = ((PAActiveObject)siblings.get(i)).getNode();
			
			FileObject[] allFiles = ProActiveVFS.getProActiveVFS().getRoot().findFiles(new AllFileSelector());
			
		     
			System.out.println("===> Number of files in VFS : " + allFiles.length);
			
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		}
	}

	@Override
	public void initActivity(Body body) {
		// TODO Auto-generated method stub
		
	}
	
	
}
