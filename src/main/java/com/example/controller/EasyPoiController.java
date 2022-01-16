package com.example.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.example.pojo.Member;
import com.example.pojo.User;
import com.example.service.EasyPoiService;
import com.example.util.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EasyPoi导入导出测试Controller
 * Created by macro on 2021/10/12.
 */
@Log4j2
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

    @ApiOperation(value = "导出用户列表Excel")
    @GetMapping("/exportUserExcel")
    // allowEmptyValue 允许swagger传空值，默认不允许，会传null
    public void exportUserList(@ApiIgnore ModelMap modelMap, HttpServletResponse response,@ApiParam(value = "一组用户id", allowEmptyValue = true) @RequestParam(required = false) String ids) {
        String[] stringIds = null;
        if (ids != "") {
            stringIds = ids.split(",");
        }

        List<Integer> userIds = new ArrayList<>();
        if (stringIds != null) {
            for (String userId:stringIds) {
                userIds.add(Integer.parseInt(userId));
            }
        }


        ExportParams exportParams = new ExportParams("用户列表", "用户列表", ExcelType.XSSF);
        List<User> userList = easyPoiService.exportUserExcel(modelMap, userIds);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, userList);
        ServletOutputStream outputStream = null;

        try {
            outputStream = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("userList", "UTF-8"));
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("导出图片失败");
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}