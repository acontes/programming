package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Vector3f;

public class SiteBasket {
	
	private static HashMap<String, String> hostSites = new HashMap<String, String>();
	private static HashMap<String, Vector3f> siteFlatLocation = new HashMap<String, Vector3f>();
	private static HashMap<String, Vector3f> siteSphereLocation = new HashMap<String, Vector3f>();
	private static Pattern grid5000Pattern; 
	/*
	 * Return the site of one host
	 * @param hostname the name of the host
	 */
	public static String getSite(String hostname) {
		String site = hostSites.get(hostname);
		Matcher match = grid5000Pattern.matcher(hostname);
		if(match.matches()) {
			int dot = hostname.indexOf('.');
			site = hostname.substring(dot + 1, hostname.length());
			dot = site.indexOf('.');
			site = site.substring(0, dot);
			System.out.println(site);
		}
		if(site != null)
			return site;
		return "sophia";
	}
	
	public static Vector3f getFlatLocation(String hostname) {
		String site = getSite(hostname);
		return siteFlatLocation.get(site);
	}
	

	public static Vector3f getSphereLocation(String hostname) {
		String site = getSite(hostname);
		return siteSphereLocation.get(site);
	}
	
	public static void init() {
		/* Omitted by default */
		//luxembourg
		//portoalegre
		grid5000Pattern = Pattern.compile(".*grid5000.*");
		Vector3f sophia = new Vector3f(28f,0,23f);
		Vector3f paris = new Vector3f(0.5f,0f,-20f);
		Vector3f bordeaux = new Vector3f(-18f,0f,15f);
		Vector3f toulouse = new Vector3f(-6f,0f,26f);
		Vector3f lyon = new Vector3f(15.5f, 0f, 7f);
		Vector3f lille = new Vector3f(4.5f, 0f, -35f);
		Vector3f orsay = new Vector3f(-1f,0f,-18f);;
		Vector3f grenoble = new Vector3f(21.5f, 0f, 13f);
		Vector3f nancy = new Vector3f(23f, 0f, -19f);
		Vector3f rennes = new Vector3f(-23f, 0f, -14f);;
		
		siteFlatLocation.put("bordeaux", bordeaux);
		siteFlatLocation.put("grenoble", grenoble);
		siteFlatLocation.put("lille", lille);
		siteFlatLocation.put("lyon", lyon);
		siteFlatLocation.put("nancy", nancy);
		siteFlatLocation.put("orsay", orsay);
		siteFlatLocation.put("paris", paris);
		siteFlatLocation.put("rennes", rennes);
		siteFlatLocation.put("sophia", sophia);
		siteFlatLocation.put("toulouse", toulouse);
		hostSites.put("segfault.inria.fr", "rennes");
		hostSites.put("saturn.inria.fr", "paris");
		hostSites.put("cobreloa.inria.fr", "grenoble");
		hostSites.put("bego.inria.fr", "lille");
		hostSites.put("taillante.inria.fr", "lyon");
		hostSites.put("subzero.inria.fr", "nancy");
		hostSites.put("shainese.inria.fr", "orsay");
		hostSites.put("sgouirk.inria.fr", "paris");
		hostSites.put("paquito.inria.fr", "rennes");
		hostSites.put("macondo.inria.fr", "sophia");
		hostSites.put("lo.inria.fr", "sophia");
		hostSites.put("jily.inria.fr", "sophia");
		hostSites.put("islamabad.inria.fr", "sophia");
		hostSites.put("dalmatie.inria.fr", "sophia");
		hostSites.put("crios.inria.fr", "sophia");
		hostSites.put("meije", "sophia");
		hostSites.put("maledie", "sophia");
		hostSites.put("lilo", "sophia");
		hostSites.put("bebita", "sophia");
		hostSites.put("sidonie", "sophia");
		hostSites.put("puravida", "sophia");
		hostSites.put("predadab", "sophia");
		hostSites.put("noadcoco", "sophia");
		hostSites.put("crusoe", "sophia");
		hostSites.put("sea", "sophia");
		hostSites.put("hajjoura", "sophia");
		hostSites.put("anaconda", "sophia");
		hostSites.put("amda", "sophia");
		hostSites.put("nahuel", "sophia");
		hostSites.put("duff", "sophia");
		hostSites.put("naruto", "sophia");
		hostSites.put("thanglong", "sophia");
		hostSites.put("tche", "sophia");
		hostSites.put("pincoya", "sophia");
		hostSites.put("mac", "sophia");
		hostSites.put("heli", "sophia");
		hostSites.put("fidelity", "sophia");
		hostSites.put("schubby", "sophia");
		hostSites.put("petawawa", "sophia");
		hostSites.put("cheypa", "sophia");
		hostSites.put("psychoquack", "sophia");
		hostSites.put("pollux.inria.fr", "sophia");
		hostSites.put("orchidee.inria.fr", "sophia");
		hostSites.put("nyx.inria.fr", "sophia");
		hostSites.put("gaudi.inria.fr", "sophia");
		hostSites.put("eon9.inria.fr", "sophia");
		hostSites.put("eon1.inria.fr", "sophia");
		hostSites.put("eon7.inria.fr", "sophia");
		hostSites.put("eon2.inria.fr", "sophia");
		hostSites.put("eon18.inria.fr", "sophia");
		hostSites.put("eon13.inria.fr", "sophia");
		hostSites.put("yala", "sophia");
		hostSites.put("ulysse", "sophia");
		hostSites.put("tulipe", "sophia");
		hostSites.put("trinidad", "sophia");
		hostSites.put("trans20", "sophia");
		hostSites.put("trans19", "sophia");
		hostSites.put("trans15", "sophia");
		hostSites.put("trans14", "sophia");
		hostSites.put("trans13", "sophia");
		hostSites.put("trans12", "sophia");
		hostSites.put("trans11", "sophia");
		hostSites.put("trans10", "sophia");
		hostSites.put("trans09", "sophia");
		hostSites.put("trans08", "sophia");
		hostSites.put("trans07", "sophia");
		hostSites.put("trans06", "sophia");
		hostSites.put("trans05", "sophia");
		hostSites.put("trans04", "sophia");
		hostSites.put("trans03", "sophia");
		hostSites.put("trans02", "sophia");
		hostSites.put("trans01", "sophia");
		hostSites.put("toupti", "sophia");
		hostSites.put("smart", "sophia");
		hostSites.put("smart1", "sophia");
		hostSites.put("scurra", "sophia");
		hostSites.put("scotti", "sophia");
		hostSites.put("sable", "sophia");
		hostSites.put("orbita", "sophia");
		hostSites.put("oasis2", "sophia");
		hostSites.put("oasis1", "sophia");
		hostSites.put("nile", "sophia");
		hostSites.put("mirage", "sophia");
		hostSites.put("mage", "sophia");
		hostSites.put("macyavel", "sophia");
		hostSites.put("lambis", "sophia");
		hostSites.put("lalime", "sophia");
		hostSites.put("krios", "sophia");
		hostSites.put("kresken", "sophia");
		hostSites.put("kevin", "sophia");
		hostSites.put("karst", "sophia");
		hostSites.put("gulliver", "sophia");
		hostSites.put("fiacre", "sophia");
		hostSites.put("eon8.inria.fr", "sophia");
		hostSites.put("eon6.inria.fr", "sophia");
		hostSites.put("eon5.inria.fr", "sophia");
		hostSites.put("eon4.inria.fr", "sophia");
		hostSites.put("eon3.inria.fr", "sophia");
		hostSites.put("eon20.inria.fr", "sophia");
		hostSites.put("eon19.inria.fr", "sophia");
		hostSites.put("eon17.inria.fr", "sophia");
		hostSites.put("eon16.inria.fr", "sophia");
		hostSites.put("eon15.inria.fr", "sophia");
		hostSites.put("eon14.inria.fr", "sophia");
		hostSites.put("eon12.inria.fr", "sophia");
		hostSites.put("eon11.inria.fr", "sophia");
		hostSites.put("eon10.inria.fr", "sophia");
		hostSites.put("dioune", "sophia");
		hostSites.put("camelot", "sophia");
		hostSites.put("biloute", "sophia");
		hostSites.put("atacama", "sophia");
		hostSites.put("apple", "sophia");
		hostSites.put("amstel", "sophia");
		hostSites.put("aglae", "sophia");
		hostSites.put("adrar", "sophia");
	}
}
