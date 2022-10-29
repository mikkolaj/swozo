import { DynamicFieldDto } from 'api';
import { useTranslation } from 'react-i18next';
import { DisplayFieldUtils } from '../utils';
import { displayFieldFactory } from './DisplayFieldFactory';

type Props = DisplayFieldUtils & {
    field: DynamicFieldDto;
};

export const DynamicReadonlyField = ({ field, ...utils }: Props) => {
    const { i18n } = useTranslation();
    const fieldProvider = displayFieldFactory()[field.parameterDescription.type];

    if (!fieldProvider) {
        console.error(`Field type: "${field.parameterDescription.type}" is not supported`);
        return <></>;
    }

    return fieldProvider(field, i18n, utils);
};
