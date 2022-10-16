import { ParameterDescription } from 'api';
import { fieldFactory, FieldUtils } from './FieldFactory';

type Props = FieldUtils & {
    param: ParameterDescription;
};

export const DynamicField = ({ param, ...fieldUtils }: Props) => {
    const fieldProvider = fieldFactory()[param.type];

    if (!fieldProvider) {
        console.error(`Field type: "${param.type}" is not supported`);
        return <></>;
    }

    return fieldProvider(param, fieldUtils);
};
