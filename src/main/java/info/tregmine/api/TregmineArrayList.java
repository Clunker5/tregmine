package info.tregmine.api;

import java.util.ArrayList;

/**
 * Created by ericrabil on 3/25/17.
 */
public class TregmineArrayList<T> extends ArrayList<T> {

    @SafeVarargs
    public final void addMultiple(T... values) {
        for (T entity : values) {
            this.add(entity);
        }
    }
}
