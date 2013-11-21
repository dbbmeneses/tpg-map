package dmeneses.maptpg.utils;

public class Tuple<T, U>  {
	T x;
	U y;

	public Tuple(T x, U y) {
		this.x = x;
		this.y = y;
	}

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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<?, ?> other = (Tuple<?, ?>) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
	
}