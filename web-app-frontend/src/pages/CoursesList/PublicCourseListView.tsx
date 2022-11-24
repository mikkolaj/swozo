import { Box, Container, Grid, Stack, Typography } from '@mui/material';
import { CourseSummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { SearchBar } from 'common/Styled/SearchBar';
import { stylesRowCenteredHorizontal, stylesRowCenteredVertical } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { CoursePublicView } from './components/CoursePublicView';
import { filterCourses } from './utils';

export const PublicCourseListView = () => {
    const { t } = useTranslation();
    const [searchPhrase, setSearchPhrase] = useState('');
    const [filteredCourses, setFilteredCourses] = useState<CourseSummaryDto[]>([]);
    const { isApiError, errorHandler, consumeErrorAction, pushApiError, isApiErrorSet, removeApiError } =
        useApiErrorHandling({});

    const { data: courseSummaryItems, isLoading } = useErrorHandledQuery(
        ['courses', 'public'],
        () => getApis().courseApi.getPublicCourses(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    useEffect(() => {
        if (courseSummaryItems) {
            setFilteredCourses(filterCourses(courseSummaryItems, searchPhrase));
        }
    }, [courseSummaryItems, searchPhrase]);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={6}>
                        <Box sx={{ ...stylesRowCenteredVertical }}>
                            <PageHeaderText text={t('publicCourses.header')} />
                            <SearchBar
                                sx={{ ml: 3 }}
                                placeholder={t('publicCourses.searchBoxPlaceholder')}
                                value={searchPhrase}
                                onChange={(e) => setSearchPhrase(e.target.value)}
                            />
                        </Box>
                    </Grid>
                </>
            }
        >
            <Container>
                {isLoading || (filteredCourses && filteredCourses.length > 0) ? (
                    <Stack spacing={2} px={2}>
                        {filteredCourses?.map((course) => (
                            <CoursePublicView key={course.joinUUID} courseSummary={course} />
                        ))}
                    </Stack>
                ) : (
                    <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mt: 8 }}>
                        <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h4">
                            {t('publicCourses.empty')}
                        </Typography>
                    </Box>
                )}
            </Container>
        </PageContainer>
    );
};
