package com.example.service.impl;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.*;
import com.example.service.EasyPoiService;
import com.example.util.HttpImgUtils;
import com.example.util.LocalJsonUtil;
import com.example.util.MemberExcelDataHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service("easyPoiService")
public class EasyPoiServiceImpl implements EasyPoiService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ModelMap exportMemberExcel(ModelMap modelMap) {
        List<Member> memberList = LocalJsonUtil.getListFromJson("json/members.json", Member.class);
        ExportParams params = new ExportParams("会员列表", "会员列表", ExcelType.XSSF);
        //对导出结果进行自定义处理
        MemberExcelDataHandler handler = new MemberExcelDataHandler();
        handler.setNeedHandlerFields(new String[]{"昵称"});
        params.setDataHandler(handler);
        modelMap.put(NormalExcelConstants.DATA_LIST, memberList);
        modelMap.put(NormalExcelConstants.CLASS, Member.class);
        modelMap.put(NormalExcelConstants.PARAMS, params);
        modelMap.put(NormalExcelConstants.FILE_NAME, "memberList");

        return modelMap;
    }

    @Override
    public List<Member> importMemberExcel(MultipartFile file) throws Exception {
        ImportParams importParams = new ImportParams();
        importParams.setTitleRows(1);
        importParams.setHeadRows(1);

        List<Member> list = ExcelImportUtil.importExcel(
                file.getInputStream(),
                Member.class, importParams);

        return list;
    }

    @Override
    public ModelMap exportOrderExcel(ModelMap modelMap) {
        List<Order> orderList = getOrderList();
        ExportParams exportParams = new ExportParams("订单列表", "订单列表", ExcelType.XSSF);
        //导出时排除一些字段
        exportParams.setExclusions(new String[]{"ID", "出生日期", "性别"});
        modelMap.put(NormalExcelConstants.DATA_LIST, orderList);
        modelMap.put(NormalExcelConstants.CLASS, Order.class);
        modelMap.put(NormalExcelConstants.PARAMS, exportParams);
        modelMap.put(NormalExcelConstants.FILE_NAME, "orderList");

        return modelMap;
    }

    @Override
    public List<User> exportUserExcel(ModelMap modelMap, List<Integer> ids) {
        List<User> userList = new ArrayList<>();

        if (ids.size() == 0) {
            userList = userMapper.selectUsersByUsername(null);
        }

        if (ids != null) {
            for (int id:ids) {
                userList.add(userMapper.selectUserById(id));
            }
        }

        for (User user: userList) {
            int id = user.getId();
            byte[] netImgByUrl = HttpImgUtils.getNetImgByUrl(user.getAvatarUrl());
            user.setAvatarImg(netImgByUrl);;
            Role role = roleMapper.selectRoleByUserId(id);
            user.setRole(role);
        }


        return userList;
    }

    private List<Order> getOrderList() {
        List<Order> orderList = LocalJsonUtil.getListFromJson("json/orders.json", Order.class);
        List<Product> productList = LocalJsonUtil.getListFromJson("json/products.json", Product.class);
        List<Member> memberList = LocalJsonUtil.getListFromJson("json/members.json", Member.class);
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            order.setMember(memberList.get(i));
            order.setProductList(productList);
        }
        return orderList;
    }
}
