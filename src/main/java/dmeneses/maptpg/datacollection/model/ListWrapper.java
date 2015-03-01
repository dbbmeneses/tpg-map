package dmeneses.maptpg.datacollection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

public class ListWrapper<T> implements Iterable<T> {
    private List<T> items;
    
    public ListWrapper() {
        items = new ArrayList<T>();
    }
    public ListWrapper(List<T> items) {
        this.items = new ArrayList<T>(items); //make sure it's an arraylist 
    }
 
    @XmlAnyElement(lax=true)
    public List<T> getItems() {
        return items;
    }
    
    public void setItems(List<T> items) {
    	this.items = items;
    }
    
	@Override
	public Iterator<T> iterator() {
		return items.iterator();
	}
}
