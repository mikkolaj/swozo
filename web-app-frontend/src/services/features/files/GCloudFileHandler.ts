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

    download(filename: string, { signedUrl, httpHeaders, httpMethod }: StorageAccessRequest): Promise<void> {
        return fetch(signedUrl, { method: httpMethod, headers: httpHeaders })
            .catch((err) => {
                console.error(err);
                throw {
                    errorType: ErrorType.THIRD_PARTY_ERROR,
                };
            })
            .then((resp) => {
                if (resp.status < 200 || resp.status > 299) {
                    throw {
                        errorType: ErrorType.THIRD_PARTY_ERROR,
                    };
                }
                return resp.blob();
            })
            .then((fileBlob) => {
                const url = window.URL.createObjectURL(fileBlob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                a.style.display = 'none';
                document.body.appendChild(a);
                a.click();
                a.remove();
            });
    }
}
