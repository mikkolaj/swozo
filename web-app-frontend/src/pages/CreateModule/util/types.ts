import { ParameterDescriptionTypeEnum } from 'api';
import { SlideValues2 } from 'common/SlideForm/util';

export const MODULE_INFO_SLIDE = '0';
export const MODULE_SPECS_SLIDE = '1';

export type DynamicFormFields = Record<string, unknown>;
export type DynamicFormResolvedFieldActions = Record<string, string>;
export type DynamicFormValueRegistry = Record<
    string,
    {
        type: ParameterDescriptionTypeEnum;
        fieldValue: unknown;
        associatedValue?: unknown;
    }
>;

export type ModuleValues = {
    name: string;
    subject: string;
    description: string;
    service: string;
    teacherInstruction: string;
    studentInstruction: string;
    isPublic: boolean;
};

export type ModuleSpecs = {
    environment: string;
    storage: number;
    cpu: string;
    ram: string;
};

export type FormValues = SlideValues2<ModuleValues, ModuleSpecs>;
