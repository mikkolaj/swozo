import { Button, Card, CardContent, Typography } from '@mui/material';
import { Container } from '@mui/system';
import { useTranslation } from 'react-i18next';
import { Activity } from 'utils/mocks';

type Props = {
    activity: Activity;
};

export const ActivityView: React.FC<Props> = ({ activity }: Props) => {
    const { t } = useTranslation();
    console.log(activity);
    return (
        <Card>
            <CardContent>
                <Typography component="h1" variant="h5" gutterBottom>
                    {activity.name}
                </Typography>
                <Container>
                    <Button>{t('course.activity.links')}</Button>
                    <Button>{t('course.activity.instructions')}</Button>
                </Container>
            </CardContent>
        </Card>
    );
};
