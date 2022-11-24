import { Box } from '@mui/material';
import { Form, Formik, FormikValues, useFormikContext } from 'formik';
import { PropsWithChildren, useEffect } from 'react';

type Props<T extends FormikValues> = {
    initialValues: T;
    onModificationApplied: (filters: T) => void;
};

export function ResourceFilters<T extends FormikValues>({
    initialValues,
    onModificationApplied,
    children,
}: PropsWithChildren<Props<T>>) {
    const ChangeObserver = () => {
        const { values } = useFormikContext<T>();
        useEffect(() => {
            onModificationApplied(values);
        }, [values]);
        return null;
    };

    return (
        <Box>
            <Formik<T> initialValues={initialValues} onSubmit={() => undefined}>
                {() => (
                    <Form>
                        <ChangeObserver />
                        {children}
                    </Form>
                )}
            </Formik>
        </Box>
    );
}
