import { Paper } from '@mui/material';
import { red } from '@mui/material/colors';
import { useEffect, useState } from 'react';
import { useAppSelector } from 'services/store';

type Props = {
    autoCloseAfterMs?: number;
};

export const PopupError = ({ autoCloseAfterMs = 2000 }: Props) => {
    const err = useAppSelector((state) => state.error);
    const [isDisplayed, setIsDisplayed] = useState(err.shouldShow);

    useEffect(() => {
        setIsDisplayed(err.shouldShow);
    }, [err.shouldShow, err.counter]);

    useEffect(() => {
        if (isDisplayed && err.options.autoClose) {
            const timeout = setTimeout(() => setIsDisplayed(false), autoCloseAfterMs);
            return () => clearTimeout(timeout);
        }
    }, [isDisplayed, autoCloseAfterMs, err.options.autoClose]);

    if (!isDisplayed) return <></>;

    return (
        <Paper
            sx={{
                position: 'fixed',
                zIndex: 100000,
                px: 5,
                py: 3,
                minWidth: 200,
                transform: 'translateX(-50%)',
                left: '50%',
                bottom: '3%',
                boxShadow: 2,
                textAlign: 'center',
                color: red[500],
                fontWeight: '600',
            }}
        >
            {err.options.message}
        </Paper>
    );
};
