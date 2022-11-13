import { Box, Checkbox, FormControlLabel } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { AutocompleteWithChips } from 'common/Input/AutocompleteWithChips';
import { stylesColumn, stylesRowCenteredVertical } from 'common/styles';
import { ActivityValues, SelectedModuleValue } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';

type Props = {
    slideIndependantName: string;
    selectBoxLabelI18n: string;
    availableModules: ServiceModuleSummaryDto[];
    labelI18n: string;
    value: ActivityValues;
    listExtractor: (v: ActivityValues) => SelectedModuleValue[];
    nameBuilder: (slideIndependantName: string) => string;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setFieldValue: (fieldName: string, value: any) => void;
};

export const ServiceModuleInput = ({
    slideIndependantName,
    availableModules,
    setFieldValue,
    listExtractor,
    nameBuilder,
    labelI18n,
    selectBoxLabelI18n,
    value,
}: Props) => {
    const { t } = useTranslation();
    return (
        <AutocompleteWithChips
            labelPath={labelI18n}
            name={nameBuilder(slideIndependantName)}
            chosenOptions={listExtractor(value)}
            options={availableModules.map<SelectedModuleValue>((serviceModule) => ({
                module: serviceModule,
                linkConfirmationRequired: false,
            }))}
            optionEquals={(m1, m2) => m1.module === m2.module}
            optionToString={({ module }) => module.name}
            setFieldValue={setFieldValue}
            required={false}
            customChipRenderer={(chips) => (
                <Box sx={{ ...stylesColumn }}>
                    {chips.map((chip, nestedIdx) => (
                        <Box sx={{ ...stylesRowCenteredVertical, mt: 1 }} key={nestedIdx}>
                            <Box sx={{ minWidth: '280px' }}>{chip}</Box>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={listExtractor(value)[nestedIdx].linkConfirmationRequired}
                                        value={listExtractor(value)[nestedIdx].linkConfirmationRequired}
                                    />
                                }
                                label={t(selectBoxLabelI18n)}
                                name={nameBuilder(
                                    `${slideIndependantName}.${nestedIdx}.linkConfirmationRequired`
                                )}
                                onChange={() =>
                                    setFieldValue(
                                        nameBuilder(
                                            `${slideIndependantName}.${nestedIdx}.linkConfirmationRequired`
                                        ),
                                        !listExtractor(value)[nestedIdx].linkConfirmationRequired
                                    )
                                }
                            />
                        </Box>
                    ))}
                </Box>
            )}
        />
    );
};
