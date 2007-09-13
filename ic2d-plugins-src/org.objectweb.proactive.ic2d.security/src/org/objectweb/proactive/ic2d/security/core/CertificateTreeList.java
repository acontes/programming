package org.objectweb.proactive.ic2d.security.core;

import java.util.ArrayList;
import java.util.Collection;

import javassist.NotFoundException;

import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;

public class CertificateTreeList extends ArrayList<CertificateTree> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8097072392302851020L;

	public CertificateTreeList() {
		super();
	}

	public CertificateTreeList(CertificateTreeList list) {
		super();
		addAll(list);
	}

	public TypedCertificate search(String name, EntityType type)
			throws NotFoundException {
		for (CertificateTree ct : this) {
			try {
				return ct.search(name, type);
			} catch (NotFoundException nfe) {
				// let's check the other trees
			}
		}

		throw new NotFoundException("Certificate " + name + " : " + type
				+ " not found.");
	}

	@Override
	public boolean add(CertificateTree newTree) {
		for (CertificateTree tree : this) {
			if (tree.merge(newTree)) {
				return true;
			}
		}

		return super.add(newTree);
	}

	@Override
	public boolean addAll(Collection<? extends CertificateTree> c) {
		for (CertificateTree tree : c) {
			add(tree);
		}
		return true;
	}

	public boolean remove(CertificateTree tree) {
		if (!tree.remove()) {
			return super.remove(tree);
		}
		return true;
	}
}
