import { Grid, Paper, SxProps, Theme } from '@mui/material';
import { stylesRowCenteredVertical } from 'common/styles';
import { Key } from 'react';

type Props<T> = {
    proportions: number[];
    items: T[];
    itemKeyExtractor: (item: T) => Key;
    itemRenderer: (item: T) => JSX.Element[];
    itemWraperSxProvider?: (idx: number) => SxProps<Theme> | undefined;
    emptyItemsComponent?: JSX.Element;
};

export function StackedListContent<T>({
    proportions,
    items,
    itemKeyExtractor,
    itemRenderer,
    itemWraperSxProvider,
    emptyItemsComponent,
}: Props<T>) {
    const contentItems = items.map(itemRenderer);
    return (
        <>
            {items.length > 0 || !emptyItemsComponent
                ? items.map((item, itemIdx) => (
                      <Paper key={itemKeyExtractor(item)} sx={{ p: 1, boxShadow: 2 }}>
                          <Grid container>
                              {proportions.map((proportion, idx) => (
                                  <Grid
                                      key={`${proportion}_${idx}`}
                                      item
                                      xs={proportion}
                                      sx={itemWraperSxProvider?.(idx) ?? stylesRowCenteredVertical}
                                      //   sx={{
                                      //       overflowX: 'hidden',
                                      //       textOverflow: 'ellipsis',
                                      //       ...stylesRowCenteredVertical,
                                      //   }}
                                  >
                                      {contentItems[itemIdx][idx]}
                                  </Grid>
                              ))}
                          </Grid>
                      </Paper>
                  ))
                : emptyItemsComponent}
        </>
    );
}
