package xyz.demorgan.projectpractice.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ExcelService {

    StudentRepository studentRepository;

    public Workbook exportStudents() {
        List<Student> students = studentRepository.findAllByOrderByTeamNameAsc();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Студенты");
        sheet.setDisplayGridlines(false);

        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] columns = {
                "№",
                "Фамилия",
                "Имя",
                "Отчество",
                "Группа",
                "Курс",
                "Первый приоритет",
                "Второй приоритет",
                "Третий приоритет",
                "Другие приоритеты",
                "Телеграм",
                "Резюме (PDF)",
                "Ссылка на резюме",
                "Команда"
        };

        createHeaderRow(sheet, headerStyle, columns);

        CellStyle[] rowStyles = createRowStyles(workbook);

        int rowNum = 1;
        String previousTeam = "";
        int styleIndex = 0;

        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);

            if (!student.getTeamName().equals(previousTeam)) {
                styleIndex = (styleIndex + 1) % 2;
                previousTeam = student.getTeamName();
            }

            fillStudentRow(student, row, rowStyles[styleIndex]);
        }

        int[] widths = {
                1500,  // Счётчик
                4000,  // Фамилия
                4000,  // Имя
                4000,  // Отчество
                2500,  // Группа
                2000,  // Курс
                6000,  // Первый приоритет
                6000,  // Второй приоритет
                6000,  // Третий приоритет
                6000,  // Другие приоритеты
                4500,  // Телеграм
                5000,  // Резюме (PDF)
                8000,  // Ссылка на резюме
                4000   // Команда
        };

        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i]);
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, columns.length - 1));

        return workbook;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle[] createRowStyles(Workbook workbook) {
        CellStyle style1 = workbook.createCellStyle();
        style1.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return new CellStyle[]{style1, style2};
    }

    private void createHeaderRow(Sheet sheet, CellStyle style, String[] columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(style);
        }
    }

    private void fillStudentRow(Student student, Row row, CellStyle style) {
        int colNum = 0;

        // Счётчик
        createCell(row, colNum++, row.getRowNum(), style);

        // Основная информация
        createCell(row, colNum++, student.getLastName(), style);
        createCell(row, colNum++, student.getFirstName(), style);
        createCell(row, colNum++, student.getPatronymic(), style);

        // Учебные данные
        createCell(row, colNum++, student.getGroupId(), style);
        createCell(row, colNum++, student.getYear(), style);

        // Приоритеты
        createCell(row, colNum++, student.getFirstPriority(), style);
        createCell(row, colNum++, student.getSecondPriority(), style);
        createCell(row, colNum++, student.getThirdPriority(), style);
        createCell(row, colNum++, student.getOtherPriorities(), style);

        // Контакты
        createCell(row, colNum++, student.getTelegram(), style);

        // Резюме
        createCell(row, colNum++, student.getResumePdf(), style);
        createCell(row, colNum++, student.getResumeLink(), style);

        // Команда
        createCell(row, colNum, student.getTeam() != null ? student.getTeam().getName() : "", style);
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
