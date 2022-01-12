package com.example.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

@ApiModel(value = "分页请求")
public class Page {

    @ApiModelProperty(value = "当前页", example = "1")
    @Range(min = 1, max = Integer.MAX_VALUE)
    private int pageIndex = 1;

    @ApiModelProperty(value = "当前页记录数", example = "10")
    @Range(min = 1, max = Integer.MAX_VALUE)
    private int pageSize = 10;

    @ApiModelProperty(value = "排序方式", example = "create_time desc,update_time desc")
    @Range(min = 1, max = Integer.MAX_VALUE)
    private String orderBy = "createTime desc";

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
