package com.example.service;

import com.example.pojo.Member;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EasyPoiService {

    /**
     * 导出会员excel数据
     * @param modelMap
     * @return
     */
    ModelMap exportMemberExcel(ModelMap modelMap);

    /**
     * 导入会员excel数据
     * @param file
     * @return
     */
    List<Member> importMemberExcel(MultipartFile file) throws Exception;

    /**
     * 导出订单excel数据
     * @param modelMap
     * @return
     */
    ModelMap exportOrderExcel(ModelMap modelMap);
}
