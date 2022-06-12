export class PageRoutes {
    static readonly HOME = '/';
    static readonly LOGIN = '/login';
    static readonly MY_COURSES = '/my-courses';
    static readonly COURSE = `${this.MY_COURSES}/:courseId`;
    static readonly CREATE_COURSE = `${this.MY_COURSES}/creator`;
    static readonly MY_MODULES = '/my-modules';
    static readonly CREATE_MODULE = `${this.MY_MODULES}/creator`;

    static buildCourseRoute(courseId: string | number): string {
        return this.replace(this.COURSE, { name: 'courseId', value: `${courseId}` });
    }

    static replace(route: string, ...placeholders: RoutePlaceholder[]): string {
        let result = route;

        placeholders.forEach(({ name, value }) => {
            result = result.replace(`:${name}`, value);
        });

        return result;
    }
}

type RoutePlaceholder = {
    name: string;
    value: string;
};
