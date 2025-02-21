package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import xyz.demorgan.projectpractice.store.entity.Student;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    Student toEntity(xyz.demorgan.projectpractice.store.dto.StudentDto studentDto);

    xyz.demorgan.projectpractice.store.dto.StudentDto toStudentDto(Student student);
}