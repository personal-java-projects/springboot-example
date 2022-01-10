package com.example.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.example.pojo.Member;
import com.example.pojo.Order;
import com.example.pojo.Product;
import com.example.service.EasyPoiService;
import com.example.util.LocalJsonUtil;
import com.example.util.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private EasyPoiService easyPoiService;

    @ApiOperation(value = "导出会员列表Excel")
    @RequestMapping(value = "/exportMemberList", method = RequestMethod.GET)
    public void exportMemberList(ModelMap modelMap,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        modelMap = easyPoiService.exportMemberExcel(modelMap);

        PoiBaseView.render(modelMap, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
    }

    /**
     * 这里需要注意的是使用@RequestPart注解修饰文件上传参数，
     * 否则在Swagger中就没法显示上传按钮了；
     * @param file
     * @return
     */
    @ApiOperation("从Excel导入会员列表")
    @RequestMapping(value = "/importMemberList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult importMemberList(@RequestPart("file") MultipartFile file) throws Exception {
        List<Member> memberList = easyPoiService.importMemberExcel(file);

        return ResponseResult.ok().data(memberList);
    }

    @ApiOperation(value = "导出订单列表Excel")
    @RequestMapping(value = "/exportOrderList", method = RequestMethod.GET)
    public void exportOrderList(ModelMap modelMap,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        modelMap = easyPoiService.exportOrderExcel(modelMap);
        PoiBaseView.render(modelMap, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
    }
}