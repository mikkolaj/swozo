// python range
export const range = (end: number, start?: number): number[] => {
    if (!start) start = 0;
    const arr = Array(end - start);
    for (let i = start; i < end; i++) arr[i - start] = i;
    return arr;
};

// python zip
export function zip<T, U>(it1: T[], it2: U[]): [T, U][] {
    const size = Math.min(it1.length, it2.length);
    const res = new Array(size);

    for (let i = 0; i < size; i++) {
        res[i] = [it1[i], it2[i]];
    }

    return res;
}

export const capitalized = (x: string) => x && x[0].toUpperCase() + x.slice(1);
