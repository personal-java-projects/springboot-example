package com.example.dto;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@ApiModel("分页信息")
public class PageDto {
    @ApiModelProperty(value = "当前页号", example = "1")
    @Range(min = 1, max = Integer.MAX_VALUE)
    private int pageIndex;

    @ApiModelProperty(value = "每页记录数", example = "10")
    @Range(min = 1, max = Integer.MAX_VALUE)
    private int pageSize;

    @ApiModelProperty(value = "总页数", example = "10")
    private int totalPages;

    @ApiModelProperty(value = "总记录数", example = "100")
    private int totalSize;

    @ApiModelProperty(value = "排序", notes = "例：create_time desc,update_time desc")
    private String orderBy;

    @ApiModelProperty(value = "分页数据")
    private List<?> list;

    public PageDto pageList(List<?> list) {
        PageHelper.startPage(this.getPageIndex(), this.getPageSize());

        PageInfo pageInfo = new PageInfo<>(list);

        // 封装分页返回信息
        this.setPageIndex(pageInfo.getPageNum());
        this.setPageSize(this.getPageSize());
        this.setTotalSize(pageInfo.getSize());
        this.setTotalPages(pageInfo.getPages());
        this.setList(pageInfo.getList());

        return this;
    }


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

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
