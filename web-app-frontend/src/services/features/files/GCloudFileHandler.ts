import { StorageAccessRequest } from 'api';
import { ErrorType } from 'api/errors';
import { withExponentialBackoff } from 'utils/util';
import { FileHandler } from './FileHandler';

const MAX_UPLOAD_RETRIES = 3;
const MAX_UPLOAD_RETRYING_TIME = 3000;

export class GCloudFileHandler implements FileHandler {
    async upload(file: File, { signedUrl, httpMethod, httpHeaders }: StorageAccessRequest): Promise<void> {
        try {
            const resp = await withExponentialBackoff(
                () =>
                    fetch(signedUrl, {
                        method: httpMethod,
                        headers: httpHeaders,
                        body: file,
                    }),
                MAX_UPLOAD_RETRIES,
                MAX_UPLOAD_RETRYING_TIME
            );

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
        // TODO: consider using fetch api to have more control
        const link = document.createElement('a');
        link.href = signedUrl;
        link.download = filename;
        link.click();
        return Promise.resolve();
    }
}
