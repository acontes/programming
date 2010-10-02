package org.objectweb.proactive.core.component.adl.luc.description;

import java.io.File;
import java.io.IOException;

import lucci.text.xml.XMLNode;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public abstract class Description
{
	private Description parentDescription;
	private File file;

	public void setFile(File file)
	{
		this.file = file;
	}

	public File getFile()
	{
		if (this.file == null)
		{
			if (getParentDescription() == null)
			{
				return null;
			}
			else
			{
				return getParentDescription().getFile();
			}
		}
		else
		{
			return file;
		}
	}

	public Description getParentDescription()
	{
		return parentDescription;
	}

	public void setParentDescription(Description parentDescription)
	{
		this.parentDescription = parentDescription;
	}

	@Override
	public String toString()
	{
		return toXMLNode().toString();
	}

	public abstract XMLNode toXMLNode();

	/**
	 * Because the instantiation of the component may involve networking, remote
	 * resource, etc... It is advisable to check things first! Without
	 * distribution, we would normally checks things on-the-fly, during
	 * instantiation.
	 * 
	 * @throws ADLException
	 */
	public abstract void check() throws ADLException;

	public File getRelativeFile(String path) throws IOException
	{
		return new File(getFile().getParentFile().getAbsolutePath() + File.separator + path).getAbsoluteFile();
	}
}