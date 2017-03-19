package ml.rugal.sshcommon.page;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Rugal Bernstein
 */
public class SimplePage implements Paginable {

    public static final int DEFAULT_COUNT = 20;

    /**
     * check the page number and give it valid number.
     *
     * @param pageNo page number to be specified, start from
     * <p>
     * @return return 1 if page number is null or less than 1; Or just return the page number.
     */
    public static int cpn(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    public SimplePage() {
    }

    /**
     *
     * @param pageNo     Page number
     * @param pageSize   Size per page
     * @param totalCount number of total records.
     */
    public SimplePage(int pageNo, int pageSize, int totalCount) {
        setTotalCount(totalCount);
        setPageSize(pageSize);
        setPageNo(pageNo);
        adjustPageNo();
    }

    /**
     * Adjust page number if it is not accurate.
     */
    public void adjustPageNo() {
        if (pageNo == 1) {
            return;
        }
        int tp = getTotalPage();
        if (pageNo > tp) {
            pageNo = tp;
        }
    }

    @Override
    public int getPageNo() {
        return pageNo;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTotalPage() {
        int totalPage = totalCount / pageSize;
        return (totalPage == 0 || totalCount % pageSize != 0) ? totalPage + 1 : totalPage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isFirstPage() {
        return pageNo <= 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isLastPage() {
        return pageNo >= getTotalPage();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNextPage() {
        return isLastPage() ? pageNo : pageNo + 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getPrePage() {
        return isFirstPage() ? pageNo : pageNo - 1;
    }

    @Expose
    protected int totalCount = 0;

    @Expose
    protected int pageSize = 20;

    @Expose
    protected int pageNo = 1;

    /**
     * To set total count for a query.
     *
     * @param totalCount Number of total records
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount < 0 ? 0 : totalCount;
    }

    /**
     * set page size to query, start from 1, will use default page size if parameter less than 1..
     *
     * @param pageSize Size of page.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize < 1 ? DEFAULT_COUNT : pageSize;
    }

    /**
     *
     * Set page number for this query, if given parameter less than 1, adjust it to 1.
     *
     * @param pageNo Page number
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo < 1 ? 1 : pageNo;
    }
}
