package org.distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.basex.client.api.BaseXClient;
import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.Value;
import org.basex.query.iter.Iter;

/**
 * This class is the main entry point for BaseX for distributed querying.
 * 
 * @author Lukas Lewandowski, University of Konstanz.
 */
public class Query {

    /** User name. */
    public static final String USER = "admin";
    /** Password. */
    public static final String PW = "admin";

    /**
     * Default constructor.
     */
    public Query() {
    }

    /**
     * This query method is the entry point for BaseX. It receives a query and some URLs to execute
     * distributed querying.
     * 
     * @param q
     *            The query which has to be distributed to other BaseX server.
     * @param urls
     *            URLs identifying the available BaseX servers.
     * @return Query results from all servers.
     */
    public Object[] query(final String q, final Value urls) {
        ArrayList<String> results = new ArrayList<String>();
        final Iter ir = urls.iter();
        try {
            List<Future<String>> stringResults = new ArrayList<Future<String>>();
            ExecutorService executor = Executors.newFixedThreadPool((int)urls.size());
            for (Item it; (it = ir.next()) != null;) {
                final Item fit = it;
                Callable<String> task = new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        String r = null;
                        try {
                            String url = fit.toJava().toString();
                            String[] hp = url.split(":");
                            BaseXClient bx = new BaseXClient(hp[0], Integer.valueOf(hp[1]), USER, PW);
                            org.basex.client.api.BaseXClient.Query qr = bx.query(q);
                            r = qr.execute();
                            qr.close();
                            bx.close();
                        } catch (QueryException exc) {
                            exc.printStackTrace();
                        } catch (NumberFormatException exc) {
                            exc.printStackTrace();
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                        return r;

                    }
                };
                stringResults.add(executor.submit(task));
            }
            executor.shutdown();
            while(!executor.isTerminated())
                ;
            for (Future<String> future : stringResults) {
                try {
                    if (future.get() != null)
                        results.add(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } catch (QueryException exc) {
            exc.printStackTrace();
        }

        return results.toArray();
    }
}
