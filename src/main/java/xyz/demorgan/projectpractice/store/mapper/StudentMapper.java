package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import xyz.demorgan.projectpractice.store.dto.StudentDto;
import xyz.demorgan.projectpractice.store.dto.input.StudentInputDto;
import xyz.demorgan.projectpractice.store.entity.Student;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    Student toEntity(StudentDto studentDto);

   StudentDto toStudentDto(Student student);

   Student toEntity(StudentInputDto studentInputDto);
}