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
    const isLastSlide = currentSlide === slideCount - 1;

    return (
        <Button
            onClick={() => {
                if (isLastSlide) {
                    finish();
                } else {
                    goNext();
                }
            }}
        >
            {!isLastSlide || !lastSlideLabel ? label : lastSlideLabel}
        </Button>
    );
};
