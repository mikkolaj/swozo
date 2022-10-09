import { StorageAccessRequest } from 'api';
import { ErrorType } from 'api/errors';
import { FileHandler } from './FileHandler';

export class GCloudFileHandler implements FileHandler {
    async upload(file: File, { signedUrl, httpMethod, httpHeaders }: StorageAccessRequest): Promise<void> {
        // TODO retries with exp backoff
        try {
            const resp = await fetch(signedUrl, {
                method: httpMethod,
                headers: httpHeaders,
                body: file,
            });

            if (!resp.ok) {
                throw {
                    errorType: ErrorType.THIRD_PARTY_ERROR,
                    message: await resp.text(),
                };
            }
        } catch (err) {
            console.error(err);

            throw {
                errorType: ErrorType.THIRD_PARTY_ERROR,
                message: 'unexpected upload error',
            };
        }
    }

    download(filename: string, { signedUrl }: StorageAccessRequest): Promise<void> {
        const link = document.createElement('a');
        link.href = signedUrl;
        link.download = filename;
        link.click();
        return Promise.resolve();
    }
}
