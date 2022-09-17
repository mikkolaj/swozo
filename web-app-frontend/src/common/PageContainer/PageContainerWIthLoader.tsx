import { Box, CircularProgress } from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { stylesRowCenteredHorizontal } from 'common/styles';
import { ComponentProps } from 'react';
import { PageContainer } from './PageContainer';

export const PageContainerWithLoader = (props: ComponentProps<typeof PageContainer>) => {
    return (
        <PageContainer {...props}>
            <AbsolutelyCentered>
                <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center' }}>
                    <CircularProgress size={80} thickness={3} sx={{ animationDuration: '1500ms' }} />
                </Box>
            </AbsolutelyCentered>
        </PageContainer>
    );
};
