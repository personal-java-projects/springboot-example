package com.example.dto;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ApiModelProperty(value = "分页数据")
    private List<?> list;

    @ApiModelProperty(value = "分页信息")
    private Map<String, Object> pagesInfo = new HashMap<>();

    @ApiModelProperty(value = "分页结果集")
    private Map<String, Object> resultMap = new HashMap<>();

    public PageDto pageList(List<?> list, String targetListName) {
        PageHelper.startPage(this.getPageIndex(), this.getPageSize());

        PageInfo pageInfo = new PageInfo<>(list);

        // 定义分页数据
        this.setPageIndex(pageInfo.getPageNum());
        this.setPageSize(this.getPageSize());
        this.setTotalSize(pageInfo.getSize());
        this.setTotalPages(pageInfo.getPages());
        this.setList(pageInfo.getList());

        // 封装结果集
        pagesInfo.put("pageIndex", pageIndex);
        pagesInfo.put("pageSize", pageSize);
        pagesInfo.put("totalSize", totalSize);
        pagesInfo.put("totalPages", totalPages);

        resultMap.put(targetListName, list);
        resultMap.put("pageInfo", pagesInfo);

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

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public Map<String, Object> getPagesInfo() {
        return pagesInfo;
    }

    public void setPagesInfo(Map<String, Object> pagesInfo) {
        this.pagesInfo = pagesInfo;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
