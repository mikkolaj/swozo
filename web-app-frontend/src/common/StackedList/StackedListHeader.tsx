import { Divider, Grid, SxProps, Theme } from '@mui/material';
import { stylesRowCenteredVertical } from 'common/styles';

type Props = {
    proportions: number[];
    items: JSX.Element[];
    itemWrapperSxProvider?: (idx: number) => SxProps<Theme> | undefined;
};

export const StackedListHeader = ({ proportions, items, itemWrapperSxProvider }: Props) => {
    return (
        <>
            <Grid container sx={{ mb: -2 }}>
                {proportions.map((proportion, idx) => (
                    <Grid
                        key={`${proportion}_${idx}`}
                        item
                        xs={proportion}
                        sx={itemWrapperSxProvider?.(idx) ?? stylesRowCenteredVertical}
                    >
                        {items[idx]}
                    </Grid>
                ))}
            </Grid>
            <Divider />
        </>
    );
};
