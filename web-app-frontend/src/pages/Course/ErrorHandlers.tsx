import { CourseDetailsDto } from 'api';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { useTranslation } from 'react-i18next';
import { PageRoutes } from 'utils/routes';

export const AlreadyJoinedError = ({ courseDetails }: { courseDetails: CourseDetailsDto }) => {
    const { t } = useTranslation();
    return (
        <PageContainerWithError
            navigateTo={PageRoutes.Course(courseDetails.id)}
            navButtonMessage={t('course.join.error.goToCourse')}
            errorMessage={t('course.join.error.alreadyParticipated', {
                name: courseDetails.name,
            })}
        />
    );
};

export const InvalidJoinUUidError = () => {
    const { t } = useTranslation();
    return <PageContainerWithError errorMessage={t('course.join.error.invalidJoinUrl')} />;
};
