import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { Box, IconButton } from '@mui/material';

type Props = {
    isFavourite: boolean;
    onSetFavourite: () => void;
    onUnsetFavourite: () => void;
};

export const FavouriteToggle = ({ isFavourite, onSetFavourite, onUnsetFavourite }: Props) => {
    return (
        <Box>
            {isFavourite ? (
                <IconButton color="primary" onClick={() => onUnsetFavourite()}>
                    <FavoriteIcon />
                </IconButton>
            ) : (
                <IconButton color="primary" onClick={() => onSetFavourite()}>
                    <FavoriteBorderIcon />
                </IconButton>
            )}
        </Box>
    );
};
