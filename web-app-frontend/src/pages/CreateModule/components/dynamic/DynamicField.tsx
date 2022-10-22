import { ParameterDescription } from 'api';
import { useTranslation } from 'react-i18next';
import { inputFieldFactory } from './InputFieldFactory';
import { InputFieldUtils } from './utils';

type Props = InputFieldUtils & {
    param: ParameterDescription;
};

export const DynamicField = ({ param, ...fieldUtils }: Props) => {
    const { i18n } = useTranslation();
    const fieldProvider = inputFieldFactory()[param.type];

    if (!fieldProvider) {
        console.error(`Field type: "${param.type}" is not supported`);
        return <></>;
    }

    return fieldProvider(param, i18n, fieldUtils);
};
