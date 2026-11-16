package queries;

import java.util.List;

public interface QuerySolver {
    public String respondeQuery(List<Object> extra_info);
    public void  listener(Object o);
    public void call(QueryManager q);
    public QuerySolver clone();
    
}