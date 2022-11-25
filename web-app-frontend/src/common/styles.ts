import { SxProps, Theme } from '@mui/material';

type MaterialStyle = SxProps<Theme>;

export const FORM_INPUT_WIDTH_NUM = 230;
export const FORM_INPUT_WIDTH = `${FORM_INPUT_WIDTH_NUM}px`;

export const stylesRow: MaterialStyle = {
    display: 'flex',
    flexDirection: 'row',
};

export const stylesRowCenteredVertical: MaterialStyle = {
    ...stylesRow,
    alignItems: 'center',
};

export const stylesRowCenteredHorizontal: MaterialStyle = {
    ...stylesRow,
    justifyItems: 'center',
};

export const stylesRowFullyCentered: MaterialStyle = {
    ...stylesRowCenteredHorizontal,
    ...stylesRowCenteredVertical,
};

export const stylesRowWithItemsAtTheEnd: MaterialStyle = {
    ...stylesRow,
    justifyContent: 'flex-end',
};

export const stylesRowWithSpaceBetweenItems: MaterialStyle = {
    ...stylesRow,
    justifyContent: 'space-between',
};

export const stylesColumn: MaterialStyle = {
    display: 'flex',
    flexDirection: 'column',
};

export const stylesColumnCenteredVertical: MaterialStyle = {
    ...stylesColumn,
    justifyItems: 'center',
};

export const stylesColumnCenteredHorizontal: MaterialStyle = {
    ...stylesColumn,
    alignItems: 'center',
};

export const stylesColumnFullyCentered: MaterialStyle = {
    ...stylesColumnCenteredVertical,
    ...stylesColumnCenteredHorizontal,
};
