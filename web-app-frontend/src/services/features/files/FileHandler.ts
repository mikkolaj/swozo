import { StorageAccessRequest } from 'api';

export interface FileHandler {
    upload: (file: File, storageAccessRequest: StorageAccessRequest) => Promise<void>;
    download: (filename: string, storageAccessRequest: StorageAccessRequest) => Promise<void>;
}
