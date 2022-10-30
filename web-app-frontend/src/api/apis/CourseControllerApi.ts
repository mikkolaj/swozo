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
    CourseDetailsDto,
    CourseDetailsDtoFromJSON,
    CourseDetailsDtoToJSON,
    CourseSummaryDto,
    CourseSummaryDtoFromJSON,
    CourseSummaryDtoToJSON,
    CreateActivityRequest,
    CreateActivityRequestFromJSON,
    CreateActivityRequestToJSON,
    CreateCourseRequest,
    CreateCourseRequestFromJSON,
    CreateCourseRequestToJSON,
    EditCourseRequest,
    EditCourseRequestFromJSON,
    EditCourseRequestToJSON,
    JoinCourseRequest,
    JoinCourseRequestFromJSON,
    JoinCourseRequestToJSON,
    ModifyParticipantRequest,
    ModifyParticipantRequestFromJSON,
    ModifyParticipantRequestToJSON,
} from '../models';

export interface AddCourseRequest {
    createCourseRequest: CreateCourseRequest;
}

export interface AddSingleActivityRequest {
    id: number;
    createActivityRequest: CreateActivityRequest;
}

export interface AddStudentToCourseRequest {
    courseId: number;
    modifyParticipantRequest: ModifyParticipantRequest;
}

export interface DeleteCourseRequest {
    id: number;
}

export interface EditCourseOperationRequest {
    id: number;
    editCourseRequest: EditCourseRequest;
}

export interface GetCourseRequest {
    id: number;
}

export interface GetPublicCourseDataRequest {
    uuid: string;
}

export interface GetPublicCoursesRequest {
    offset?: number;
    limit?: number;
}

export interface JoinCourseOperationRequest {
    joinCourseRequest: JoinCourseRequest;
}

export interface RemoveStudentFromCourseRequest {
    courseId: number;
    modifyParticipantRequest: ModifyParticipantRequest;
}

/**
 * 
 */
export class CourseControllerApi extends runtime.BaseAPI {

