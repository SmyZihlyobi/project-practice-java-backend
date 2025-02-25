package xyz.demorgan.projectpractice.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;

import java.io.IOException;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ExcelService {

    StudentRepository studentRepository;

    public Workbook exportStudents() throws IOException {
        List<Student> students = studentRepository.findAllByOrderByTeamNameAsc();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        String[] columns = {
                "id",  "group_id", "year", "last_name", "first_name",
                "patronymic", "first_priority", "second_priority", "third_priority",
                "other_priorities", "telegram", "resume_pdf", "resume_link",
                "created_at", "team_name"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        CellStyle style1 = workbook.createCellStyle();
        style1.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNum = 1;
        String previousTeam = "";
        boolean useStyle1 = true;

        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);

            if (!student.getTeam().getName().equals(previousTeam)) {
                useStyle1 = !useStyle1;
                previousTeam = student.getTeam().getName();
            }

            CellStyle currentStyle = useStyle1 ? style1 : style2;

            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(currentStyle);
            }

            row.getCell(0).setCellValue(student.getId());
            row.getCell(1).setCellValue(student.getGroupId());
            row.getCell(2).setCellValue(student.getYear());
            row.getCell(3).setCellValue(student.getLastName());
            row.getCell(4).setCellValue(student.getFirstName());
            row.getCell(5).setCellValue(student.getPatronymic() != null ? student.getPatronymic() : "");
            row.getCell(6).setCellValue(student.getFirstPriority());
            row.getCell(7).setCellValue(student.getSecondPriority());
            row.getCell(8).setCellValue(student.getThirdPriority());
            row.getCell(9).setCellValue(student.getOtherPriorities() != null ? student.getOtherPriorities() : "");
            row.getCell(10).setCellValue(student.getTelegram());
            row.getCell(11).setCellValue(student.getResumePdf() != null ? student.getResumePdf() : "");
            row.getCell(12).setCellValue(student.getResumeLink() != null ? student.getResumeLink() : "");
            row.getCell(13).setCellValue(student.getCreatedAt().toString());
            row.getCell(14).setCellValue(student.getTeam() != null ? student.getTeam().getName() : "");
        }

        return workbook;
    }
}
