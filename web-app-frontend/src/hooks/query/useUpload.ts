import { useCallback, useState } from 'react';
import { upload, UploadRequest } from 'services/features/files/fileSlice';
import { selectFileUploadState, useAppDispatch, useAppSelector } from 'services/store';

export function useUpload<T>(props: Omit<UploadRequest<T>, 'file'>) {
    const dispatch = useAppDispatch();
    const [filename, setFilename] = useState<string>();
    const uploadState = useAppSelector((state) => selectFileUploadState(state, filename ?? ''));

    const typedUpload = useCallback(
        async (file: File) => {
            setFilename(file.name);
            await dispatch(
                upload({
                    ...props,
                    file,
                    onSuccess: props.onSuccess as (data: unknown) => void,
                })
            );
        },
        [dispatch, props]
    );

    return { upload: typedUpload, isUploading: uploadState && uploadState.isUploading };
}
