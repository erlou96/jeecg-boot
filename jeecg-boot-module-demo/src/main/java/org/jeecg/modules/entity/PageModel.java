package org.jeecg.modules.entity;

import java.util.List;

/**
 *封装分页信息
 * @author HanZhe
 *
 */
public class PageModel<E> {
    //结果集
    private List<E> list;
    //查询记录数
    private int totalRecords;
    //每页多少条记录
    private int pageSize = 10;
    //第几页
    private int pageNo;

    public List<E> getList() {
        return list;
    }
    public void setList(List<E> list) {
        this.list = list;
    }
    public int getTotalRecords() {
        return totalRecords;
    }
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getPageNo() {
        return pageNo;
    }
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
    /**
     * 总页数
     * @return
     */
    public int getTotalPages(){
        return totalRecords%pageSize == 0?totalRecords/pageSize:totalRecords/pageSize+1;

    }
    /**
     * 取得首页
     * @return
     */
    public int getTopPageNo(){
        return 1;
    }
    /**
     * 上一页
     * @return
     */
    public int getPreviousPageNo(){
        if(pageNo<=1){
            return 1;
        }
        return pageNo-1;
    }
    /**
     * 下一页
     * @return
     */
    public int getNextPageNo() {
        if (pageNo >= getBottomPageNo()) {
            return getBottomPageNo();
        }
        return pageNo + 1;
    }
    /**
     * 取得尾页
     * @return
     */
    public int getBottomPageNo(){
        return getTotalPages();
    }

}

