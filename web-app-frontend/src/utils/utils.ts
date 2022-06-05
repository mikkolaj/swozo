export const range = (end: number): number[] => {
    const arr = Array(end);
    for (let i = 0; i < end; i++) arr[i] = i;
    return arr;
};
