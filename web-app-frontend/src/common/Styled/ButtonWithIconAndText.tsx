import { SvgIconComponent } from '@mui/icons-material';
import { Button } from '@mui/material';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';

type Props = ComponentProps<typeof Button> & {
    textI18n: string;
    Icon: SvgIconComponent;
    iconPosition?: 'left' | 'right';
};

export const ButtonWithIconAndText = ({ textI18n, Icon, iconPosition = 'right', color, ...props }: Props) => {
    const { t } = useTranslation();
    const StyledIcon = <Icon sx={{ transform: 'scale(0.9)', ml: 0.5, mt: -0.3 }} />;
    return (
        <Button color={color ?? 'primary'} {...props}>
            {iconPosition === 'left' && StyledIcon}
            {t(textI18n)}
            {iconPosition === 'right' && StyledIcon}
        </Button>
    );
};
