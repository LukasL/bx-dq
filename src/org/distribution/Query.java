package org.distribution;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.basex.client.api.BaseXClient;
import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.Str;
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
    /** Successful execution. */
    public static final String OK = "Execution successful";

    /** Info messages. */
    private Map<String, String> infos;

    /**
     * Default constructor.
     */
    public Query() {
        infos = new HashMap<String, String>();
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
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws QueryException
     */
    public Item[] query(final Str q, final Value urls) throws InterruptedException, ExecutionException,
        QueryException {
        ArrayList<Item> results = new ArrayList<Item>();
        final Iter ir = urls.iter();
        try {
            List<Future<Str>> sr = new ArrayList<Future<Str>>();
            ExecutorService executor = Executors.newFixedThreadPool((int)urls.size());
            for (Item it; (it = ir.next()) != null;) {
                final Item fit = it;
                Callable<Str> task = new Callable<Str>() {

                    @Override
                    public Str call() throws Exception {
                        Str r = null;
                        try {
                            String url = fit.toJava().toString();
                            String[] hp = url.split(":");
                            BaseXClient bx = new BaseXClient(hp[0], Integer.valueOf(hp[1]), USER, PW);
                            r = Str.get(bx.execute("xquery " + q.toJava().toString()).getBytes());
                            bx.close();
                            infos.put(url, OK);
                        } catch (QueryException exc) {
                            exc.printStackTrace();
                            throw exc;
                        } catch (NumberFormatException exc) {
                            exc.printStackTrace();
                            throw exc;
                        } catch (SocketException exc) {
                            String url = fit.toJava().toString();
                            infos.put(url, exc.getMessage());
                        } catch (IOException exc) {
                            exc.printStackTrace();
                            if (!(exc instanceof SocketException))
                                throw exc;
                        }
                        return r;
                    }
                };
                sr.add(executor.submit(task));
            }
            executor.shutdown();
            while(!executor.isTerminated())
                ;
            for (Future<Str> future : sr) {
                try {
                    if (future.get() != null)
                        results.add(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    throw e;
                }
            }

        } catch (QueryException exc) {
            exc.printStackTrace();
            throw exc;
        }
        Item[] itemResults = new Item[results.size()];
        return results.toArray(itemResults);
    }

    /**
     * Returns information to the parallel query execution and returns it to BaseX.
     * 
     * @return Query execution information.
     */
    public Str[] getInfo() {
        Str[] info = new Str[infos.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : infos.entrySet())
            info[i++] = Str.get((entry.getKey() + " returned: " + entry.getValue()).getBytes());
        return info;
    }
}
