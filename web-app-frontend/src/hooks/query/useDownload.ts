import { FileDto, StorageAccessRequest } from 'api';
import { ApiError } from 'api/errors';
import _ from 'lodash';
import { DependencyList, useCallback, useMemo, useState } from 'react';
import { getFileHandler } from 'services/features/files/fileSlice';

type Props = {
    fetcher: (file: FileDto) => Promise<StorageAccessRequest>;
    onError: (error: ApiError) => void;
    deps: DependencyList;
};

const DOWNLOAD_DEBOUNCE_MILLIS = 1000;

export const useDownload = ({ fetcher, onError, deps }: Props) => {
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
        // eslint-disable-next-line react-hooks/exhaustive-deps
        deps
    );

    const throttledDownload = useMemo(() => {
        return _.throttle(download, DOWNLOAD_DEBOUNCE_MILLIS);
    }, [download]);

    return { download: throttledDownload, isDownloading };
};
