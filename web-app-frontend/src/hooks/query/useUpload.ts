import { DependencyList, useCallback, useEffect, useState } from 'react';
import { upload, UploadRequest } from 'services/features/files/fileSlice';
import { selectFileUploadState, useAppDispatch, useAppSelector } from 'services/store';

type Props<T> = Omit<UploadRequest<T>, 'file'> & {
    deps: DependencyList;
};

export function useUpload<T>({ deps, ...props }: Props<T>) {
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
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [...deps, dispatch]
    );

    useEffect(() => {
        console.log('recomputing');
    }, [typedUpload]);

    return { upload: typedUpload, isUploading: uploadState && uploadState.isUploading };
}
