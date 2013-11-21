package dmeneses.maptpg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.xml.sax.SAXException;

import dmeneses.maptpg.image.gradient.Gradients.GRADIENTS;
import dmeneses.maptpg.process.Itinerary.DATA_TYPE;
import dmeneses.maptpg.utils.LogFormatter;


public class Command {
	private final static Logger log = Logger.getLogger(Command.class.getName());
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		Options generate = new Options();
		//OptionGroup generate = new OptionGroup();

		generate.addOption(OptionBuilder.withArgName("gradient_type")
				.isRequired(false)
				.hasArg()
				.withDescription("Which gradient to use. Defaults to 'linear_hue'")
				.withLongOpt("gradient")
				.withType(String.class)
				.create());

		generate.addOption(OptionBuilder.withArgName("size")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Size of the side of the square of points to calculate. Defaults to 251")
				.withLongOpt("points")
				.withType(Integer.class)
				.create('p'));

		generate.addOption(OptionBuilder.withArgName("pixels")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Resolution of the image. Defaults to 1000.")
				.withLongOpt("resolution")
				.withType(Integer.class)
				.create('r'));

		generate.addOption(OptionBuilder.withArgName("file_path")
				.isRequired(false)
				.hasArgs()
				.withDescription("Location of the database file. Can be raw data to process or the itineraries " +
						"previously calculated, in which case the image will be generated directly from it. " +
						"Also, it can be a file in the file system or a url")
				.withLongOpt("database")
				.withType(String.class)
				.create('d'));

		generate.addOption(OptionBuilder.withArgName("verbose")
				.isRequired(false)
				.hasArg(false)
				.withDescription("Verbose mode - logs debug messages")
				.withLongOpt("verbose")
				.create('v'));

		generate.addOption(OptionBuilder.withArgName("location")
				.isRequired(true)
				.hasArg(true)
				.withDescription("Name of the location to be the source of the itineraries. Defaults to CERN.")
				.withLongOpt("source")
				.create('s'));

		generate.addOption(OptionBuilder.withArgName("file_path")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Name of the output files. Defaults to a random name." +
						"Ideally, it will identify the options used. It can be a full path.")
				.withLongOpt("output")
				.create('o'));

		generate.addOption(OptionBuilder.withArgName("start_hour")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Hour at which the itinerary starts (0-24). Defaults to 15.")
				.withLongOpt("hour")
				.withType(Integer.class)
				.create('h'));

		generate.addOption(OptionBuilder.withArgName("type")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Type of data to display. Defaults to 'time'")
				.withLongOpt("datatype")
				.withType(Integer.class)
				.create('t'));

		generate.addOption(OptionBuilder.withArgName("maximum")
				.isRequired(false)
				.hasArg(true)
				.withDescription("Maximum of the scale. By default, it's the maximum of the entire dataset")
				.withType(Integer.class)
				.create('m'));

		boolean bolValue;
		int intValue;
		double doubleValue;
		String s;
		CommandLineParser parser = new BasicParser();
		
		try {
			CommandLine cmd = parser.parse(generate, args);

			//gradient
			s = cmd.getOptionValue('g');
			if(s != null) {
				GRADIENTS g = GRADIENTS.valueOf(GRADIENTS.class, s.toUpperCase());
				Main.setGradientType(g);
			}

			//number of points
			s = cmd.getOptionValue('p');
			if(s != null) {
				intValue = Integer.parseInt(s);
				Main.setNumPoints(intValue);
			}

			//image resolution
			s = cmd.getOptionValue('r');
			if(s != null) {
				intValue = Integer.parseInt(s);
				Main.setImageSize(intValue);
			}

			//verbose
			bolValue = false;
			s = cmd.getOptionValue('v');
			if(s != null) {
				bolValue = Boolean.parseBoolean(s);
			}
			configLog(bolValue);

			//location of the source
			s = cmd.getOptionValue('s');
			Main.setSourceLocation(s);

			//location of the database
			s = cmd.getOptionValue('d');
			Main.setLoadPath(s);
			
			//Output image/data path
			s = cmd.getOptionValue('o');
			if(s != null) {
				Main.setName(s);
			}

			//Start hour
			s = cmd.getOptionValue('h');
			if(s != null) {
				intValue = Integer.parseInt(s);

				if(intValue > 24 || intValue < 0) {
					throw new Exception("Invalid start hour!");
				}
				Main.setStartHour(intValue);
			}

			//datatype
			s = cmd.getOptionValue('t');
			if(s != null) {
				DATA_TYPE dt = DATA_TYPE.valueOf(DATA_TYPE.class, s.toUpperCase());
				Main.setDataType(dt);
			}

			//Maximum of the scale
			s = cmd.getOptionValue('m');
			if(s != null) {
				doubleValue = Double.parseDouble(s);
				Main.setMaxScale(doubleValue);
			}
		} catch (Exception e) {
			HelpFormatter formatter = new HelpFormatter();
			log.severe(e.getMessage() + "\n");
			formatter.printHelp("tpgmap", generate, true);
			return;
		}
		
		log.info("Parameters parsed");
		Main.generate();
	}
	
	private static void configLog(boolean bolValue) {
		LogManager.getLogManager().reset();
		Logger mainLogger = LogManager.getLogManager().getLogger("");
		
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LogFormatter());
		mainLogger.addHandler(handler);
		
		if(bolValue) {
			mainLogger.setLevel(Level.FINE); 
			handler.setLevel(Level.FINE);
		}
		else {
			mainLogger.setLevel(Level.CONFIG); 
			handler.setLevel(Level.CONFIG);
		}
		
	}
}
