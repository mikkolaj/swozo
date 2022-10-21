package com.swozo.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceModuleInstructions {
    private String teacherInstructionHtml;
    private String connectionInstructionHtml;

}
