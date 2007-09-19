package org.objectweb.proactive.ic2d.security.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class CertificateTreeMapTransfer extends ByteArrayTransfer {

	private static final String MYTYPENAME = "CertificateTreeMap";

	private static final int MYTYPEID = registerType(MYTYPENAME);

	private static CertificateTreeMapTransfer instance;

	public static CertificateTreeMapTransfer getInstance() {
		if (instance == null) {
			instance = new CertificateTreeMapTransfer();
		}
		return instance;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}

	@Override
	protected boolean validate(Object object) {
		if (object == null || !(object instanceof CertificateTreeMap)
				|| ((CertificateTreeMap) object).size() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (!validate(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		// CertificateTreeMap trees = (CertificateTreeMap) object;
		try {
			// write data to a byte array and then ask super to convert to
			// pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream writeOut = new ObjectOutputStream(out);

			writeOut.writeObject(object);
			byte[] buffer = out.toByteArray();
			writeOut.close();
			super.javaToNative(buffer, transferData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null) {
				return null;
			}

			Object chains = null;
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream readIn = new ObjectInputStream(in);
				chains = readIn.readObject();
				readIn.close();
			} catch (IOException ex) {
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return chains;
		}

		return null;
	}

}
