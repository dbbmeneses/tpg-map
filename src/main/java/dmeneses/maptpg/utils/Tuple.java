package dmeneses.maptpg.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class Tuple<T, U>  {
	T x;
	U y;

	public T getFirst() {
		return x;
	}

	public U getSecond() {
		return y;
	}
	
	public void setFirst(T t) {
		this.x = t;
	}
	
	public void setSecond(U u) {
		this.y = u;
	}
	
	@Override 
	public String toString() {
		return x.toString() + " " + y.toString();
	}
}