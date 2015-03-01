package maptpg;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j2;
import objectexplorer.MemoryMeasurer;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.XmlDao;
import dmeneses.maptpg.gtfs.GTFSExporter;
import dmeneses.maptpg.utils.Tools;

@Log4j2
public class PersistenceTest {
	XmlDao p = new XmlDao();
	DAO dao = null;
	
	@Test
	@Ignore
	public void testLoad() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		p.loadCache();
		
		log.debug("Size persistence: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(p)));
		p.cacheData();
	}
	
	@Test
	@Ignore
	public void testDAO() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		testLoad();
		dao = new DAO(p);
		log.debug("Size DAO: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(dao)));
	}
	
	@Test
	public void testGTFS() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		testDAO();
		
		GTFSExporter exporter = new GTFSExporter();
		
		exporter.export(p);
	}
}
