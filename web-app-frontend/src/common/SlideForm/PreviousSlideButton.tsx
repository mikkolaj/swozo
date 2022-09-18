import { Button } from '@mui/material';

type Props = {
    currentSlide: number;
    goBack: (toSlide: number) => void;
    label: string;
};

export const PreviousSlideButton = ({ currentSlide, goBack, label }: Props) => {
    return <>{currentSlide > 0 && <Button onClick={() => goBack(currentSlide - 1)}>{label}</Button>}</>;
};
