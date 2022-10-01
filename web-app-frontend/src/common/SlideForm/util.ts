/* eslint-disable @typescript-eslint/no-explicit-any */
import _ from 'lodash';

export type SlideProps = {
    nameBuilder: (slideIndependantName: string) => string;
};

export interface SlideValues1<T0> {
    '0': T0;
}

export interface SlideValues2<T0, T1> extends SlideValues1<T0> {
    '1': T1;
}

export const prependNamesWithSlideNum = (slideNum: string | number, object: any) =>
    _.mapKeys(object, (_, key) => `${slideNum}.${key}`);

export const mergeNestedKeyNames = (object: any) => {
    const result: any = {};
    const traverse = (curPath: string) => {
        const cur = _.get(object, curPath);
        if (typeof cur !== 'object') {
            result[curPath] = cur;
        } else {
            Object.keys(cur).forEach((key) => traverse(`${curPath}.${key}`));
        }
    };

    Object.keys(object).forEach((key) => traverse(key));
    return result;
};
