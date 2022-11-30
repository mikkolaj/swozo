import { CreateVmRequest } from 'api';

export const initialVmValues = (): CreateVmRequest => ({
    name: '',
    bandwidthMbps: 1024,
    ramGB: 8,
    vcpu: 4,
    imageDiskSizeGB: 10,
    descriptionHtml: '',
});
