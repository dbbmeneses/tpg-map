package maptpg;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j2;
import objectexplorer.MemoryMeasurer;

import org.junit.Test;
import org.xml.sax.SAXException;

import dmeneses.maptpg.database.Persistence;
import dmeneses.maptpg.utils.Tools;

@Log4j2
public class PersistenceTest {
	Persistence p = new Persistence();
	
	@Test
	public void testLoad() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		p.loadCache();
		
		log.debug("Size persistence: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(p)));
		p.cacheData();
		
	}
}
