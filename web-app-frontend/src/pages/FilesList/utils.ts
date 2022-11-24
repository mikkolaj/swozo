import { FavouriteFileDto } from 'api';
import dayjs from 'dayjs';
import _ from 'lodash';

export type SortKey = 'name' | 'courseName' | 'createdAt';
export type SortDirection = 'ASC' | 'DESC';

export const opposite = (direction: SortDirection): SortDirection => (direction === 'ASC' ? 'DESC' : 'ASC');

export const sorted = (files: FavouriteFileDto[] = [], _: SortKey): FavouriteFileDto[] => {
    const filesCpy = [...files];
    filesCpy.sort((f1, f2) => dayjs(f1.file.createdAt).diff(f2.file.createdAt));
    return filesCpy;
};

export const withSortDirection = (sortedFiles: FavouriteFileDto[], sortDirection: SortDirection) =>
    sortDirection === 'ASC' ? [...sortedFiles] : _.reverse([...sortedFiles]);
