package xyz.demorgan.projectpractice.store.dto.input;

import lombok.Data;

@Data
public class ProjectInputDto {
    String name;
    String description;
    String url;
    int teamsAmount;
    String technicalSpecifications;
    Long presentation;
    boolean studentProject;
    int companyId;
}
