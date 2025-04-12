package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import xyz.demorgan.projectpractice.store.dto.FavoriteProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.FavoriteProjectInput;
import xyz.demorgan.projectpractice.store.entity.FavoriteProject;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FavoriteProjectMapper {
    FavoriteProject toEntity(FavoriteProjectDto favoriteProjectDto);

    FavoriteProjectDto toFavoriteProjectDto(FavoriteProject favoriteProject);

    FavoriteProject toEntity(FavoriteProjectInput input);
}