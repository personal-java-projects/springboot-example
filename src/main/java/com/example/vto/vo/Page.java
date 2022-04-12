package com.example.vto.vo;

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
    private int pageSize;

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
}
