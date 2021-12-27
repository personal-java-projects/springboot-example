package com.example.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.example.pojo.Member;
import com.example.util.LocalJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * EasyPoi导入导出测试Controller
 * Created by macro on 2021/10/12.
 */
@Controller
@Api(tags = "EasyPoiController", description = "EasyPoi导入导出测试")
@RequestMapping("/easyPoi")
public class EasyPoiController {

    @ApiOperation(value = "导出会员列表Excel")
    @RequestMapping(value = "/exportMemberList", method = RequestMethod.GET)
    public void exportMemberList(ModelMap map,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        List<Member> memberList = LocalJsonUtil.getListFromJson("json/members.json", Member.class);
        ExportParams params = new ExportParams("会员列表", "会员列表", ExcelType.XSSF);
        map.put(NormalExcelConstants.DATA_LIST, memberList);
        map.put(NormalExcelConstants.CLASS, Member.class);
        map.put(NormalExcelConstants.PARAMS, params);
        map.put(NormalExcelConstants.FILE_NAME, "memberList");
        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
    }
}