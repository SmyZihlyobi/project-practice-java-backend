package xyz.demorgan.projectpractice.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getGroupId());
            row.createCell(2).setCellValue(student.getYear());
            row.createCell(3).setCellValue(student.getLastName());
            row.createCell(4).setCellValue(student.getFirstName());
            row.createCell(5).setCellValue(student.getPatronymic() != null ? student.getPatronymic() : "");
            row.createCell(6).setCellValue(student.getFirstPriority());
            row.createCell(7).setCellValue(student.getSecondPriority());
            row.createCell(8).setCellValue(student.getThirdPriority());
            row.createCell(9).setCellValue(student.getOtherPriorities() != null ? student.getOtherPriorities() : "");
            row.createCell(10).setCellValue(student.getTelegram());
            row.createCell(11).setCellValue(student.getResumePdf() != null ? student.getResumePdf() : "");
            row.createCell(12).setCellValue(student.getResumeLink() != null ? student.getResumeLink() : "");
            row.createCell(13).setCellValue(student.getCreatedAt().toString());
            row.createCell(14).setCellValue(student.getTeam() != null ? student.getTeam().getName() : "");
        }

        return workbook;
    }
}
