package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dmeneses.maptpg.Main;
import dmeneses.maptpg.model.ListWrapper;


public abstract class Collector {
	static public String cacheRoot = Main.cacheRoot;
	
	public static void setCacheRoot(String path) {
		Collector.cacheRoot = path;
	}
	protected static <T> void marshal(Class<T> clazz, List<Class<?>> classes, List<T> root, String name, String path) throws JAXBException {
		File file = new File(path);
		new File(file.getParent()).mkdirs();
		
		QName qName = new QName(name);
		ListWrapper<T> list = new ListWrapper<T>();
		list.setItems(root);

		JAXBContext jc = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
		Marshaller marshaller = jc.createMarshaller();

		// output pretty printed
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		@SuppressWarnings("rawtypes")
		JAXBElement<ListWrapper> jaxbElement = new JAXBElement<ListWrapper>(qName, ListWrapper.class, list);
		marshaller.marshal(jaxbElement, file);
	}

	protected static <T> List<T> unmarshal(Class<T> clazz, List<Class<?>> classes, String uri, String root) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		/*
		 * get connection/file
		 */
		InputStream in = null;
		try {
			if(!uri.startsWith("http")) { //files
				in = new FileInputStream(new File(uri));
			}
			else {
				URL url = new URL(uri);
				URLConnection conn = url.openConnection();
				in =conn.getInputStream();
			}
			/*
			 * get xml document
			 */
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(in);

			/*
			 * extract subtree root
			 */
			Element rootElement = doc.getDocumentElement();
			Node subtree = rootElement;
			
			NodeList nl = rootElement.getElementsByTagName(root);
			if(nl.getLength() != 0) {
				subtree = rootElement.getElementsByTagName(root).item(0);
			}

			/*
			 * get unmarshaller
			 */
			JAXBContext jc = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			/*
			 * unmarshall it
			 */
			@SuppressWarnings("unchecked")
			ListWrapper<T> wrapper = (ListWrapper<T>) unmarshaller.unmarshal(subtree, ListWrapper.class).getValue();

			return wrapper.getItems();

		} finally {
			if(in != null) {
				in.close();
			}
		}
	}
}
