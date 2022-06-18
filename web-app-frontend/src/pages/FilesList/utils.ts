import { FileSummary } from 'utils/mocks';

export type SortKey = 'name' | 'courseName' | 'createdAt';
export type SortDirection = 'increasing' | 'decreasing';

export const sorted = (
    files: FileSummary[],
    sortKey: SortKey,
    sortDirection: SortDirection
): FileSummary[] => {
    // TODO proper sorting
    return files.sort(
        (f1, f2) => (sortDirection === 'decreasing' ? -1 : 1) * f1.createdAt.diff(f2.createdAt)
    );
};
