package dmeneses.maptpg.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.process.Itinerary;

public class ResultsManager {
	public static void save(String path, List<Itinerary> itineraries) throws IOException {
		FileWriter fw = new FileWriter(path);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Itinerary i : itineraries) {
			StringBuilder sb = new StringBuilder(1000);
			
			sb.append(i.getSrc().getLatitude() + ",");
			sb.append(i.getSrc().getLongitude() + ",");
			sb.append(i.getDst().getLatitude() + ",");
			sb.append(i.getDst().getLongitude() + ",");
			sb.append(i.getStartTime().getTime() + ",");
			sb.append(i.getEndTime().getTime()  + ",");
			sb.append(i.getWalkTime() + ",");
			sb.append(i.getSteps());
			sb.append("\n");
			
			bw.write(sb.toString());
		}
		
		bw.close();
	}
	
	public static List<Itinerary> load(String path) throws IOException {
		List<Itinerary> itineraries = new ArrayList<Itinerary>();
		FileReader fw = new FileReader(path);
		BufferedReader bw = new BufferedReader(fw);
		String line = null;
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		while((line = bw.readLine()) != null) {
			String[] csv = line.split(",");
			
			if(csv.length != 8) {
				System.err.println("error reading line");
				continue;
			}
			
			LatLng src = new LatLng(Double.parseDouble(csv[0]), Double.parseDouble(csv[1]));
			LatLng dst = new LatLng(Double.parseDouble(csv[2]), Double.parseDouble(csv[3]));
			c1.setTimeInMillis(Long.parseLong(csv[4]));
			Date start = c1.getTime();
			c2.setTimeInMillis(Long.parseLong(csv[5]));
			Date end = c2.getTime();
			double walkTime = Double.parseDouble(csv[6]);
			int steps = Integer.parseInt(csv[7]);
			
			itineraries.add(new Itinerary(src, dst, start, end, walkTime, steps));
		}
		
		bw.close();
		
		return itineraries;
	}
}
