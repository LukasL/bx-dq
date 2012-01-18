package org.distribution;

import java.util.ArrayList;

import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.Value;
import org.basex.query.iter.Iter;

public class Query {

	/**
	 * ...
	 * 
	 * @param urls
	 * @return
	 */
	public Object[] query(final String q, final Value urls) {
		ArrayList<String> results = new ArrayList<String>();
		final Iter ir = urls.iter();
		try {
			for (Item it; (it = ir.next()) != null;) {
				results.add(it.toJava().toString());
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * for (final Object u : urls) results.add(u.toString());
		 */
		return results.toArray();
	}

	/**
	 * ...
	 * 
	 * @param urls
	 * @return
	 */
	public Object[] q(final String s, final String s2) {
		return new String[] { s, s2 };
	}

	public Object qu(final Object s) {
		return "!";
	}
}
