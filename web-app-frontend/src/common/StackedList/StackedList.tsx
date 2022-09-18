import { Divider, Grid, Paper, Stack, SxProps } from '@mui/material';
import { Theme } from '@mui/system';
import { stylesRowCenteredVertical } from 'common/styles';
import { Key } from 'react';

type Props<T> = {
    proportions: number[];
    items: T[];
    itemKeyExtractor: (item: T) => Key;
    headerItemRenderer?: (idx: number) => JSX.Element;
    headerItemWraperSxProvider?: (idx: number) => SxProps<Theme> | undefined;
    contentItemRenderer: (idx: number, item: T) => JSX.Element;
    contentItemWraperSxProvider?: (idx: number) => SxProps<Theme> | undefined;
};

export function StackedList<T>({
    proportions,
    items,
    itemKeyExtractor,
    headerItemRenderer,
    headerItemWraperSxProvider,
    contentItemRenderer,
    contentItemWraperSxProvider,
}: Props<T>) {
    return (
        <Stack spacing={2} px={2}>
            {headerItemRenderer && (
                <>
                    <Grid container sx={{ mb: -2 }}>
                        {proportions.map((proportion, idx) => (
                            <Grid
                                key={`${proportion}_${idx}`}
                                item
                                xs={proportion}
                                sx={headerItemWraperSxProvider?.(idx) ?? stylesRowCenteredVertical}
                            >
                                {headerItemRenderer(idx)}
                            </Grid>
                        ))}
                    </Grid>
                    <Divider />
                </>
            )}
            {items.map((item) => (
                <Paper key={itemKeyExtractor(item)} sx={{ p: 1, boxShadow: 2 }}>
                    <Grid container>
                        {proportions.map((proportion, idx) => (
                            <Grid
                                key={`${proportion}_${idx}`}
                                item
                                xs={proportion}
                                sx={contentItemWraperSxProvider?.(idx) ?? stylesRowCenteredVertical}
                            >
                                {contentItemRenderer(idx, item)}
                            </Grid>
                        ))}
                    </Grid>
                </Paper>
            ))}
        </Stack>
    );
}
