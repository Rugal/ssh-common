package ml.rugal.sshcommon.page;

/**
 * Interface of object that is paginable.
 *
 * @author Rugal Bernstein
 */
public interface Paginable
{

    public int getTotalCount();

    /**
     * Get the total page number of a query. to see total row number divided by page size per page.
     *
     * @return get total page a query could have.
     */
    public int getTotalPage();

    public int getPageSize();

    public int getPageNo();

    /**
     * See if this is the first(1) page.
     *
     * @return true if current page if the first one.
     */
    public boolean isFirstPage();

    /**
     * To see if current page number is equal or greater than last page number.
     *
     * @return true if current page is equal or greater than last page number.
     */
    public boolean isLastPage();

    /**
     * Get next page number, just return current one if current page is the last one.
     *
     * @return page number calculated
     */
    public int getNextPage();

    /**
     *
     * Get previous page number, if current page is the first one just return current one.
     *
     * @return page number calculated
     */
    public int getPrePage();
}
