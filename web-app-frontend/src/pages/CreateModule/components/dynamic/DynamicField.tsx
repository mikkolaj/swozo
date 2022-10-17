import { ParameterDescription } from 'api';
import { useTranslation } from 'react-i18next';
import { fieldFactory } from './FieldFactory';
import { FieldUtils } from './utils';

type Props = FieldUtils & {
    param: ParameterDescription;
};

export const DynamicField = ({ param, ...fieldUtils }: Props) => {
    const { i18n } = useTranslation();
    const fieldProvider = fieldFactory()[param.type];

    if (!fieldProvider) {
        console.error(`Field type: "${param.type}" is not supported`);
        return <></>;
    }

    return fieldProvider(param, i18n, fieldUtils);
};
