package dmeneses.maptpg.model;

import java.util.List;
import lombok.Data;

@Data
public class Line {
		private String code;
		private String destinationName;
		private String destinationCode;
		private List<PhysicalStop> stops;

		public Line() {}

		public Line(String code, String destinationCode, List<PhysicalStop> stops) {
			this.code = code;
			this.destinationCode = destinationCode;
			this.stops = stops;
		}

		@Override
		public String toString() {
			return code + " (" + destinationName + ")";
		}
}
