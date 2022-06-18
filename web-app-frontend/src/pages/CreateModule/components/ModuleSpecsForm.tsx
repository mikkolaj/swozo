import { MenuItem } from '@mui/material';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormSelectField } from 'common/SlideForm/SlideFormSelectField';
import { Form, Formik } from 'formik';

type ModuleSpecs = {
    environment: string;
    storage: number;
    cpu: string;
    ram: string;
};

export const ModuleSpecsForm = ({ formRef, initialValues, setValues }: SlideProps<ModuleSpecs>) => {
    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validateOnChange={false}
            onSubmit={setValues}
        >
            {() => (
                <Form>
                    {/* This should be dynamic, from server */}
                    <SlideFormSelectField
                        name="environment"
                        labelPath="createModule.slides.1.form.environment"
                    >
                        <MenuItem value={'isolated'}>Izolowane</MenuItem>
                    </SlideFormSelectField>
                    <SlideFormSelectField name="storage" labelPath="createModule.slides.1.form.storage">
                        <MenuItem value={1}>1GB</MenuItem>
                    </SlideFormSelectField>
                    <SlideFormSelectField name="cpu" labelPath="createModule.slides.1.form.cpu">
                        <MenuItem value={'big'}>Duże</MenuItem>
                    </SlideFormSelectField>
                    <SlideFormSelectField name="ram" labelPath="createModule.slides.1.form.ram">
                        <MenuItem value={'big'}>Duże</MenuItem>
                    </SlideFormSelectField>
                </Form>
            )}
        </Formik>
    );
};
