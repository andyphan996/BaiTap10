package vn.iotstar.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "username", source = "user.username")
	@Mapping(target = "image", ignore = true)
	ProductDTO toDTO(Product entity);

	// id và imageUrl do server quyết định, không lấy từ form
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "imageUrl", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Product toEntity(ProductDTO dto);
}
