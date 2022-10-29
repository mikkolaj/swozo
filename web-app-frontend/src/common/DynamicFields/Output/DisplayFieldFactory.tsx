import DownloadIcon from '@mui/icons-material/Download';
import { Box } from '@mui/material';
import { DynamicFieldDto, FileDto, ParameterDescriptionTypeEnum } from 'api';
import { getApis } from 'api/initialize-apis';
import { ReadonlyField } from 'common/Input/Readonly/ReadonlyField';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { stylesRowCenteredVertical } from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { i18n } from 'i18next';
import { useState } from 'react';
import { getTranslated } from 'utils/util';

type FieldUtils = {
    onInteractionError: (error: unknown) => void;
};

type FieldProvider = (param: DynamicFieldDto, i18n: i18n, fieldUtils: FieldUtils) => JSX.Element;

type DisplayFieldFactory = Record<ParameterDescriptionTypeEnum, FieldProvider>;

export const displayFieldFactory = (): DisplayFieldFactory => {
    return {
        FILE: ({ value, parameterDescription }: DynamicFieldDto, i18n, { onInteractionError }) => {
            const [file] = useState<FileDto>(JSON.parse(value));
            const { download } = useDownload({
                fetcher: (file) =>
                    getApis().fileApi.getDownloadSignedAccessRequestExternal({ remoteFileId: file.id }),
                onError: onInteractionError,
                deps: [],
            });

            return (
                <Box sx={{ ...stylesRowCenteredVertical }}>
                    <ReadonlyField
                        value={file.name}
                        labelText={getTranslated(i18n, parameterDescription.translatedLabel)}
                    />
                    <ButtonWithIconAndText
                        sx={{ ml: 2, height: 56 }}
                        textI18n="dynamicDisplay.file.button"
                        Icon={DownloadIcon}
                        variant="contained"
                        iconPosition="right"
                        onClick={() => download(file)}
                    />
                </Box>
            );
        },
        TEXT: ({ value, parameterDescription }: DynamicFieldDto, i18n) => {
            return (
                <ReadonlyField
                    value={value}
                    labelText={getTranslated(i18n, parameterDescription.translatedLabel)}
                />
            );
        },
    };
};
