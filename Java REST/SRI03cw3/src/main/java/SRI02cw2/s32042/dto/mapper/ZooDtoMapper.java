package SRI02cw2.s32042.dto.mapper;

import SRI02cw2.s32042.dto.ZooDetailsDto;
import SRI02cw2.s32042.dto.ZooDto;
import SRI02cw2.s32042.model.Zoo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZooDtoMapper {
    private final ModelMapper modelMapper;

    public ZooDetailsDto convertToDtoDetails(Zoo zoo) {
        return modelMapper.map(zoo, ZooDetailsDto.class);
    }

    public ZooDto convertToDto(Zoo zoo) {
        return modelMapper.map(zoo, ZooDto.class);
    }

    private Zoo convertToEntity(ZooDto dto) {
        return modelMapper.map(dto, Zoo.class);
    }
}
