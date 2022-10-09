import { useEffect, useRef, useState } from 'react';

export const useElementYPosition = <T extends HTMLElement>() => {
    const ref = useRef<T>(null);
    const [yPos, setYPos] = useState(0);
    useEffect(() => {
        const rect = ref.current?.getBoundingClientRect();
        if (rect) setYPos(rect.y);
    }, [ref]);

    return { ref, yPos };
};
