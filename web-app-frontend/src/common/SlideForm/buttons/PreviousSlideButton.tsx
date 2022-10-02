import { Button } from '@mui/material';

type Props = {
    currentSlide: number;
    onBack: (toSlide: number) => void;
    label: string;
};

export const PreviousSlideButton = ({ currentSlide, onBack: goBack, label }: Props) => {
    return <>{currentSlide > 0 && <Button onClick={() => goBack(currentSlide - 1)}>{label}</Button>}</>;
};
