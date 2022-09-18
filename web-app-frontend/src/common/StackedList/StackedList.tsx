import { Stack } from '@mui/material';

type Props = {
    header?: JSX.Element;
    content: JSX.Element;
};

export const StackedList = ({ header, content }: Props) => {
    return (
        <Stack spacing={2} px={2}>
            {header}
            {content}
        </Stack>
    );
};
