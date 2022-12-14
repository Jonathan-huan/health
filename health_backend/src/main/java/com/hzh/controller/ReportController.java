package com.hzh.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hzh.constant.MessageConstant;
import com.hzh.entity.Result;
import com.hzh.service.MemberService;
import com.hzh.service.ReportService;
import com.hzh.service.SetmealService;
import com.sun.jersey.core.header.InBoundHeaders;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);

        List<String> list=new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH,1);
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));
        }
        Map<String,Object> map=new HashMap<>();
        map.put("months",list);

        try{
            List<Integer> memberCount = memberService.findMemberCountByMonth(list);
            map.put("memberCount",memberCount);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        List<Map<String,Object>> list=setmealService.findSetmealCount();
        Map<String,Object> map=new HashMap<>();
        map.put("setmealCount",list);

        List<String> setmealNames=new ArrayList<>();
        for (Map<String, Object> m : list) {
            String name=(String) m.get("name");
            setmealNames.add(name);
        }
        map.put("setmealNames",setmealNames);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
    }
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try{
            Map<String,Object> result =reportService.getBusinessReport();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,result);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try{
            //??????????????????????????????????????????
            Map<String,Object> result = reportService.getBusinessReport();

            //??????????????????????????????????????????????????????Excel?????????
            String reportDate=(String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");


            //??????Excel????????????????????????
            String temlateRealPath=request.getSession().getServletContext().getRealPath("template")+
                    File.separator+"report_template.xlsx";

            //????????????????????????Excel????????????
            XSSFWorkbook workBook=new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));
            XSSFSheet sheet = workBook.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//???????????????????????????
            row.getCell(7).setCellValue(totalMember);//????????????

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//?????????????????????
            row.getCell(7).setCellValue(thisMonthNewMember);//?????????????????????

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//???????????????
            row.getCell(7).setCellValue(todayVisitsNumber);//???????????????

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//???????????????
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//???????????????

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//???????????????
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//???????????????

            int rowNum = 12;
            for(Map map : hotSetmeal){//????????????
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//????????????
                row.getCell(5).setCellValue(setmeal_count);//????????????
                row.getCell(6).setCellValue(proportion.doubleValue());//??????
            }
            //?????????????????????????????????
            ServletOutputStream out=response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition","attachment;filename=report.xlsx");
            workBook.write(out);


            out.flush();
            out.close();
            workBook.close();

            return null;
        }catch (Exception e){
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL,null);
        }
    }

    @RequestMapping("/exportBusinessReport4PDF")
    public Result exportBusinessReport4PDF(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String, Object> result = reportService.getBusinessReport();

            //?????????????????????????????????????????????????????????PDF?????????
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //??????????????????????????????????????????
            String jrxmlPath =
                    request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath =
                    request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";
            //????????????
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            //????????????---??????JavaBean?????????????????????
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperPath,result,
                            new JRBeanCollectionDataSource(hotSetmeal));

            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");

            //????????????
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
}
