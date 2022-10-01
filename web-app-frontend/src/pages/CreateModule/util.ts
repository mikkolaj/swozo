import { SlideValues2 } from 'common/SlideForm/util';

export const MODULE_INFO_SLIDE = '0';
export const MODULE_SPECS_SLIDE = '1';

export type ModuleValues = {
    name: string;
    subject: string;
    description: string;
    service: string;
    serviceFile: string;
    instructions: string;
    isPublic: boolean;
};

export type ModuleSpecs = {
    environment: string;
    storage: number;
    cpu: string;
    ram: string;
};

export type FormValues = SlideValues2<ModuleValues, ModuleSpecs>;

export const initialModuleValues = () => ({
    name: '',
    subject: '',
    description: '',
    service: 'Jupyter',
    serviceFile: '',
    instructions: '',
    isPublic: true,
});
