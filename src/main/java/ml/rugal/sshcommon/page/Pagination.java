package ml.rugal.sshcommon.page;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * Concrete page for pagination.
 *
 * @author Rugal Bernstein
 */
public class Pagination extends SimplePage implements Serializable, Paginable {

    public Pagination() {
    }

    public Pagination(int pageNo, int pageSize, int totalCount) {
        super(pageNo, pageSize, totalCount);
    }

    public Pagination(int pageNo, int pageSize, int totalCount, List<?> list) {
        super(pageNo, pageSize, totalCount);
        this.list = list;
    }

    /**
     * Get the index of first record of current page.
     *
     * @return
     */
    public int getFirstResult() {
        return (pageNo - 1) * pageSize;
    }

    @Expose
    private List<?> list;

    public List<?> getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
