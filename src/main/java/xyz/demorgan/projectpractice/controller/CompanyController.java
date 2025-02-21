package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.CompanyService;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    @QueryMapping
    public List<CompanyDto> companies() {
        return companyService.getAll();
    }

    @QueryMapping
    public CompanyDto company(@Argument Long id) {
        return companyService.getById(id);
    }
}
