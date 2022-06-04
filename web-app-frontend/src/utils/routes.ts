export class PageRoutes {
    static readonly HOME = '/';
    static readonly LOGIN = '/login';
    static readonly COURSES = '/courses';
    static readonly COURSE = `${this.COURSES}/:courseId`;
    static readonly CREATE_COURSE = `${this.COURSES}/creator`;

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
