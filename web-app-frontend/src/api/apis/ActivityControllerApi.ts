/* tslint:disable */
/* eslint-disable */
/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import {
    ActivityDetailsDto,
    ActivityDetailsDtoFromJSON,
    ActivityDetailsDtoToJSON,
    InitFileUploadRequest,
    InitFileUploadRequestFromJSON,
    InitFileUploadRequestToJSON,
    StorageAccessRequest,
    StorageAccessRequestFromJSON,
    StorageAccessRequestToJSON,
    UploadAccessDto,
    UploadAccessDtoFromJSON,
    UploadAccessDtoToJSON,
} from '../models';

export interface AckPublicActivityFileUploadRequest {
    activityId: number;
    uploadAccessDto: UploadAccessDto;
}

export interface GetPublicActivityFileDownloadRequestRequest {
    activityId: number;
    fileId: number;
}

export interface PreparePublicActivityFileUploadRequest {
    activityId: number;
    initFileUploadRequest: InitFileUploadRequest;
}

/**
 * 
 */
export class ActivityControllerApi extends runtime.BaseAPI {

    /**
     */
    async ackPublicActivityFileUploadRaw(requestParameters: AckPublicActivityFileUploadRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<ActivityDetailsDto>> {
        if (requestParameters.activityId === null || requestParameters.activityId === undefined) {
            throw new runtime.RequiredError('activityId','Required parameter requestParameters.activityId was null or undefined when calling ackPublicActivityFileUpload.');
        }

        if (requestParameters.uploadAccessDto === null || requestParameters.uploadAccessDto === undefined) {
            throw new runtime.RequiredError('uploadAccessDto','Required parameter requestParameters.uploadAccessDto was null or undefined when calling ackPublicActivityFileUpload.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/activities/{activityId}/files`.replace(`{${"activityId"}}`, encodeURIComponent(String(requestParameters.activityId))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: UploadAccessDtoToJSON(requestParameters.uploadAccessDto),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => ActivityDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async ackPublicActivityFileUpload(requestParameters: AckPublicActivityFileUploadRequest, initOverrides?: RequestInit): Promise<ActivityDetailsDto> {
        const response = await this.ackPublicActivityFileUploadRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async getPublicActivityFileDownloadRequestRaw(requestParameters: GetPublicActivityFileDownloadRequestRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<StorageAccessRequest>> {
        if (requestParameters.activityId === null || requestParameters.activityId === undefined) {
            throw new runtime.RequiredError('activityId','Required parameter requestParameters.activityId was null or undefined when calling getPublicActivityFileDownloadRequest.');
        }

        if (requestParameters.fileId === null || requestParameters.fileId === undefined) {
            throw new runtime.RequiredError('fileId','Required parameter requestParameters.fileId was null or undefined when calling getPublicActivityFileDownloadRequest.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/activities/{activityId}/files/{fileId}`.replace(`{${"activityId"}}`, encodeURIComponent(String(requestParameters.activityId))).replace(`{${"fileId"}}`, encodeURIComponent(String(requestParameters.fileId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => StorageAccessRequestFromJSON(jsonValue));
    }

    /**
     */
    async getPublicActivityFileDownloadRequest(requestParameters: GetPublicActivityFileDownloadRequestRequest, initOverrides?: RequestInit): Promise<StorageAccessRequest> {
        const response = await this.getPublicActivityFileDownloadRequestRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async preparePublicActivityFileUploadRaw(requestParameters: PreparePublicActivityFileUploadRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<StorageAccessRequest>> {
        if (requestParameters.activityId === null || requestParameters.activityId === undefined) {
            throw new runtime.RequiredError('activityId','Required parameter requestParameters.activityId was null or undefined when calling preparePublicActivityFileUpload.');
        }

        if (requestParameters.initFileUploadRequest === null || requestParameters.initFileUploadRequest === undefined) {
            throw new runtime.RequiredError('initFileUploadRequest','Required parameter requestParameters.initFileUploadRequest was null or undefined when calling preparePublicActivityFileUpload.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/activities/{activityId}/files`.replace(`{${"activityId"}}`, encodeURIComponent(String(requestParameters.activityId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: InitFileUploadRequestToJSON(requestParameters.initFileUploadRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => StorageAccessRequestFromJSON(jsonValue));
    }

    /**
     */
    async preparePublicActivityFileUpload(requestParameters: PreparePublicActivityFileUploadRequest, initOverrides?: RequestInit): Promise<StorageAccessRequest> {
        const response = await this.preparePublicActivityFileUploadRaw(requestParameters, initOverrides);
        return await response.value();
    }

}