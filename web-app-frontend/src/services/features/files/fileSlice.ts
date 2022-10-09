import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { FileDto, InitFileUploadRequest, StorageAccessRequest, UploadAccessDto } from 'api';
import { ApiError } from 'api/errors';
import { AppDispatch, RootState } from 'services/store';
import { FileHandler } from './FileHandler';
import { GCloudFileHandler } from './GCloudFileHandler';

type Filename = string;

type UploadState = {
    filename: Filename;
    isUploading: boolean;
    startTimestamp: number;
};

export type UploadRequest<T> = {
    file: File;
    preparator: (initFileUploadRequest: InitFileUploadRequest) => Promise<StorageAccessRequest>;
    acker: (uploadAccessDto: UploadAccessDto) => Promise<T>;
    onSuccess: (data: T) => void;
    onError: (error: ApiError) => void;
};

export type DownloadRequest = {
    file: FileDto;
    fetcher: () => Promise<StorageAccessRequest>;
};

export type FileHandlerState = Record<Filename, UploadState>;

const buildInitialState = (): FileHandlerState => ({});

const getFileHandler = (_storageRequest: StorageAccessRequest): FileHandler => {
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
>('files/upload', async ({ file, preparator, acker, onSuccess, onError }, { getState, dispatch }) => {
    if (getState().files[file.name]?.isUploading) return;

    dispatch(receiveStartUpload(file.name));
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
        dispatch(receiveFinishUpload(file.name));
    }
});

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const download = createAsyncThunk<unknown, DownloadRequest, any>(
    'files/download',
    async ({ file, fetcher }) => {
        const storageAccessRequest = await fetcher();
        const fileHandler = getFileHandler(storageAccessRequest);
        await fileHandler.download(file.name, storageAccessRequest);
    }
);

export const fileSlice = createSlice({
    name: 'files',
    initialState: buildInitialState(),
    reducers: {
        receiveStartUpload: (state: FileHandlerState, action: PayloadAction<Filename>) => {
            state[action.payload] = {
                filename: action.payload,
                isUploading: true,
                startTimestamp: new Date().getTime(),
            };
        },
        receiveFinishUpload: (state: FileHandlerState, action: PayloadAction<Filename>) => {
            state[action.payload] = {
                ...state[action.payload],
                isUploading: false,
            };
        },
    },
});

const { receiveStartUpload, receiveFinishUpload } = fileSlice.actions;

export default fileSlice.reducer;
