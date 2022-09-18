import _ from 'lodash';
import { FileSummary } from 'utils/mocks';

export type SortKey = 'name' | 'courseName' | 'createdAt';
export type SortDirection = 'ASC' | 'DESC';

export const sorted = (files: FileSummary[], _: SortKey): FileSummary[] => {
    // TODO: proper sorting
    return files.sort((f1, f2) => f1.createdAt.diff(f2.createdAt));
};

export const withSortDirection = (sortedFiles: FileSummary[], sortDirection: SortDirection) =>
    sortDirection === 'ASC' ? [...sortedFiles] : _.reverse([...sortedFiles]);
