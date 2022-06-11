// python semantics
export const range = (end: number, start?: number): number[] => {
    if (!start) start = 0;
    const arr = Array(end - start);
    for (let i = start; i < end; i++) arr[i - start] = i;
    return arr;
};