    /**
     */
    async addCourseRaw(requestParameters: AddCourseRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.createCourseRequest === null || requestParameters.createCourseRequest === undefined) {
            throw new runtime.RequiredError('createCourseRequest','Required parameter requestParameters.createCourseRequest was null or undefined when calling addCourse.');
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
            path: `/courses`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: CreateCourseRequestToJSON(requestParameters.createCourseRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async addCourse(requestParameters: AddCourseRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.addCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async addSingleActivityRaw(requestParameters: AddSingleActivityRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.id === null || requestParameters.id === undefined) {
            throw new runtime.RequiredError('id','Required parameter requestParameters.id was null or undefined when calling addSingleActivity.');
        }

        if (requestParameters.createActivityRequest === null || requestParameters.createActivityRequest === undefined) {
            throw new runtime.RequiredError('createActivityRequest','Required parameter requestParameters.createActivityRequest was null or undefined when calling addSingleActivity.');
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
            path: `/courses/{id}/activities`.replace(`{${"id"}}`, encodeURIComponent(String(requestParameters.id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: CreateActivityRequestToJSON(requestParameters.createActivityRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async addSingleActivity(requestParameters: AddSingleActivityRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.addSingleActivityRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async addStudentToCourseRaw(requestParameters: AddStudentToCourseRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.courseId === null || requestParameters.courseId === undefined) {
            throw new runtime.RequiredError('courseId','Required parameter requestParameters.courseId was null or undefined when calling addStudentToCourse.');
        }

        if (requestParameters.modifyParticipantRequest === null || requestParameters.modifyParticipantRequest === undefined) {
            throw new runtime.RequiredError('modifyParticipantRequest','Required parameter requestParameters.modifyParticipantRequest was null or undefined when calling addStudentToCourse.');
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
            path: `/courses/{courseId}/students`.replace(`{${"courseId"}}`, encodeURIComponent(String(requestParameters.courseId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: ModifyParticipantRequestToJSON(requestParameters.modifyParticipantRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async addStudentToCourse(requestParameters: AddStudentToCourseRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.addStudentToCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async deleteCourseRaw(requestParameters: DeleteCourseRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.id === null || requestParameters.id === undefined) {
            throw new runtime.RequiredError('id','Required parameter requestParameters.id was null or undefined when calling deleteCourse.');
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
            path: `/courses/{id}`.replace(`{${"id"}}`, encodeURIComponent(String(requestParameters.id))),
            method: 'DELETE',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.VoidApiResponse(response);
    }

    /**
     */
    async deleteCourse(requestParameters: DeleteCourseRequest, initOverrides?: RequestInit): Promise<void> {
        await this.deleteCourseRaw(requestParameters, initOverrides);
    }

    /**
     */
    async editCourseRaw(requestParameters: EditCourseOperationRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.id === null || requestParameters.id === undefined) {
            throw new runtime.RequiredError('id','Required parameter requestParameters.id was null or undefined when calling editCourse.');
        }

        if (requestParameters.editCourseRequest === null || requestParameters.editCourseRequest === undefined) {
            throw new runtime.RequiredError('editCourseRequest','Required parameter requestParameters.editCourseRequest was null or undefined when calling editCourse.');
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
            path: `/courses/{id}`.replace(`{${"id"}}`, encodeURIComponent(String(requestParameters.id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: EditCourseRequestToJSON(requestParameters.editCourseRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async editCourse(requestParameters: EditCourseOperationRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.editCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async getCourseRaw(requestParameters: GetCourseRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.id === null || requestParameters.id === undefined) {
            throw new runtime.RequiredError('id','Required parameter requestParameters.id was null or undefined when calling getCourse.');
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
            path: `/courses/{id}`.replace(`{${"id"}}`, encodeURIComponent(String(requestParameters.id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async getCourse(requestParameters: GetCourseRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.getCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async getPublicCourseDataRaw(requestParameters: GetPublicCourseDataRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseSummaryDto>> {
        if (requestParameters.uuid === null || requestParameters.uuid === undefined) {
            throw new runtime.RequiredError('uuid','Required parameter requestParameters.uuid was null or undefined when calling getPublicCourseData.');
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
            path: `/courses/summary/{uuid}`.replace(`{${"uuid"}}`, encodeURIComponent(String(requestParameters.uuid))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseSummaryDtoFromJSON(jsonValue));
    }

    /**
     */
    async getPublicCourseData(requestParameters: GetPublicCourseDataRequest, initOverrides?: RequestInit): Promise<CourseSummaryDto> {
        const response = await this.getPublicCourseDataRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async getPublicCoursesRaw(requestParameters: GetPublicCoursesRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<Array<CourseSummaryDto>>> {
        const queryParameters: any = {};

        if (requestParameters.offset !== undefined) {
            queryParameters['offset'] = requestParameters.offset;
        }

        if (requestParameters.limit !== undefined) {
            queryParameters['limit'] = requestParameters.limit;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/courses/summary`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(CourseSummaryDtoFromJSON));
    }

    /**
     */
    async getPublicCourses(requestParameters: GetPublicCoursesRequest = {}, initOverrides?: RequestInit): Promise<Array<CourseSummaryDto>> {
        const response = await this.getPublicCoursesRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async getUserCoursesRaw(initOverrides?: RequestInit): Promise<runtime.ApiResponse<Array<CourseDetailsDto>>> {
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
            path: `/courses`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(CourseDetailsDtoFromJSON));
    }

    /**
     */
    async getUserCourses(initOverrides?: RequestInit): Promise<Array<CourseDetailsDto>> {
        const response = await this.getUserCoursesRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async joinCourseRaw(requestParameters: JoinCourseOperationRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.joinCourseRequest === null || requestParameters.joinCourseRequest === undefined) {
            throw new runtime.RequiredError('joinCourseRequest','Required parameter requestParameters.joinCourseRequest was null or undefined when calling joinCourse.');
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
            path: `/courses/join`,
            method: 'PATCH',
            headers: headerParameters,
            query: queryParameters,
            body: JoinCourseRequestToJSON(requestParameters.joinCourseRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async joinCourse(requestParameters: JoinCourseOperationRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.joinCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async removeStudentFromCourseRaw(requestParameters: RemoveStudentFromCourseRequest, initOverrides?: RequestInit): Promise<runtime.ApiResponse<CourseDetailsDto>> {
        if (requestParameters.courseId === null || requestParameters.courseId === undefined) {
            throw new runtime.RequiredError('courseId','Required parameter requestParameters.courseId was null or undefined when calling removeStudentFromCourse.');
        }

        if (requestParameters.modifyParticipantRequest === null || requestParameters.modifyParticipantRequest === undefined) {
            throw new runtime.RequiredError('modifyParticipantRequest','Required parameter requestParameters.modifyParticipantRequest was null or undefined when calling removeStudentFromCourse.');
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
            path: `/courses/{courseId}/students`.replace(`{${"courseId"}}`, encodeURIComponent(String(requestParameters.courseId))),
            method: 'DELETE',
            headers: headerParameters,
            query: queryParameters,
            body: ModifyParticipantRequestToJSON(requestParameters.modifyParticipantRequest),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => CourseDetailsDtoFromJSON(jsonValue));
    }

    /**
     */
    async removeStudentFromCourse(requestParameters: RemoveStudentFromCourseRequest, initOverrides?: RequestInit): Promise<CourseDetailsDto> {
        const response = await this.removeStudentFromCourseRaw(requestParameters, initOverrides);
        return await response.value();
    }

}
