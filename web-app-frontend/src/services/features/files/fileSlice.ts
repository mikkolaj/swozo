import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { FileDto, InitFileUploadRequest, StorageAccessRequest, UploadAccessDto } from 'api';
import { ApiError } from 'api/errors';
import { AppDispatch, RootState } from 'services/store';
import { FileHandler } from './FileHandler';
import { GCloudFileHandler } from './GCloudFileHandler';

type FileStateKey = string;

type UploadState = {
    filename: string;
    uploadContext: string;
    isUploading: boolean;
    startTimestamp: number;
};

export type UploadRequest<T> = {
    file: File;
    uploadContext: string;
    preparator: (initFileUploadRequest: InitFileUploadRequest) => Promise<StorageAccessRequest>;
    acker: (uploadAccessDto: UploadAccessDto) => Promise<T>;
    onSuccess: (data: T) => void;
    onError: (error: ApiError) => void;
};

export type DownloadRequest = {
    file: FileDto;
    fetcher: () => Promise<StorageAccessRequest>;
};

export type FileHandlerState = Record<FileStateKey, UploadState>;

const buildInitialState = (): FileHandlerState => ({});

export const buildFileStateKey = (context: string, filename: string): FileStateKey =>
    `${context}/${filename}`;

export const getFileHandler = (_storageRequest: StorageAccessRequest): FileHandler => {
    // if (storageRequest.provider === StorageAccessRequestProviderEnum.Gcloud) {
    //     return new GCloudFileHandler();
    // }

    // console.error(`Unknown storage provider: ${storageRequest.provider}, using default handler`);
    return new GCloudFileHandler();
};

export const upload = createAsyncThunk<
    unknown,
    UploadRequest<unknown>,
    { dispatch: AppDispatch; state: RootState }
>(
    'files/upload',
    async ({ file, uploadContext, preparator, acker, onSuccess, onError }, { getState, dispatch }) => {
        if (getState().files[buildFileStateKey(uploadContext, file.name)]?.isUploading) return;

        dispatch(receiveStartUpload({ filename: file.name, uploadContext }));

        const initFileUploadRequest: InitFileUploadRequest = { filename: file.name, sizeBytes: file.size };

        try {
            const storageAccessRequest = await preparator(initFileUploadRequest);

            const fileHandler = getFileHandler(storageAccessRequest);

            await fileHandler.upload(file, storageAccessRequest);

            const result = await acker({ initFileUploadRequest, storageAccessRequest });

            onSuccess(result);
        } catch (err) {
            onError(err as ApiError);
        } finally {
            dispatch(receiveFinishUpload({ filename: file.name, uploadContext }));
        }
    }
);

export const fileSlice = createSlice({
    name: 'files',
    initialState: buildInitialState(),
    reducers: {
        receiveStartUpload: (
            state: FileHandlerState,
            action: PayloadAction<{ filename: string; uploadContext: string }>
        ) => {
            state[buildFileStateKey(action.payload.uploadContext, action.payload.filename)] = {
                filename: action.payload.filename,
                uploadContext: action.payload.uploadContext,
                isUploading: true,
                startTimestamp: new Date().getTime(),
            };
        },
        receiveFinishUpload: (
            state: FileHandlerState,
            action: PayloadAction<{ filename: string; uploadContext: string }>
        ) => {
            const key = buildFileStateKey(action.payload.filename, action.payload.uploadContext);
            state[key] = {
                ...state[key],
                isUploading: false,
            };
        },
    },
});

const { receiveStartUpload, receiveFinishUpload } = fileSlice.actions;

export default fileSlice.reducer;
