package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;

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
//		Vector3f sophia = new Vector3f(28f,0,23f);
//		Vector3f paris = new Vector3f(3f,0f,-23f);
//		Vector3f bordeaux = new Vector3f(-20f,0f,19.5f);
//		Vector3f toulouse = new Vector3f(-5.5f,0f,32.5f);
//		Vector3f lyon = new Vector3f(18.5f, 0f, 11f);
//		Vector3f lille = new Vector3f(1.5f, 0f, -33.5f);
//		Vector3f orsay = new Vector3f(0f,0f,-19.5f);;
//		Vector3f grenoble = new Vector3f(26.5f, 0f, 16f);
//		Vector3f nancy = new Vector3f(29f, 0f, -21.5f);
//		Vector3f rennes = new Vector3f(-26.5f, 0f, -15.5f);;
		Vector3f bordeaux = new Vector3f(-0.56f, 44.83f, 0f); // D: -0.56 O ; 44.83
		Vector3f grenoble = new Vector3f(5.71f, 45.16f, 0f); // D: 5.71 E ; 45.16
		Vector3f lille = new Vector3f(3.06f, 50.63f, 0f); // D: 3.06 E; 50.63
		Vector3f lyon = new Vector3f(4.85f, 45.75f, 0f); // D: 4.85 E ; 45.75
		Vector3f nancy = new Vector3f(6.2f, 48.68f, 0f); // D: 6.2 E; 48.68
		Vector3f orsay = new Vector3f(2.18f, 48.7f, 0f); // D: 2.18E; 48.7
		Vector3f paris = new Vector3f(2.33f,48.86f,0f); // D: 2.33 E , 48.86
		Vector3f rennes = new Vector3f(-1.68f, 48.08f, 0f); // D:-1.68 O; 48.08
		Vector3f sophia = new Vector3f(7f, 43.63f, 0f); // D: 7.0 E, 43.63
		Vector3f toulouse = new Vector3f(1.43f, 43.6f, 0f); // D: 1.43 E   ; 43.6
		
		siteFlatLocation.put("bordeaux", toFlat(bordeaux));
		siteFlatLocation.put("grenoble", toFlat(grenoble));
		siteFlatLocation.put("lille", toFlat(lille));
		siteFlatLocation.put("lyon", toFlat(lyon));
		siteFlatLocation.put("nancy", toFlat(nancy));
		siteFlatLocation.put("orsay", toFlat(orsay));
		siteFlatLocation.put("paris", toFlat(paris));
		siteFlatLocation.put("rennes", toFlat(rennes));
		siteFlatLocation.put("sophia", toFlat(sophia));
		siteFlatLocation.put("toulouse", toFlat(toulouse));
		
		siteSphereLocation.put("bordeaux", toSphere(bordeaux));
		siteSphereLocation.put("grenoble", toSphere(grenoble));
		siteSphereLocation.put("lille", toSphere(lille));
		siteSphereLocation.put("lyon", toSphere(lyon));
		siteSphereLocation.put("nancy", toSphere(nancy));
		siteSphereLocation.put("orsay", toSphere(orsay));
		siteSphereLocation.put("paris", toSphere(paris));
		siteSphereLocation.put("rennes", toSphere(rennes));
		siteSphereLocation.put("sophia", toSphere(sophia));
		siteSphereLocation.put("toulouse", toSphere(toulouse));
		
		hostSites.put("segfault.inria.fr", "rennes");
		hostSites.put("saturn.inria.fr", "paris");
		hostSites.put("cobreloa.inria.fr", "grenoble");
		hostSites.put("bego.inria.fr", "lille");
		hostSites.put("taillante.inria.fr", "lyon");
		hostSites.put("subzero.inria.fr", "nancy");
		hostSites.put("shainese.inria.fr", "orsay");
		hostSites.put("sgouirk.inria.fr", "paris");
		hostSites.put("paquito.inria.fr", "rennes");
		hostSites.put("macondo.inria.fr", "bordeaux");
		hostSites.put("lo.inria.fr", "toulouse");
		hostSites.put("jily.inria.fr", "lille");
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

	private static Vector3f toFlat(Vector3f location) {
		Vector3f flatLocation = new Vector3f();
		flatLocation.x = location.x * GeometryBasket.MAP_TILE * GeometryBasket.TILE_X / 360;
		flatLocation.z = -location.y * GeometryBasket.MAP_TILE * GeometryBasket.TILE_Y / 180;
		return(flatLocation);
	}
	
	private static Vector3f toSphere(Vector3f location) {
		Vector3f sphereLocation = new Vector3f();
		
		//sphereLocation.x = location.x;
		//if ( location.x < 0 ) sphereLocation.x += 360f;
		sphereLocation.x = location.x / 180f;
		sphereLocation.x *= (float)Math.PI;
		
		//if(sphereLocation.x < 0) sphereLocation.x += 2 * Math.PI;
		sphereLocation.y = 90 - location.y;
		sphereLocation.y /= 180f;
		sphereLocation.y *= Math.PI;
		return(sphereLocation);
	}
	

	public static Point3d getTownLocation(String string) {
		Vector3f toReturn = siteFlatLocation.get(string);
		return new Point3d(toReturn);
	}
}
