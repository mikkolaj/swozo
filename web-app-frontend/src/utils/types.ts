import * as Yup from 'yup';

export type RedirectState = {
    redirectTo: string;
};

export type ValidationSchema<T> = Partial<Record<keyof T, Yup.AnySchema>>;
