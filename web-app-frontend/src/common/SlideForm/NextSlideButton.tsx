import { Button } from '@mui/material';
import { FormikProps } from 'formik';
import { mergeNestedKeyNames, prependNamesWithSlideNum } from './util';

type Props = {
    currentSlide: number;
    slideCount: number;
    label: string;
    lastSlideLabel?: string;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    slideValidator?: FormikProps<any>;
    onNext: (toSlide: number) => void;
    onFinish: () => void;
};

export const NextSlideButton = ({
    currentSlide,
    slideCount,
    label,
    lastSlideLabel,
    slideValidator,
    onNext,
    onFinish,
}: Props) => {
    const isLastSlide = currentSlide === slideCount - 1;

    return (
        <Button
            onClick={async () => {
                if (isLastSlide) {
                    onFinish();
                } else {
                    if (slideValidator) {
                        const validationErrors = await slideValidator
                            .validateForm()
                            .then((errors) => errors[currentSlide]);

                        if (!validationErrors) {
                            onNext(currentSlide + 1);
                        } else {
                            console.log(prependNamesWithSlideNum(currentSlide, validationErrors));
                            console.log(mergeNestedKeyNames(validationErrors));
                            Object.keys(
                                prependNamesWithSlideNum(currentSlide, mergeNestedKeyNames(validationErrors))
                            ).forEach((field) => slideValidator.setFieldTouched(field));
                        }
                    } else {
                        onNext(currentSlide + 1);
                    }
                }
            }}
        >
            {!isLastSlide || !lastSlideLabel ? label : lastSlideLabel}
        </Button>
    );
};
