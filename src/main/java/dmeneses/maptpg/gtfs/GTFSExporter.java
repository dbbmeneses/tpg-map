package dmeneses.maptpg.gtfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.common.base.Stopwatch;

import lombok.extern.log4j.Log4j2;
import dmeneses.maptpg.config.Configuration;
import dmeneses.maptpg.database.XmlDao;
import dmeneses.maptpg.datacollection.model.XMLPhysicalStop;
import dmeneses.maptpg.datacollection.model.XMLStop;

@Log4j2
public class GTFSExporter {
	protected static String GTFS_ROOT = Configuration.CACHE_ROOT 
			+ File.separator + "gtfs" + File.separator;

	public void export(XmlDao dao) throws IOException {
		Stopwatch watch = Stopwatch.createStarted();
		log.info("Export GTFS");
		exportStops(dao);
		exportAgency();
		log.info("Export done ({})", watch);
	}

	private void exportAgency() throws IOException {
		try(BufferedWriter fw = createFile("agency.txt")) {
			fw.write("agency_id, agency_name,agency_url,agency_timezone,agency_phone,agency_lang");
			fw.newLine();

			fw.write("0,Transports publics genevois,www.tpg.ch,Europe/Zurich,+41 22 308 33 11,fr");
			fw.newLine();
		}
	}

	private BufferedWriter createFile(String name) throws IOException {
		File file = new File(GTFS_ROOT + name);
		new File(file.getParent()).mkdirs();
		return new BufferedWriter(new FileWriter(file));
	}

	private void exportStops(XmlDao dao) throws IOException {
		try(BufferedWriter fw = createFile("stops.txt")) {
			fw.write("stop_id,stop_name,stop_lat,stop_lon");
			fw.newLine();

			StringBuffer sb = new StringBuffer();
			int id = 0;

			for(XMLStop s : dao.getPhysicalStops()) {
				for(XMLPhysicalStop ph : s.getPhysicalStops()) {
					sb.setLength(0);
					sb.append(id++).append(",");
					sb.append(ph.getCode()).append(",");
					sb.append(ph.getName()).append(",");
					sb.append(ph.getLocation().getLatitude()).append(",");
					sb.append(ph.getLocation().getLongitude()).append(",");
					fw.write(sb.toString());
					fw.newLine();
				}
			}
		}
	}
}
