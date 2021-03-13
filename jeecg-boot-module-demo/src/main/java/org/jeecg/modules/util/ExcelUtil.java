package org.jeecg.modules.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelUtil {

    public static final String XLS = ".xls";
    public static final String XLSX = ".xlsx";

    public List<List<String>> getexcel(MultipartFile file) throws Exception{
        Workbook workbook =  buildWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> cellname = new ArrayList<String>();
        List<List<String>> quanbu = new ArrayList<>();
        for (Row row : sheet) {
            //获取excel表列数
            int ColumnNumber = row.getPhysicalNumberOfCells();
            List<String> cellstr = new ArrayList<String>();
            //首行（即表头）不读取
            if (row.getRowNum() == 0) {
                continue;
            }
            for (int i = 0; i < ColumnNumber; i++) {

                Cell cell = row.getCell(i);
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        cellstr.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        cellstr.add(String.format("%.2f", cell.getNumericCellValue()));
                        break;
//                    case NUMERIC:
//                        cellstr.add(String.valueOf(cell.getNumericCellValue()));
//                        break;
                    case BOOLEAN:
                        cellstr.add(cell.getBooleanCellValue() ? "true" : "false");
                        break;
                    case FORMULA:
                        try {
                            cellstr.add(cell.getStringCellValue());
                            break;
                        } catch (Exception e) {
                            cellstr.add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        }
                    default: cellstr.add("");
                    break;
                }
            }
            quanbu.add(cellstr);

        }
        return quanbu;
    }


    public Workbook buildWorkbook(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename.endsWith(XLS)) {
            return new HSSFWorkbook(file.getInputStream());
        } else if (filename.endsWith(XLSX)) {
            return new XSSFWorkbook(file.getInputStream());
        } else {
            throw new IOException("unknown file format: " + filename);
        }
    }

}
