package com.example.vto.dto;

import com.github.pagehelper.Page;
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
    private long totalSize;

    @ApiModelProperty(value = "分页数据")
    private List<?> list;

    @ApiModelProperty(value = "分页信息")
    private static Map<String, Object> pagesInfo = new HashMap<>();

    @ApiModelProperty(value = "分页结果集")
    private static Map<String, Object> resultMap = new HashMap<>();

    @ApiModelProperty(hidden = true)
    private static Page page;

    /**
     * 初始化pageHelper
     * 因为PageHelper.startPage()需要在查询操作之前，否则分页不生效
     * @param pageIndex
     * @param pageSize
     */
    public static void initPageHelper (int pageIndex, int pageSize) {
        page = PageHelper.startPage(pageIndex, pageSize);
    }


    public static PageDto pageList(List<?> list, String targetListName) {
        PageInfo pageInfo = new PageInfo<>(list);
        PageDto pageDto = new PageDto();
        resultMap = new HashMap<>();

        // 定义分页数据
        pageDto.setPageIndex(pageInfo.getPageNum());

        if (page != null) { // 进行分页，需要通过page获取总记录数
            pageDto.setTotalSize(page.getTotal());
            pageDto.setPageSize(page.getPageSize());
        }

        pageDto.setTotalSize(pageInfo.getTotal());
        pageDto.setTotalPages(pageInfo.getPages());
        pageDto.setList(pageInfo.getList());

        // 封装结果集
        pagesInfo.put("pageIndex", pageDto.getPageIndex());
        pagesInfo.put("pageSize", pageDto.getPageSize());
        pagesInfo.put("totalSize", pageDto.getTotalSize());
        pagesInfo.put("totalPages", pageDto.getTotalPages());

        resultMap.put(targetListName, list);
        resultMap.put("pageInfo", pagesInfo);

        return pageDto;
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

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
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
