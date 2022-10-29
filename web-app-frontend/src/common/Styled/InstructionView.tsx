import { Paper, SxProps, Theme } from '@mui/material';
import { InstructionDto } from 'api';
import { RichTextViewer } from './RichTextViewer';

type Props = {
    instruction: InstructionDto;
    wrapperSx?: SxProps<Theme>;
};

export const InstructionView = ({ instruction, wrapperSx }: Props) => {
    return (
        <Paper
            sx={{
                width: '100%',
                p: 2,
                ...wrapperSx,
            }}
        >
            <RichTextViewer untrustedPossiblyDangerousHtml={instruction.untrustedPossiblyDangerousHtml} />
        </Paper>
    );
};
