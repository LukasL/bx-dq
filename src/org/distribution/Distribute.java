package org.distribution;

import java.util.ArrayList;

public class Distribute {

	/**
	   * ... 
	   * @param urls
	   * @return
	   */
	  public Object[] distribute(Object[] urls) {
	    ArrayList<String> results = new ArrayList<String>();
	    for(final Object u : urls) results.add(u.toString());
	    return results.toArray();
	  }
	  
	  public static void main(String[] args) {
	    Distribute d = new Distribute();
	    Object[] urls = { "abc", "def" };
	    for(final Object o : d.distribute(urls)) {
	      System.out.println(o);
	    }
	  }
	
}
