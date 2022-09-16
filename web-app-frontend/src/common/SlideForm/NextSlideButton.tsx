import { Button } from '@mui/material';

type Props = {
    currentSlide: number;
    slideCount: number;
    label: string;
    lastSlideLabel?: string;
    goNext: () => void;
    finish: () => void;
};

export const NextSlideButton = ({
    currentSlide,
    slideCount,
    label,
    lastSlideLabel,
    goNext,
    finish,
}: Props) => {
    return (
        <Button
            onClick={() => {
                if (currentSlide === slideCount) {
                    finish();
                } else {
                    goNext();
                }
            }}
        >
            {currentSlide < slideCount - 1 || !lastSlideLabel ? label : lastSlideLabel}
        </Button>
    );
};
