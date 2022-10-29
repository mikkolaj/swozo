package com.swozo.mapper;

import com.swozo.model.utils.InstructionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CommonMappers {
    public InstructionDto instructionToDto(String html) {
        return new InstructionDto(html);
    }

    public String instructionToPersistence(InstructionDto instructionDto) {
        return instructionDto.untrustedPossiblyDangerousHtml();
    }
}
