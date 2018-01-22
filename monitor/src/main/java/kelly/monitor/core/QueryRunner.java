package kelly.monitor.core;

import com.stumbleupon.async.Deferred;
import org.hbase.async.HBaseException;

import java.util.List;

public interface QueryRunner {

    /**
     * Runs this query.
     * 
     * @return The data points matched by this query.
     * <p>
     * Each element in the non-{@code null} but possibly empty array returned
     * corresponds to one time series for which some data points have been
     * matched by the query.
     * @throws HBaseException if there was a problem communicating with HBase to
     * perform the search.
     */
    List<DataPoints> run(Query query) throws Exception;

    Deferred<Object> shutdown();
}
