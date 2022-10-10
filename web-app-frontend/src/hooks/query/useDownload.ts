import { FileDto, StorageAccessRequest } from 'api';
import { ApiError } from 'api/errors';
import _ from 'lodash';
import { useCallback, useState } from 'react';
import { getFileHandler } from 'services/features/files/fileSlice';

type Props = {
    fetcher: (file: FileDto) => Promise<StorageAccessRequest>;
    onError: (error: ApiError) => void;
};

const DOWNLOAD_DEBOUNCE_MILLIS = 500;

export const useDownload = ({ fetcher, onError }: Props) => {
    const [isDownloading, setDownloading] = useState(true);

    const download = useCallback(
        async (file: FileDto) => {
            setDownloading(true);
            try {
                const storageAccessRequest = await fetcher(file).catch((err) => {
                    onError(err as ApiError);
                    return undefined;
                });

                if (storageAccessRequest !== undefined) {
                    const handler = getFileHandler(storageAccessRequest);
                    handler.download(file.name, storageAccessRequest);
                }
            } finally {
                setDownloading(false);
            }
        },
        [fetcher, onError]
    );

    return { download: _.debounce(download, DOWNLOAD_DEBOUNCE_MILLIS), isDownloading };
};
