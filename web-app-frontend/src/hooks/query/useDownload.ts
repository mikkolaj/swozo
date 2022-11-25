import { FileDto, StorageAccessRequest } from 'api';
import { ApiError } from 'api/errors';
import _ from 'lodash';
import { DependencyList, useCallback, useMemo, useState } from 'react';
import {
    getFileHandler,
    receiveFinishDownload,
    receiveStartDownload,
} from 'services/features/files/fileSlice';
import { useAppDispatch } from 'services/store';

type Props = {
    fetcher: (file: FileDto) => Promise<StorageAccessRequest>;
    onError: (error: ApiError) => void;
    deps: DependencyList;
};

const DOWNLOAD_DEBOUNCE_MILLIS = 1000;

export const useDownload = ({ fetcher, onError, deps }: Props) => {
    const [isDownloading, setDownloading] = useState(true);
    const dispatch = useAppDispatch();

    const download = useCallback(
        async (file: FileDto) => {
            try {
                setDownloading(true);
                dispatch(receiveStartDownload({ file }));
                const storageAccessRequest = await fetcher(file).catch((err) => {
                    onError(err as ApiError);
                    return undefined;
                });

                if (storageAccessRequest !== undefined) {
                    const handler = getFileHandler(storageAccessRequest);
                    await handler.download(file.name, storageAccessRequest);
                }
            } finally {
                setDownloading(false);
                dispatch(receiveFinishDownload({ fileId: file.id }));
            }
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        deps
    );

    const throttledDownload = useMemo(() => {
        return _.debounce(download, DOWNLOAD_DEBOUNCE_MILLIS, { leading: true });
    }, [download]);

    return { download: throttledDownload, isDownloading };
};
